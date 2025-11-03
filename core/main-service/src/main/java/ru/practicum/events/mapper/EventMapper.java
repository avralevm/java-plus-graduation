package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.model.Category;
import ru.practicum.events.dto.in.NewEventDto;
import ru.practicum.events.dto.in.UpdateEventAdminRequest;
import ru.practicum.events.dto.in.UpdateEventUserRequest;
import ru.practicum.events.dto.output.EventFullDto;
import ru.practicum.events.dto.output.EventShortDto;
import ru.practicum.events.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "initiatorId", source = "initiatorId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(NewEventDto newEventDto, Category category, Long initiatorId);

    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "initiatorId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventUserRequest updateEventUserRequest, Category category, Long initiatorId);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "initiatorId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventAdminRequest updateEventAdminRequest, Category category, Long initiatorId);
}