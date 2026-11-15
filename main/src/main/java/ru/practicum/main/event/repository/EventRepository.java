package ru.practicum.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.request.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = """
            SELECT e
            FROM Event e
            JOIN FETCH e.category
            JOIN FETCH e.initiator
            WHERE e.initiator.id = :userId
            """)
    List<Event> findEventsByInitiator(@Param("userId") long userId, Pageable pageable);

    @Query("""
            SELECT e FROM Event e
            JOIN FETCH e.category
            JOIN FETCH e.initiator
            WHERE e.initiator.id = :userId AND e.id = :eventId""")
    Optional<Event> findByInitiatorIdAndIdWithCategoryAndInitiator(@Param("userId") long userId,
                                                                   @Param("eventId") long eventId);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    @Query("""
            SELECT e
            FROM Event e
            JOIN FETCH e.category
            JOIN FETCH e.initiator
            WHERE (:users IS NULL OR e.initiator.id IN :users)
                AND (:states IS NULL OR e.state IN :states)
                AND e.category.id IN :categories
                AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart)
                AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)
            """)
    List<Event> findByInitiatorInAndStateInAndCategoryInAndEventDateBetween(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("""
            SELECT e
            FROM Event e
            JOIN FETCH e.category
            JOIN FETCH e.initiator
            WHERE (:users IS NULL OR e.initiator.id IN :users)
                AND (:states IS NULL OR e.state IN :states)
                AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart)
                AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)
            """)
    List<Event> findByInitiatorInAndStateInAndEventDateBetween(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("""
            SELECT e FROM Event e
            JOIN FETCH e.category
            JOIN FETCH e.initiator
            WHERE e.id = :id AND e.state = :state""")
    Optional<Event> findByIdAndStateWithCategoryAndInitiator(@Param("id") long id, @Param("state") EventState state);

    @Query("""
            SELECT e FROM Event e
            JOIN FETCH e.category
            JOIN FETCH e.initiator
            WHERE e.state = :state
                AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', CAST(:text AS text), '%'))
                     OR LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:text AS text), '%')))
                AND (CAST(:paid AS boolean) IS NULL OR e.paid = :paid)
                AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart)
                AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)
                AND (CAST(:onlyAvailable AS boolean) = FALSE OR e.participantLimit = 0
                    OR (SELECT COUNT(r) FROM Request r WHERE r.event.id = e.id AND r.status = :status) < e.participantLimit)
            """)
    List<Event> findPublishedEventsWithoutCategories(
            @Param("text") String text,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable,
            @Param("state") EventState state,
            @Param("status") RequestStatus status,
            Pageable pageable);

    @Query("""
            SELECT e FROM Event e
            JOIN FETCH e.category
            JOIN FETCH e.initiator
            WHERE e.state = :state
                AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', CAST(:text AS text), '%'))
                     OR LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:text AS text), '%')))
                AND e.category.id IN :categories
                AND (CAST(:paid AS boolean) IS NULL OR e.paid = :paid)
                AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart)
                AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)
                AND (CAST(:onlyAvailable AS boolean) = FALSE OR e.participantLimit = 0
                    OR (SELECT COUNT(r) FROM Request r WHERE r.event.id = e.id AND r.status = :status) < e.participantLimit)
            """)
    List<Event> findPublishedEventsWithCategories(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable,
            @Param("state") EventState state,
            @Param("status") RequestStatus status,
            Pageable pageable);

    Optional<Event> findByIdAndState(long eventId, EventState state);
}