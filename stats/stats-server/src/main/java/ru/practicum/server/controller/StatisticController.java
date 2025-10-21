package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;
import ru.practicum.server.service.StatisticService;

import java.util.List;


@RequiredArgsConstructor
@RestController
@Slf4j
public class StatisticController {

    private final StatisticService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody StatisticDto statisticDto) {
        log.info("POST /hit - Adding hit: {}", statisticDto);
        service.addHit(statisticDto);
    }

    @GetMapping("/stats")
    public List<GetStatisticDto> getStatistic(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false, defaultValue = "") List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique
    ) {
        log.info("GET /stats - Getting statistic for uris: {} from: {} to {}, unique: {}", uris, start, end, unique);
        return service.getStatistic(start, end, uris, unique);
    }
}