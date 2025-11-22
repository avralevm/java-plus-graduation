package ru.practicum.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.model.ActionType;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;
import ru.yandex.practicum.grpc.stats.proto.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.proto.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.proto.UserPredictionsRequestProto;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserActionRepository userActionRepository;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        PageRequest pageRequest = PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<UserAction> interactions = userActionRepository.findAllByUserId(userId, pageRequest);

        if (interactions.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> recentEvents = interactions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Long> allUserEvents = interactions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> similarities = new ArrayList<>();
        for (Long eventId : recentEvents) {
            similarities.addAll(eventSimilarityRepository.findAllByEventAOrEventB(eventId, eventId));
        }

        return similarities.stream()
                .filter(sim -> !(allUserEvents.contains(sim.getEventA()) && allUserEvents.contains(sim.getEventB())))
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(maxResults)
                .map(sim -> {
                    Long recommendedEventId = allUserEvents.contains(sim.getEventA()) ? sim.getEventB() : sim.getEventA();
                    return createRecommendedEvent(recommendedEventId, sim.getScore());
                })
                .toList();
    }


    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long userId = request.getUserId();
        Long eventId = request.getEventId();
        int limit = request.getMaxResults();

        Set<Long> interacted = userActionRepository.findAllByUserId(userId).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> similar = eventSimilarityRepository.findAllByEventAOrEventB(eventId, eventId);

        return similar.stream()
                .filter(sim -> !interacted.contains(sim.getEventA()) || !interacted.contains(sim.getEventB()))
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(limit)
                .map(sim -> {
                    Long recommendedEventId = sim.getEventA().equals(eventId) ? sim.getEventB() : sim.getEventA();
                    return createRecommendedEvent(recommendedEventId, sim.getScore());
                })
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Set<Long> eventIds = new HashSet<>(request.getEventIdList());

        Map<Long, Double> eventScore = userActionRepository.findAllByEventIdIn(eventIds).stream()
                .collect(Collectors.groupingBy(UserAction::getEventId,
                        Collectors.summingDouble(action -> convertTypeActionToWeight(action.getActionType()))));

        return eventScore.entrySet().stream()
                .map(el -> createRecommendedEvent(el.getKey(), el.getValue()))
                .toList();
    }

    private RecommendedEventProto createRecommendedEvent(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score)
                .build();
    }

    private Double convertTypeActionToWeight(ActionType actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}