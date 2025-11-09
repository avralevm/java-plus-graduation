package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.output.ParticipationRequestDtoOut;
import ru.practicum.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "requesterId", target = "requester")
    ParticipationRequestDtoOut toRequestDto(Request request);
}