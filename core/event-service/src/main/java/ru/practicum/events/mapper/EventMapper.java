package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.in.NewEventDto;
import ru.practicum.event.in.UpdateEventAdminRequest;
import ru.practicum.event.in.UpdateEventUserRequest;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.events.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "initiatorId", source = "initiatorId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(NewEventDto newEventDto, Long categoryId, Long initiatorId);

    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "initiatorId", source = "initiatorId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventUserRequest updateEventUserRequest, Long category, Long initiatorId);

    @Mapping(target = "initiatorId", source = "initiatorId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventAdminRequest updateEventAdminRequest, Long category, Long initiatorId);
}