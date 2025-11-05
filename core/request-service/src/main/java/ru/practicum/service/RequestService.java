package ru.practicum.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.request.Status;
import ru.practicum.request.output.EventRequestCountDto;
import ru.practicum.request.output.ParticipationRequestDtoOut;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDtoOut> findByUserId(Long userId);

    List<ParticipationRequestDtoOut> findByIds(List<Long> ids);

    List<ParticipationRequestDtoOut> findByEventId(Long eventId);

    List<EventRequestCountDto> getRequestCountsByEventIds(List<Long> ids, Status status);

    ParticipationRequestDtoOut create(Long userId, Long eventId);

    ParticipationRequestDtoOut cancel(Long userId, Long requestId);

    List<ParticipationRequestDtoOut> setStatusForAllByIds(List<Long> ids, Status status);
}