package ru.practicum;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.stats.proto.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.proto.UserActionProto;

import java.time.Instant;

@Component
public class CollectorClient {
    @GrpcClient("collector")
    private static UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void sendUserAction(long userId, long eventId, ActionTypeProto actionType) {
        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(getTimestamp())
                .build();

        client.collectUserAction(request);
    }

    private Timestamp getTimestamp() {
        return Timestamp.newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .setNanos(Instant.now().getNano())
                .build();
    }
}