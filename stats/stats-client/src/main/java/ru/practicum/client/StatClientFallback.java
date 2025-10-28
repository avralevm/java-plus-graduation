package ru.practicum.client;

import org.springframework.stereotype.Component;
import ru.practicum.client.exception.StatisticClientException;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatClientFallback implements StatClient {
    @Override
    public void hit(StatisticDto statisticDto) {
        throw new StatisticClientException("Error sending statistics: " +  statisticDto);
    }

    @Override
    public List<GetStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        throw new StatisticClientException("Error sending statistics param: " + uris);
    }
}
