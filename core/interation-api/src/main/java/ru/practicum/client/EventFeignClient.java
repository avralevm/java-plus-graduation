package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.output.EventFullDto;

@FeignClient(name = "main-service", path = "internal/api/events", configuration = FeignConfig.class)
public interface EventFeignClient {
    @GetMapping("/{eventId}")
    EventFullDto getEventById(@PathVariable Long eventId);
}