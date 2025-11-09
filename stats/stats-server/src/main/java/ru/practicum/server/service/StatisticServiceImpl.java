package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;
import ru.practicum.server.mapper.StatisticMapper;
import ru.practicum.server.model.Statistic;
import ru.practicum.server.storage.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository repository;
    private final StatisticMapper mapper;

    @Override
    public void addHit(StatisticDto statisticDto) {
        Statistic statisticToSave = mapper.toStatistic(statisticDto);
        repository.save(statisticToSave);
        log.info("Hit with uri {} was saved", statisticDto.getUri());
    }

    @Override
    public List<GetStatisticDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<GetStatisticDto> statistics;

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (uris.isEmpty()) {
            statistics = repository.findHitsByTimestampBetween(start, end, unique);
            log.info("Retrieved {} statistics for all uris from {} to {}, unique IPs: {}", statistics.size(), start, end, unique);
        } else {
            statistics = repository.findHitsByUriInAndTimestampBetween(uris, start, end, unique);
            log.info("Retrieved {} statistics for uris: {} from {} to {}, unique IPs: {}", statistics.size(), uris, start, end, unique);
        }

        return statistics;
    }
}