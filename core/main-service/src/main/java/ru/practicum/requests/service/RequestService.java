package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDtoOut;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDtoOut> findByUserId(Long userId);

    ParticipationRequestDtoOut create(Long userId, Long eventId);

    ParticipationRequestDtoOut cancel(Long userId, Long requestId);
}
