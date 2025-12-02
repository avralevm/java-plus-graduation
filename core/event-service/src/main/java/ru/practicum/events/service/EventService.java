package ru.practicum.events.service;

import ru.practicum.event.in.EventRequestStatusUpdateRequest;
import ru.practicum.event.in.NewEventDto;
import ru.practicum.event.in.UpdateEventAdminRequest;
import ru.practicum.event.in.UpdateEventUserRequest;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.event.output.SwitchRequestsStatus;
import ru.practicum.events.model.EventAdminParam;
import ru.practicum.events.model.EventPublicParam;
import ru.practicum.request.output.ParticipationRequestDtoOut;

import java.util.List;

public interface EventService {
    EventFullDto updateEvent(UpdateEventAdminRequest request, Long eventId);

    List<EventFullDto> findEvents(EventAdminParam param);

    EventFullDto getEventById(Long eventId, Long userId);

    EventFullDto getEvenFullById(Long eventId);

    EventShortDto getEventShortById(Long eventId);

    List<EventShortDto> getEventByIds(List<Long> eventIds);

    List<EventShortDto> findEvents(EventPublicParam param);

    SwitchRequestsStatus switchRequestsStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long eventId, Long userId);

    List<ParticipationRequestDtoOut> getRequests(Long userId, Long eventId);

    EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, Long eventId, Long userId);

    EventFullDto getEvent(Long eventId, Long userId);

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer to);

    List<EventShortDto> getRecommendationEvents(Long userId);

    void likeEvent(Long eventId, Long userId);

    boolean checkExistsEventByCategoryId(Long id);
}