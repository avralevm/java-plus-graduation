package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.UserAction;

import java.util.List;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    UserAction findByUserIdAndEventId(Long userId, Long eventId);

    List<UserAction> findAllByEventIdIn(Set<Long> ids);

    List<UserAction> findAllByUserId(Long userId);

    List<UserAction> findAllByUserId(Long userId, PageRequest pageRequest);
}