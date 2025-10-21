package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatClient;
import ru.practicum.dto.output.GetStatisticDto;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class StatClientService {
    private final StatClient statClient;

    public Map<Long, Long> getEventsView(List<Event> events) {
        //eventId, views
        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .toList();

        if (publishedEvents.isEmpty()) {
            return views;
        }

        Optional<LocalDateTime> minPublishedOn = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minPublishedOn.isEmpty()) {
            return views;
        }

        LocalDateTime start = minPublishedOn.get();


        List<String> uri = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .toList();

        List<GetStatisticDto> stats = statClient.getStats(start, LocalDateTime.now(), uri, true);

        stats.forEach(statDto -> {
            String[] parts = statDto.getUri().split("/");
            if (parts.length >= 3) {
                Long eventId = Long.parseLong(parts[2]);
                views.put(eventId, statDto.getHits());
            }
        });
        return views;
    }
}
