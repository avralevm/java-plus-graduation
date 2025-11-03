package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.UserAdminFeignClient;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ConflictException;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.storage.RequestRepository;
import ru.practicum.user.output.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserAdminFeignClient userAdminFeignClient;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDtoOut> findByUserId(Long userId) {
        log.info("Fetching requests for user with id: {}", userId);

        userAdminFeignClient.getById(userId);

        List<ParticipationRequestDtoOut> requests = requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDtoOut)
                .toList();

        log.info("Found {} requests for user with id: {}", requests.size(), userId);
        return requests;
    }


    @Override
    public ParticipationRequestDtoOut create(Long userId, Long eventId) {
        log.info("Creating request for user with id: {} and event with id: {}", userId, eventId);

        UserDto requester = userAdminFeignClient.getById(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Event with id={} not found", eventId);
            return new NotFoundException("Event with id=" + eventId + " was not found");
        });

        validateRequestCreation(requester, event);

        Request request = new Request();
        request.setEvent(event);
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());
        request.setStatus(event.getParticipantLimit() == 0 ? Status.CONFIRMED : Status.PENDING);

        if (!event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }

        Request savedRequest = requestRepository.save(request);
        log.info("Request created with id: {}", savedRequest.getId());
        return requestMapper.toParticipationRequestDtoOut(savedRequest);
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
        return requestMapper.toParticipationRequestDtoOut(updatedRequest);
    }

    private void validateRequestCreation(UserDto requester, Event event) {
        if (requestRepository.existsByRequesterIdAndEventId(requester.getId(), event.getId())) {
            log.warn("Request already exists for user with id={} and event with id={}", requester.getId(), event.getId());
            throw new ConflictException(String.format(
                    "Request already exists for user with id=%d and event with id=%d", requester.getId(), event.getId()));
        }

        if (event.getInitiatorId().equals(requester.getId())) {
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
