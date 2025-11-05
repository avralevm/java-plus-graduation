package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.Status;
import ru.practicum.request.output.EventRequestCountDto;
import ru.practicum.request.output.ParticipationRequestDtoOut;

import java.util.List;

@FeignClient(name = "request-service", path = "internal/api/requests", configuration = FeignConfig.class)
public interface RequestFeignClient {
    @GetMapping("/by-ids")
    List<ParticipationRequestDtoOut> getByIds(@RequestParam List<Long> ids);

    @GetMapping("/event/{eventId}")
    List<ParticipationRequestDtoOut> getByEventId(@PathVariable Long eventId);

    @GetMapping("/counts")
    List<EventRequestCountDto> getRequestCountsByEventIds(@RequestParam List<Long> ids, @RequestParam Status status);

    @PostMapping("/status")
    List<ParticipationRequestDtoOut> setStatusForAllByIds(@RequestParam List<Long> ids, @RequestParam Status status);
}