package ru.practicum.server.service;

import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;

import java.util.List;

public interface StatisticService {
    void addHit(StatisticDto statisticDto);

    List<GetStatisticDto> getStatistic(String start, String end, List<String> uris, Boolean unique);
}
