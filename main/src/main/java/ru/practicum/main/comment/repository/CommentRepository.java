package ru.practicum.main.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndEventIdAndAuthorId(long commentId, long eventId, long userId);

    Optional<Comment> findByIdAndEventId(long commentId, long eventId);

    @Query(value = """
            SELECT c
            FROM Comment c
            JOIN FETCH c.author
            WHERE c.author.id = :userId AND c.event.id = :eventId
            """)
    List<Comment> findCommentsByAuthorAndByEvent(@Param("userId") long userId,
                                                 @Param("eventId") long eventId,
                                                 Pageable pageable
    );

    @Query("""
            SELECT c
            FROM Comment c
            JOIN FETCH c.author
            JOIN FETCH c.event
            WHERE (:userId IS NULL OR c.author.id = :userId)
                AND (:eventId IS NULL OR c.event.id = :eventId)
                AND (:text IS NULL OR LOWER(c.text) LIKE LOWER(CONCAT('%', CAST(:text AS text),'%')))
            """)
    List<Comment> findByAuthorAndByEventAndTextLike(@Param("userId") Long userId,
                                                    @Param("eventId") Long eventId,
                                                    @Param("text") String text,
                                                    Pageable pageable
    );

    @Query(value = """
            SELECT c
            FROM Comment c
            JOIN FETCH c.author
            WHERE c.event.id = :eventId
            """)
    List<Comment> findCommentsByEvent(@Param("eventId") long eventId, Pageable pageable);
}