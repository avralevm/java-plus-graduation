package ru.practicum.events.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.event.EventFeignClient;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.events.service.EventService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/api/events")
public class EventClientController implements EventFeignClient {
    private final EventService eventService;

    @GetMapping("/{eventId}/full")
    public EventFullDto getEventFullById(@PathVariable Long eventId) {
        return eventService.getEvenFullById(eventId);
    }

    @GetMapping("/{eventId}/short")
    public EventShortDto getEventShortById(@PathVariable Long eventId) {
        return eventService.getEventShortById(eventId);
    }

    @GetMapping("/by-ids")
    public List<EventShortDto> getEventByIds(@RequestParam @UniqueElements List<Long> ids) {
        return eventService.getEventByIds(ids);
    }

    @GetMapping("/check")
    public boolean checkExistsEventByCategoryId(@RequestParam Long id) {
        return eventService.checkExistsEventByCategoryId(id);
    }
}