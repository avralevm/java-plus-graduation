package ru.practicum.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

@Component
public class EventSimilarityMapper {
    public EventSimilarity toEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .score(eventSimilarityAvro.getScore())
                .eventA(eventSimilarityAvro.getEventA())
                .eventB(eventSimilarityAvro.getEventB())
                .timestamp(eventSimilarityAvro.getTimestamp())
                .build();
    }
}