package ru.practicum.service.recommendation;

import ru.yandex.practicum.grpc.stats.proto.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.proto.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.proto.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationService {
    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);
}