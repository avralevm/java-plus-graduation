package ru.practicum.config;

import deserializer.EventSimilarityDeserializer;
import deserializer.UserActionDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    @Value("${kafka.properties.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.properties.user-action.group-id}")
    private String userActionGroupId;

    @Value("${kafka.consumer.properties.event-similarity.group-id}")
    private String eventSimilarityGroupId;

    @Bean(destroyMethod = "close")
    public Consumer<String, EventSimilarityAvro> eventSimilarityConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, eventSimilarityGroupId);
        return new KafkaConsumer<>(configProps);
    }

    @Bean(destroyMethod = "close")
    public Consumer<String, UserActionAvro> userActionConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, userActionGroupId);
        return new KafkaConsumer<>(configProps);
    }
}
