package ru.practicum.service.userAction;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionService {
    void saveUserAction(UserActionAvro userActionAvro);
}