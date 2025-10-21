package ru.practicum.events.service;

import ru.practicum.events.dto.in.EventRequestStatusUpdateRequest;
import ru.practicum.events.dto.in.NewEventDto;
import ru.practicum.events.dto.in.UpdateEventAdminRequest;
import ru.practicum.events.dto.in.UpdateEventUserRequest;
import ru.practicum.events.dto.output.EventFullDto;
import ru.practicum.events.dto.output.EventShortDto;
import ru.practicum.events.dto.output.SwitchRequestsStatus;
import ru.practicum.events.model.EventAdminParam;
import ru.practicum.events.model.EventPublicParam;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;

import java.util.List;

public interface EventService {

    EventFullDto updateEvent(UpdateEventAdminRequest request, Long eventId);

    List<EventFullDto> findEvents(EventAdminParam param);

    EventFullDto getEvent(Long eventId);

    List<EventShortDto> findEvents(EventPublicParam param);

    SwitchRequestsStatus switchRequestsStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long eventId, Long userId);

    List<ParticipationRequestDtoOut> getRequests(Long userId, Long eventId);

    EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, Long eventId, Long userId);

    EventFullDto getEvent(Long eventId, Long userId);

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer to);
}