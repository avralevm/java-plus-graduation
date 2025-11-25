package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {
    private final Producer<String, EventSimilarityAvro> producer;

    @Value("${kafka.topics.events-similarity}")
    private String topic;

    @Override
    public void sendEventSimilarities(List<EventSimilarityAvro> eventSimilarities) {
        for (EventSimilarityAvro eventSimilarity : eventSimilarities) {
            ProducerRecord<String, EventSimilarityAvro> record = createdRecord(eventSimilarity);
            log.info("Отправляем record: {} \n", record);
            producer.send(record);
        }

        producer.flush();
        log.info("Отправили весь record \n");
    }

    private ProducerRecord<String, EventSimilarityAvro> createdRecord(EventSimilarityAvro eventSimilarity) {
        return new ProducerRecord<>(
                topic,
                eventSimilarity);
    }
}