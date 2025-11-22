package ru.practicum.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.List;

public interface AggregationService {
    void sendEventSimilarities(List<EventSimilarityAvro> eventSimilarities);
}
