package ru.practicum.mapper;


import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

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