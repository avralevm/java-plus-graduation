package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.events.dto.in.EventRequestStatusUpdateRequest;
import ru.practicum.events.dto.in.NewEventDto;
import ru.practicum.events.dto.in.UpdateEventAdminRequest;
import ru.practicum.events.dto.in.UpdateEventUserRequest;
import ru.practicum.events.dto.output.EventFullDto;
import ru.practicum.events.dto.output.EventShortDto;
import ru.practicum.events.dto.output.SwitchRequestsStatus;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.*;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DateException;
import ru.practicum.exceptions.NoHavePermissionException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.storage.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.constants.Methods.copyFields;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final StatClientService statClientService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Transactional
    @Override
    public EventFullDto updateEvent(UpdateEventAdminRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        Category category;
        if (request.getCategory() == null) {
            category = event.getCategory();
        } else {
            category = categoryRepository.findById(request.getCategory()).orElseThrow(() -> new NotFoundException("Category not found"));
        }
        Event newEvent = eventMapper.toEvent(request, category, event.getInitiator());
        copyFields(event, newEvent);

        if (request.getStateAction() == null) {
            return eventMapper.toEventFullDto(eventRepository.save(event));
        } else if (request.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            if (LocalDateTime.now().isAfter(event.getEventDate().minusHours(1).minusSeconds(5))) {
                throw new DateException("Date must be before one hour before the event start");
            }
            if (event.getState() != State.PENDING) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: "
                        + event.getState().name());
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (request.getStateAction().equals(StateActionForAdmin.REJECT_EVENT)) {
            if (event.getState() == State.PUBLISHED) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: "
                        + event.getState().name());
            }
            event.setState(State.CANCELED);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Event with id " + eventId + " has not been published");
        }

        return mapToFullDto(List.of(event)).getFirst();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> findEvents(EventAdminParam param) {
        if (param.getSize() == 0) {
            List<Event> events = eventRepository.findEventsByParam(param, param.getFrom());
            return mapToFullDto(events);
        }
        if (param.getFrom() > param.getSize()) {
            return List.of();
        }
        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        List<Event> events = eventRepository.findEventsByParam(param, pageRequest);
        return mapToFullDto(events);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> findEvents(EventPublicParam param) {
        List<Event> events;
        if (param.getSize() == 0) {
            events = eventRepository.findEventsByParam(param, param.getFrom());
        } else if (param.getFrom() < param.getSize()) {
            PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
            events = eventRepository.findEventsByParam(param, pageRequest);
        } else {
            return List.of();
        }

        if (param.getRangeStart() != null && param.getRangeEnd() != null
                && param.getRangeStart().isAfter(param.getRangeEnd())) {
            throw new IllegalArgumentException("DateStart cannot be later than the dateEnd");
        }

        List<EventShortDto> eventShortDtos = mapToShortDto(events);
        List<EventShortDto> mutableEvents = new ArrayList<>(eventShortDtos);

        if (param.getSort() != null) {
            switch (param.getSort()) {
                case "EVENT_DATE":
                    mutableEvents.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;
                case "VIEWS":
                    mutableEvents.sort(Comparator.comparing(EventShortDto::getViews).reversed());
                    break;
                default:
                    break;
            }
        }
        return mutableEvents;
    }

    @Transactional
    @Override
    public SwitchRequestsStatus switchRequestsStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " " + "not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NoHavePermissionException("You do not have permission to update this event");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return new SwitchRequestsStatus(requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds()).stream()
                    .map(requestMapper::toParticipationRequestDtoOut)
                    .toList(),
                    List.of());
        }

        EventFullDto eventFullDto = mapToFullDto(List.of(event)).getFirst();


        if (eventRequestStatusUpdateRequest.getStatus() == Status.CONFIRMED) {
            int freeLimit = (int) Math.min(eventFullDto.getParticipantLimit() - eventFullDto.getConfirmedRequests(), eventRequestStatusUpdateRequest.getRequestIds().size());
            if (freeLimit <= 0) {
                throw new ConflictException("The participant limit has been reached");
            }

            List<Long> confirmedIds = eventRequestStatusUpdateRequest.getRequestIds().subList(0,
                    freeLimit);
            List<Long> rejectedIds = eventRequestStatusUpdateRequest.getRequestIds().subList(freeLimit,
                    eventRequestStatusUpdateRequest.getRequestIds().size());

            List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

            List<ParticipationRequestDtoOut> rejected = requests.stream()
                    .filter(obj -> rejectedIds.contains(obj.getId()))
                    .peek(obj -> {
                        if (obj.getStatus() == Status.CONFIRMED) {
                            throw new ConflictException("Request with id " + obj.getId() + " has been confirmed. It cannot be rejected ");
                        }
                    })
                    .map(requestMapper::toParticipationRequestDtoOut)
                    .peek(obj -> obj.setStatus(Status.REJECTED))
                    .toList();

            List<ParticipationRequestDtoOut> confirmed = requests.stream()
                    .filter(obj -> confirmedIds.contains(obj.getId()))
                    .peek(obj -> {
                        if (obj.getStatus() == Status.REJECTED) {
                            throw new ConflictException("Request with id " + obj.getId() + " has been confirmed. It cannot be rejected ");
                        }
                    })
                    .map(requestMapper::toParticipationRequestDtoOut)
                    .peek(obj -> obj.setStatus(Status.CONFIRMED))
                    .toList();

            requestRepository.setStatusForAllByIdIn(rejectedIds, Status.REJECTED);
            requestRepository.setStatusForAllByIdIn(confirmedIds, Status.CONFIRMED);

            return new SwitchRequestsStatus(confirmed, rejected);
        } else {
            List<ParticipationRequestDtoOut> rejected = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds())
                    .stream()
                    .peek(obj -> {
                        if (obj.getStatus() == Status.CONFIRMED) {
                            throw new ConflictException("Request with id " + obj.getId() + " has been confirmed. It cannot be rejected ");
                        }
                    })
                    .map(requestMapper::toParticipationRequestDtoOut)
                    .peek(obj -> obj.setStatus(Status.REJECTED))
                    .toList();
            requestRepository.setStatusForAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds(), Status.REJECTED);
            return new SwitchRequestsStatus(List.of(), rejected);
        }
    }

    @Override
    public List<ParticipationRequestDtoOut> getRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " " + "not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NoHavePermissionException("You do not have permission to update this event");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(requestMapper::toParticipationRequestDtoOut)
                .toList();
    }

    @Override
    public EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NoHavePermissionException("You do not have permission to update this event");
        }
        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (updateEventUserRequest.getEventDate() != null) {
            dateValidation(updateEventUserRequest.getEventDate(), 2);
        }
        Category category;
        if (updateEventUserRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
        } else {
            category = event.getCategory();
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event newEvent = eventMapper.toEvent(updateEventUserRequest, category, user);

        copyFields(event, newEvent);
        if (updateEventUserRequest.getStateAction() == StateActionForUser.SEND_TO_REVIEW) {
            event.setState(State.PENDING);
        } else if (updateEventUserRequest.getStateAction() == StateActionForUser.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        }
        event = eventRepository.save(event);
        return mapToFullDto(List.of(event)).getFirst();
    }

    @Override
    public EventFullDto getEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NoHavePermissionException("No allowed to access this event");
        }
        return mapToFullDto(List.of(event)).getFirst();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        dateValidation(newEventDto.getEventDate(), 2);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " not found"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("category with id " + newEventDto.getCategory() + " not found"));

        Event event = eventMapper.toEvent(newEventDto, category, user);
        if (event.getState() == null) {
            event.setState(State.PENDING);
        }
        event = eventRepository.save(event);
        return mapToFullDto(List.of(event)).getFirst();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer to) {
        List<Event> events;
        if (to == 0) {
            events = eventRepository.findAllByInitiatorId(userId).stream()
                    .skip(from)
                    .toList();
        } else if (from < to && to > 0) {
            PageRequest pageRequest = PageRequest.of(from / to, to);
            events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        } else {
            return List.of();
        }


        Map<Long, Long> views = Map.of();

        if (events.isEmpty()) {
            return List.of();
        }
        try {
            views = statClientService.getEventsView(events);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        List<EventShortDto> shortDtos = events
                .stream()
                .map(eventMapper::toEventShortDto)
                .toList();

        if (!views.isEmpty()) {
            for (EventShortDto eventShortDto : shortDtos) {
                eventShortDto.setViews(views.get(eventShortDto.getId()));
            }
        } else {
            shortDtos.forEach(eventShortDto -> eventShortDto.setViews(0L));
        }
        return shortDtos;
    }

    private void dateValidation(LocalDateTime date, int hours) {
        if (!date.isAfter(LocalDateTime.now().plusHours(hours).minusSeconds(5))) {
            throw new DateException("The date must be " + hours + " hours after now. Value: " + date);
        }
    }

    private List<EventFullDto> mapToFullDto(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> confirmedRequests = getRequests(ids, Status.CONFIRMED);
        Map<Long, Long> rejectedRequests = getRequests(ids, Status.REJECTED);
        Map<Long, Long> views = statClientService.getEventsView(events);

        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventMapper::toEventFullDto).toList();

        for (EventFullDto eventFullDto : eventFullDtos) {
            eventFullDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventFullDto.getId(), 0L));

            eventFullDto.setViews(views.getOrDefault(eventFullDto.getId(), 0L));

            if (!eventFullDto.getRequestModeration() || eventFullDto.getParticipantLimit() == 0) {
                eventFullDto.setConfirmedRequests(eventFullDto.getConfirmedRequests() +
                        rejectedRequests.getOrDefault(eventFullDto.getId(), 0L));
            }

        }

        return eventFullDtos;
    }

    private List<EventShortDto> mapToShortDto(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> confirmedRequests = getRequests(ids, Status.CONFIRMED);
        Map<Long, Long> rejectedRequests = getRequests(ids, Status.REJECTED);
        Map<Long, Long> views = statClientService.getEventsView(events);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::toEventShortDto).toList();
        for (EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
            eventShortDto.setViews(views.getOrDefault(eventShortDto.getId(), 0L));
        }

        return eventShortDtos;
    }

    private Map<Long, Long> getRequests(List<Long> events, Status status) {
        return requestRepository.countAllByEventIdInAndStatus(events, status).stream()
                .collect(Collectors.toMap(eventId -> (Long) eventId[0],
                        requestCount -> (Long) requestCount[1]));
    }

    private Map<Long, User> getUsersByEventIds(List<Long> eventIds) {
        return eventRepository.getUsersByEventIds(eventIds).stream()
                .collect(Collectors.toMap(eventId -> (Long) eventId[0],
                        user -> (User) user[1]));
    }
}
