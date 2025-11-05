package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.EventFeignClient;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.events.service.EventService;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/api/events")
public class EventClientController implements EventFeignClient {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }
}