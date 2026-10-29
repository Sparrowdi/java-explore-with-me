package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByEventId(long eventId);

    List<Request> findByEventIdAndIdIn(long eventId, List<Long> requestIds);

    long countByEventIdAndStatus(long eventId, RequestStatus status);

    List<Request> findByEventIdAndStatusOrderById(long eventId, RequestStatus status);

    @Query("""
            SELECT r 
            FROM Request r
            WHERE r.requester.id = :userId
                AND r.event.initiator.id <> :userId
            """)
    List<Request> findByRequesterIdAndEventInitiatorIdNot(@Param("userId") long userId);

    Optional<Request> findByIdAndRequesterId(long id, long userId);
}