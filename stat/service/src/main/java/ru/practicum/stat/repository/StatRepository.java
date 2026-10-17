package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.stat.dto.ViewStats(app, uri, COUNT(DISTINCT ip)) " +
            "FROM Hit " +
            "WHERE :start <= timestamp AND timestamp <= :end " +
            "AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(DISTINCT ip) DESC")
    List<ViewStats> findUniqueStatsByUris(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stat.dto.ViewStats(app, uri, COUNT(DISTINCT ip)) " +
            "FROM Hit " +
            "WHERE :start <= timestamp AND timestamp <= :end " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(DISTINCT ip) DESC")
    List<ViewStats> findUniqueStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.stat.dto.ViewStats(app, uri, COUNT(ip)) " +
            "FROM Hit " +
            "WHERE :start <= timestamp AND timestamp <= :end " +
            "AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(ip) DESC")
    List<ViewStats> findStatsByUris(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stat.dto.ViewStats(app, uri, COUNT(ip)) " +
            "FROM Hit " +
            "WHERE :start <= timestamp AND timestamp <= :end " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(ip) DESC")
    List<ViewStats> findAllStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
