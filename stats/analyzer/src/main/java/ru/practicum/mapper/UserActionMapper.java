package ru.practicum.mapper;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.ActionType;
import ru.practicum.model.UserAction;

public class UserActionMapper {
    public UserAction toUserAction(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .actionType(setActionType(userActionAvro.getActionType()))
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .timestamp(userActionAvro.getTimestamp())
                .build();
    }

    private ActionType setActionType(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case VIEW -> ActionType.VIEW;
            case REGISTER -> ActionType.REGISTER;
            case LIKE -> ActionType.LIKE;
        };
    }
}