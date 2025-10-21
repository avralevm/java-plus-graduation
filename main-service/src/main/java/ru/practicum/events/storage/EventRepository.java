package ru.practicum.events.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>, EventQueryDslRepository {
    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.initiator.id = :initiatorId " +
            "ORDER BY e.createdOn")
    List<Event> findAllByInitiatorId(@Param("initiatorId") Long initiatorId, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.initiator.id = :initiatorId " +
            "ORDER BY e.createdOn")
    List<Event> findAllByInitiatorId(@Param("initiatorId") Long initiatorId);

    @Query("SELECT e.id, e.initiator " +
            "FROM Event AS e " +
            "WHERE e.id IN :eventsIds " +
            "GROUP BY e.id")
    List<Object[]> getUsersByEventIds(@Param("eventsIds") List<Long> eventIds);

    boolean existsByCategoryId(Long id);
}
