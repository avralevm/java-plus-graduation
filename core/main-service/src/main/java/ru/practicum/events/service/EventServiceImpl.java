package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.client.RequestFeignClient;
import ru.practicum.client.UserAdminFeignClient;
import ru.practicum.event.in.EventRequestStatusUpdateRequest;
import ru.practicum.event.in.NewEventDto;
import ru.practicum.event.in.UpdateEventAdminRequest;
import ru.practicum.event.in.UpdateEventUserRequest;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.event.output.SwitchRequestsStatus;
import ru.practicum.event.state.State;
import ru.practicum.event.state.StateActionForAdmin;
import ru.practicum.event.state.StateActionForUser;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.*;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exceptions.DateException;
import ru.practicum.exceptions.NoHavePermissionException;
import ru.practicum.request.output.EventRequestCountDto;
import ru.practicum.request.output.ParticipationRequestDtoOut;
import ru.practicum.request.Status;
import ru.practicum.user.output.UserDto;
import ru.practicum.user.output.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.constants.Methods.copyFields;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    private final UserAdminFeignClient userAdminFeignClient;
    private final StatClientService statClientService;
    private final RequestFeignClient requestFeignClient;

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
        Event newEvent = eventMapper.toEvent(request, category, event.getInitiatorId());
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
        Event savedEvent = eventRepository.save(event);
        UserDto userDto = userAdminFeignClient.getById(savedEvent.getInitiatorId());

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setInitiator(toUserShortDto(userDto));
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Event with id " + eventId + " has not been published");
        }
        return mapToFullDto(List.of(event)).getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvenFullById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Event with id " + eventId + " has not been published");
        }
        return mapToFullDto(List.of(event)).getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public EventShortDto getEventShortById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Event with id " + eventId + " has not been published");
        }
        return mapToShortDto(List.of(event)).getFirst();
    }

    @Override
    public List<EventShortDto> getEventByIds(List<Long> eventIds) {
        List<Event> events = eventRepository.findAllById(eventIds);
        return mapToShortDto(events);
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
    public SwitchRequestsStatus switchRequestsStatus(EventRequestStatusUpdateRequest updateRequest, Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " " + "not found"));
        if (!event.getInitiatorId().equals(userId)) {
            throw new NoHavePermissionException("You do not have permission to update this event");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            List<ParticipationRequestDtoOut> requests = requestFeignClient.getByIds(updateRequest.getRequestIds());
            return new SwitchRequestsStatus(requests, List.of());
        }

        EventFullDto eventFullDto = mapToFullDto(List.of(event)).getFirst();

        if (updateRequest.getStatus() == Status.CONFIRMED) {
            int freeLimit = (int) Math.min(eventFullDto.getParticipantLimit() - eventFullDto.getConfirmedRequests(),
                    updateRequest.getRequestIds().size());
            if (freeLimit <= 0) {
                throw new ConflictException("The participant limit has been reached");
            }

            List<Long> confirmedIds = updateRequest.getRequestIds().subList(0, freeLimit);
            List<Long> rejectedIds = updateRequest.getRequestIds().subList(freeLimit, updateRequest.getRequestIds().size());

            List<ParticipationRequestDtoOut> confirmed = requestFeignClient.setStatusForAllByIds(confirmedIds, Status.CONFIRMED);

            List<ParticipationRequestDtoOut> rejected =
                    requestFeignClient.setStatusForAllByIds(rejectedIds, Status.REJECTED);

            return new SwitchRequestsStatus(confirmed, rejected);
        } else {
            List<ParticipationRequestDtoOut> rejected =
                    requestFeignClient.setStatusForAllByIds(updateRequest.getRequestIds(), Status.REJECTED);
            return new SwitchRequestsStatus(List.of(), rejected);
        }
    }

    @Override
    public List<ParticipationRequestDtoOut> getRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " " + "not found"));
        if (!event.getInitiatorId().equals(userId)) {
            throw new NoHavePermissionException("You do not have permission to update this event");
        }

        return requestFeignClient.getByEventId(eventId);
    }

    @Override
    public EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiatorId().equals(userId)) {
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

        UserDto user = userAdminFeignClient.getById(userId);
        Event newEvent = eventMapper.toEvent(updateEventUserRequest, category, user.getId());

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

        if (!event.getInitiatorId().equals(userId)) {
            throw new NoHavePermissionException("No allowed to access this event");
        }
        return mapToFullDto(List.of(event)).getFirst();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        dateValidation(newEventDto.getEventDate(), 2);

        UserDto user = userAdminFeignClient.getById(userId);

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("category with id " + newEventDto.getCategory() + " not found"));

        Event event = eventMapper.toEvent(newEventDto, category, user.getId());
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

        List<Long> initiatorIds = events.stream()
                .map(Event::getInitiatorId)
                .distinct()
                .toList();
        Map<Long, UserDto> initiators = getUsers(initiatorIds);

        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventMapper::toEventFullDto)
                .toList();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            EventFullDto dto = eventFullDtos.get(i);

            // Устанавливаем количество подтверждённых и просмотры
            dto.setConfirmedRequests(confirmedRequests.getOrDefault(dto.getId(), 0L));
            dto.setViews(views.getOrDefault(dto.getId(), 0L));

            // Устанавливаем инициатора
            UserDto userDto = initiators.get(event.getInitiatorId());
            if (userDto != null) {
                dto.setInitiator(toUserShortDto(userDto));
            }

            // Дополнительная логика для requestModeration
            if (!dto.getRequestModeration() || dto.getParticipantLimit() == 0) {
                dto.setConfirmedRequests(dto.getConfirmedRequests() +
                        rejectedRequests.getOrDefault(dto.getId(), 0L));
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

        List<Long> initiatorIds = events.stream()
                .map(Event::getInitiatorId)
                .distinct()
                .toList();
        Map<Long, UserDto> initiators = getUsers(initiatorIds);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::toEventShortDto).toList();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            EventShortDto dto = eventShortDtos.get(i);

            dto.setConfirmedRequests(confirmedRequests.getOrDefault(dto.getId(), 0L));
            dto.setViews(views.getOrDefault(dto.getId(), 0L));

            UserDto userDto = initiators.get(event.getInitiatorId());
            if (userDto != null) {
                dto.setInitiator(toUserShortDto(userDto));
            }
        }
        return eventShortDtos;
    }

    private UserShortDto toUserShortDto(UserDto userDto) {
        return UserShortDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();
    }

    private Map<Long, UserDto> getUsers(List<Long> userIds) {
        return userAdminFeignClient.getByIds(userIds).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }

    private Map<Long, Long> getRequests(List<Long> events, Status status) {
        return requestFeignClient.getRequestCountsByEventIds(events, status).stream()
                .collect(Collectors.toMap(EventRequestCountDto::getEventId, EventRequestCountDto::getCount));
    }
}