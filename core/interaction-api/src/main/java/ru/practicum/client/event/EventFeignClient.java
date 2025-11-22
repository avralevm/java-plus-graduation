package ru.practicum.client.event;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.FeignConfig;
import ru.practicum.event.output.EventFullDto;
import ru.practicum.event.output.EventShortDto;

import java.util.List;

@FeignClient(name = "event-service",
        path = "internal/api/events",
        configuration = FeignConfig.class,
        fallback = EventFeignClientFallback.class)
public interface EventFeignClient {
    @GetMapping("/{eventId}/full")
    EventFullDto getEventFullById(@PathVariable Long eventId);

    @GetMapping("/{eventId}/short")
    EventShortDto getEventShortById(@PathVariable Long eventId);

    @GetMapping("/by-ids")
    List<EventShortDto> getEventByIds(@RequestParam @UniqueElements List<Long> ids);
}