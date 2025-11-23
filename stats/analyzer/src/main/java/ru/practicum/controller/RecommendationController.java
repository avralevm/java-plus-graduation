package ru.practicum.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.service.recommendation.RecommendationService;
import ru.yandex.practicum.grpc.stats.collector.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.proto.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.proto.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.proto.UserPredictionsRequestProto;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RecommendationController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationService service;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Get Recommendations for user: {}", request);
            service.getRecommendationsForUser(request).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in qGetRecommendationsForUser", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Get SimilarEvents for: {}", request);
            service.getSimilarEvents(request).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in getSimilarEvents", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Get Interactions count: {}", request);
            service.getInteractionsCount(request).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in getInteractionsCount", e);
            responseObserver.onError(e);
        }
    }
}