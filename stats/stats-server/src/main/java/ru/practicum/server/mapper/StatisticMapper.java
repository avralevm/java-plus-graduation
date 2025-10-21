package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.server.model.Statistic;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    Statistic toStatistic(StatisticDto statisticDto);
}
