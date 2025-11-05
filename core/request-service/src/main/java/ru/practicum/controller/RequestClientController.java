package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.RequestFeignClient;
import ru.practicum.request.Status;
import ru.practicum.request.output.EventRequestCountDto;
import ru.practicum.request.output.ParticipationRequestDtoOut;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("internal/api/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestClientController implements RequestFeignClient {
    private final RequestService requestService;

    @GetMapping("/by-ids")
    public List<ParticipationRequestDtoOut> getByIds(@RequestParam List<Long> ids) {
        return requestService.findByIds(ids);
    }

    @GetMapping("/event/{eventId}")
    public List<ParticipationRequestDtoOut> getByEventId(@PathVariable Long eventId) {
        return requestService.findByEventId(eventId);
    }

    @GetMapping("/counts")
    public List<EventRequestCountDto> getRequestCountsByEventIds(@RequestParam List<Long> ids, @RequestParam Status status) {
        return requestService.getRequestCountsByEventIds(ids, status);
    }

    @PostMapping("/status")
    public List<ParticipationRequestDtoOut> setStatusForAllByIds(@RequestParam List<Long> ids, @RequestParam Status status) {
        return requestService.setStatusForAllByIds(ids, status);
    }
}