package ru.practicum.kafka;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface KafkaService {
    void sendUserAction(UserActionAvro userAction);
}
