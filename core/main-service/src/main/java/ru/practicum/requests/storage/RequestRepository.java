package ru.practicum.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r.event.id, COUNT (r) " +
            "FROM Request r " +
            "WHERE r.event.id IN :eventIds AND r.status = :status " +
            "GROUP BY r.event.id")
    List<Object[]> countAllByEventIdInAndStatus(@Param("eventIds") List<Long> eventIds, @Param("status") Status status);

    @Query("UPDATE Request AS r " +
            "SET r.status = :status " +
            "WHERE r.id IN :ids")
    @Modifying
    void setStatusForAllByIdIn(@Param("ids") List<Long> ids, @Param("status") Status status);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findByRequesterId(Long requesterId);

    long countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);
}
