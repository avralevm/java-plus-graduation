package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorServiceImpl implements CollectorService {
    private final Producer<String, SpecificRecordBase> producer;

    @Value("collector.kafka.producer.topics.user-actions")
    private String topic;

    @Override
    public void sendUserAction(UserActionAvro userAction) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                String.valueOf(userAction.getUserId()),
                userAction);

        log.info("Отправляем record: {} \n", record);
        producer.send(record);
        producer.flush();

        log.info("Отправили record \n");
    }
}