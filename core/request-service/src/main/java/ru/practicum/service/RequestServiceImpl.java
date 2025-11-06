package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.EventFeignClient;
import ru.practicum.client.UserAdminFeignClient;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.event.state.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.output.EventRequestCountDto;
import ru.practicum.request.output.ParticipationRequestDtoOut;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.request.Status;
import ru.practicum.storage.RequestRepository;
import ru.practicum.user.output.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventFeignClient eventFeignClient;
    private final UserAdminFeignClient userAdminFeignClient;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDtoOut> findByUserId(Long userId) {
        log.info("Fetching requests for user with id: {}", userId);

        userAdminFeignClient.getById(userId);

        List<ParticipationRequestDtoOut> requests = requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toRequestDto)
                .toList();

        log.info("Found {} requests for user with id: {}", requests.size(), userId);
        return requests;
    }

    @Override
    public List<ParticipationRequestDtoOut> findByIds(List<Long> ids) {
        return requestRepository.findAllById(ids).stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Override
    public List<ParticipationRequestDtoOut> findByEventId(Long eventId) {
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Override
    public List<EventRequestCountDto> getRequestCountsByEventIds(List<Long> ids, Status status) {
        return requestRepository.countAllByEventIdInAndStatus(ids, status);
    }

    @Override
    public List<ParticipationRequestDtoOut> setStatusForAllByIds(List<Long> ids, Status status) {
        List<Request> requests = requestRepository.findAllById(ids);

        for (Request r : requests) {
            if (status == Status.REJECTED && r.getStatus() == Status.CONFIRMED) {
                throw new ConflictException("Request with id " + r.getId() + " is already confirmed and cannot be rejected");
            }
            if (status == Status.CONFIRMED && r.getStatus() == Status.REJECTED) {
                throw new ConflictException("Request with id " + r.getId() + " is already rejected and cannot be confirmed");
            }
            r.setStatus(status);
        }

        List<Request> response = requestRepository.saveAll(requests);
        return response.stream().map(requestMapper::toRequestDto).toList();
    }

    @Override
    public ParticipationRequestDtoOut create(Long userId, Long eventId) {
        log.info("Creating request for user with id: {} and event with id: {}", userId, eventId);

        UserDto requester = userAdminFeignClient.getById(userId);

        EventFullDto event = eventFeignClient.getEventFullById(eventId);

        validateRequestCreation(requester, event);

        Request request = new Request();
        request.setEventId(eventId);
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());
        request.setStatus(event.getParticipantLimit() == 0 ? Status.CONFIRMED : Status.PENDING);

        if (!event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }

        Request savedRequest = requestRepository.save(request);
        log.info("Request created with id: {}", savedRequest.getId());
        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    public ParticipationRequestDtoOut cancel(Long userId, Long requestId) {
        log.info("Cancelling request with id: {} for user with id: {}", requestId, userId);

        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("Request with id={} not found", requestId);
            return new NotFoundException(String.format("Request with id=%d was not found", requestId));
        });

        if (!request.getRequesterId().equals(userId)) {
            log.warn("User with id={} cannot cancel non-his request with id={}", userId, requestId);
            throw new ConflictException(String.format(
                    "User with id=%d cannot cancel non-his request with id=%d", userId, requestId));
        }

        request.setStatus(Status.CANCELED);
        Request updatedRequest = requestRepository.save(request);

        log.info("Request with id: {} cancelled successfully", requestId);
        return requestMapper.toRequestDto(updatedRequest);
    }

    private void validateRequestCreation(UserDto requester, EventFullDto event) {
        if (requestRepository.existsByRequesterIdAndEventId(requester.getId(), event.getId())) {
            log.warn("Request already exists for user with id={} and event with id={}", requester.getId(), event.getId());
            throw new ConflictException(String.format(
                    "Request already exists for user with id=%d and event with id=%d", requester.getId(), event.getId()));
        }

        if (event.getInitiator().getId().equals(requester.getId())) {
            log.warn("Event initiator with id={} cannot create a request for his event with id={}", requester.getId(), event.getId());
            throw new ConflictException(String.format(
                    "Event initiator with id=%d cannot create a request for his event with id=%d", requester.getId(), event.getId()));
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("Cannot participate in unpublished event with id={}", event.getId());
            throw new ConflictException(String.format("Cannot participate in unpublished event with id=%d", event.getId()));
        }

        if (event.getParticipantLimit() > 0 &&
                requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED) >= event.getParticipantLimit()) {
            log.warn("Participant limit reached for event with id={}", event.getId());
            throw new ConflictException(String.format("Participant limit reached for event with id=%d", event.getId()));
        }

    }
}
