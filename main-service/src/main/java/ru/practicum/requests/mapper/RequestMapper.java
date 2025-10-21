package ru.practicum.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;
import ru.practicum.requests.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDtoOut toParticipationRequestDtoOut(Request request);
}
