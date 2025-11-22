package ru.practicum.service.userAction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.model.ActionType;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionRepository repository;
    private final UserActionMapper mapper;

    @Override
    public void saveUserAction(UserActionAvro newUserAction) {
        log.info("Сохранение в бд действие: {}", newUserAction);
        UserAction existing = repository.findByUserIdAndEventId(newUserAction.getUserId(), newUserAction.getEventId());

        UserAction incoming = mapper.toUserAction(newUserAction);

        if (existing == null) {
            repository.save(incoming);
            return;
        }

        Double oldWeight = convertTypeActionToWeight(existing.getActionType());
        Double newWeight = convertTypeActionToWeight(incoming.getActionType());

        if (newWeight >= oldWeight) {
            existing.setActionType(incoming.getActionType());
            existing.setTimestamp(incoming.getTimestamp());
            repository.save(existing);
            log.info("Сохранили в бд действие с большим весом: {}", existing);
        }

        log.debug("В бд не сохранилось новое действие, вес не достаточен: oldWeight - {}, newWeight - {}", oldWeight, newWeight );
    }

    private Double convertTypeActionToWeight(ActionType actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}