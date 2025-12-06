package ru.practicum.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.in.UpdateEventAdminRequest;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.events.model.EventAdminParam;
import ru.practicum.event.state.State;
import ru.practicum.events.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<State> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(0) Integer size) {

        EventAdminParam param = EventAdminParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .start(rangeStart)
                .end(rangeEnd)
                .from(from)
                .size(size)
                .build();

        return eventService.findEvents(param);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto editEvent(@Valid @RequestBody UpdateEventAdminRequest request, @PathVariable Long eventId) {
        return eventService.updateEvent(request, eventId);
    }
}
