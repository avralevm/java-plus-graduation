package ru.practicum.client.request;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.request.Status;
import ru.practicum.request.output.EventRequestCountDto;
import ru.practicum.request.output.ParticipationRequestDtoOut;

import java.util.List;

@Component
public class RequestFeignClientFallback implements RequestFeignClient {
    @Override
    public List<ParticipationRequestDtoOut> getByIds(List<Long> ids)  {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: RequestService не доступен");
    }

    @Override
    public List<ParticipationRequestDtoOut> getByEventId(Long eventId) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: RequestService не доступен");
    }

    @Override
    public List<EventRequestCountDto> getRequestCountsByEventIds(List<Long> ids, Status status) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: RequestService не доступен");
    }

    @Override
    public List<ParticipationRequestDtoOut> setStatusForAllByIds(List<Long> ids, Status status) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: RequestService не доступен");
    }
}