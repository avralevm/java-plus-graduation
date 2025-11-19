package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.AggregationService;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityHandler {
    private final Map<Long, Map<Long, Double>> userWeights = new HashMap<>();
    private final Map<Long, Double> eventWeightSums = new HashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightsSum = new HashMap<>();

    private final AggregationService aggregationService;

    public void handle(UserActionAvro userAction) {
        Long userId = userAction.getUserId();
        Long eventId = userAction.getEventId();
        Double newWeight = convertTypeActionToWeight(userAction.getActionType());
        Instant timestamp = userAction.getTimestamp();

        Map<Long, Double> userWeight = userWeights.computeIfAbsent(eventId, k -> new HashMap<>());
        Double currentWeight = userWeight.get(userId);

        if (currentWeight != null && currentWeight >= newWeight) {
            log.debug("Вес не увеличился, пропускаем пересчет. Текущий: {}, Новый: {}", currentWeight, newWeight);
            return;

        }

        userWeight.put(userId, newWeight);
        updateEventWeightSum(eventId);
        List<EventSimilarityAvro> similarities = updateMinWeightsAndCalculateSimilarities(eventId, userId, newWeight, currentWeight, timestamp);

        if (!similarities.isEmpty()) {
            aggregationService.sendEventSimilarities(similarities);
        }
    }

    private List<EventSimilarityAvro> updateMinWeightsAndCalculateSimilarities(Long eventId, Long userId, Double newWeight,
                                                               Double oldWeight, Instant timestamp) {
        List<EventSimilarityAvro> eventSimilarities = new ArrayList<>();

        for (Long userEventId : userWeights.keySet()) {
            if (userEventId.equals(eventId)) {
                continue;
            }
            Long minId = Math.min(userEventId, eventId);
            Long maxId = Math.max(userEventId, eventId);
            Map<Long, Double> usersWeight = userWeights.get(userEventId);

            if (!usersWeight.containsKey(userId)) {
                continue;
            }

            Double deltaMin = calculateDelta(usersWeight.get(userId), oldWeight, newWeight);

            if (deltaMin != 0) {
                minWeightsSum.computeIfAbsent(minId, k -> new HashMap<>()).merge(maxId, deltaMin, Double::sum);
            }

            Double score = calculateSimilarity(minId, maxId);

            EventSimilarityAvro EventsSimilarityAvro = createEventSimilarity(minId, maxId, score, timestamp);
            eventSimilarities.add(EventsSimilarityAvro);
            log.debug("Avro-сообщение: {}", EventsSimilarityAvro);
        }

        return eventSimilarities;
    }

    private EventSimilarityAvro createEventSimilarity(Long minId, Long maxId, Double score, Instant timestamp) {
        return EventSimilarityAvro.newBuilder()
                .setEventA(minId)
                .setEventB(maxId)
                .setScore(score)
                .setTimestamp(timestamp)
                .build();
    }

    private Double convertTypeActionToWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }

    private Double calculateSimilarity(Long minId, Long maxId) {
        Double sumMin = minWeightsSum.get(minId).get(maxId);
        Double sumA = eventWeightSums.get(minId);
        Double sumB = eventWeightSums.get(maxId);

        if (sumMin == null || sumA == null || sumB == null) {
            log.debug("Неполные данные для расчета сходства пары ({}, {})", minId, maxId);
            return null;
        }

        if (sumA == 0 || sumB == 0) {
            return 0.0;
        }

        Double similarity = sumMin / Math.sqrt(sumA * sumB);
        log.debug("Рассчитано сходство для пары ({}, {}): sumMin={}, sumA={}, sumB={}, similarity={}",
                minId, maxId, sumMin, sumA, sumB, similarity);
        return similarity;
    }

    private Double calculateDelta(Double userWeight, Double oldWeight, Double newWeight) {
        double oldW = (oldWeight != null) ? oldWeight : 0.0;
        Double oldMin = Math.min(oldW, userWeight);
        Double newMin = Math.min(newWeight, userWeight);
        return newMin - oldMin;
    }

    private void updateEventWeightSum(Long eventId) {
        Double sumWeight = userWeights.get(eventId).values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        eventWeightSums.put(eventId, sumWeight);
        log.debug("Обновлена сумма весов для мероприятия {}: {}", eventId, sumWeight);
    }
}