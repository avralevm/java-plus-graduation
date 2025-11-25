package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.CollectorService;
import ru.yandex.practicum.grpc.stats.proto.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.proto.UserActionProto;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionHandler {
    private final CollectorService kafkaService;

    public void handle(UserActionProto userAction) {
        UserActionAvro request = UserActionAvro.newBuilder()
                .setUserId(userAction.getUserId())
                .setEventId(userAction.getEventId())
                .setActionType(setActionType(userAction.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(userAction.getTimestamp().getSeconds(), userAction.getTimestamp().getNanos()))
                .build();

        log.info("Преобразовали Proto -> Avro: {}", request);
        kafkaService.sendUserAction(request);
    }

    private ActionTypeAvro setActionType(ActionTypeProto actionType) {
            return switch (actionType) {
                case ACTION_VIEW -> ActionTypeAvro.VIEW;
                case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
                case ACTION_LIKE -> ActionTypeAvro.LIKE;
                default -> throw new IllegalArgumentException("Unrecognized action type: " + actionType);
            };
    }
}