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
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface EventMapper {

    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "id", ignore = true)
    Event toEvent(NewEventDto newEventDto, Category category, User user);

    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventUserRequest updateEventUserRequest, Category category, User user);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventAdminRequest updateEventAdminRequest, Category category, User user);
}
