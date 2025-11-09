package ru.practicum.client.event;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.event.output.EventShortDto;

import java.util.List;

@Component
public class EventFeignClientFallback implements EventFeignClient {
    @Override
    public EventFullDto getEventFullById(Long eventId) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: EventService не доступен");
    }

    @Override
    public EventShortDto getEventShortById(Long eventId) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: EventService не доступен");
    }

    @Override
    public List<EventShortDto> getEventByIds(List<Long> ids) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: EventService не доступен");
    }
}