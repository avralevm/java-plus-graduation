package ru.practicum.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.output.GetStatisticDto;
import ru.practicum.server.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Integer> {

    @Query("SELECT new ru.practicum.dto.output.GetStatisticDto(s.app, s.uri, " +
            "CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s.ip) END) " +
            "FROM Statistic s " +
            "WHERE s.uri IN :uris " +
            "AND s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<GetStatisticDto> findHitsByUriInAndTimestampBetween(
            @Param("uris") List<String> uris,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("unique") Boolean unique
    );

    @Query("SELECT new ru.practicum.dto.output.GetStatisticDto(s.app, s.uri, " +
            "CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s.ip) END) " +
            "FROM Statistic s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<GetStatisticDto> findHitsByTimestampBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("unique") Boolean unique
    );
}