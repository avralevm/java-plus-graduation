package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;
import ru.practicum.request.output.EventRequestCountDto;
import ru.practicum.request.Status;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT new ru.practicum.request.output.EventRequestCountDto(r.eventId, COUNT(r)) " +
            "FROM Request r " +
            "WHERE r.eventId IN :eventIds AND r.status = :status " +
            "GROUP BY r.eventId")
    List<EventRequestCountDto> countAllByEventIdInAndStatus(@Param("eventIds") List<Long> eventIds, @Param("status") Status status);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findByRequesterId(Long requesterId);

    long countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findAllByEventId(Long eventId);

    boolean existsByRequesterIdAndEventIdAndStatus(Long userId, Long eventId, Status status);
}
