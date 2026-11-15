package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentRequest;
import ru.practicum.main.comment.dto.UpdateCommentRequest;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.pagination.FromSizeRequest;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    private Map<Long, Long> getConfirmedStatusGroupByEventId(List<Comment> comments) {
        List<Long> eventIds = comments.stream()
                .map(comment -> comment.getEvent().getId())
                .distinct()
                .toList();

        return requestRepository
                .countConfirmedByEventIds(eventIds, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    @Transactional
    public CommentDto add(long userId, long eventId, NewCommentRequest newCommentRequest) {
        Request request = requestRepository.findByEventIdAndRequesterIdAndStatus(eventId, userId, RequestStatus.CONFIRMED)
                .orElseThrow(() -> new ConflictException(
                        String.format("User with id=%d didn't participate in event with id=%d", userId, eventId)));
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%d in state PUBLISHED was not found", eventId)));

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        Comment comment = CommentMapper.mapToComment(newCommentRequest, request.getRequester(), event);
        comment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(comment, event, confirmed);
    }

    @Override
    @Transactional
    public CommentDto update(long userId, long eventId, long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findByIdAndEventIdAndAuthorId(commentId, eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));
        Comment updatedComment = CommentMapper.updateCommentFields(comment, updateCommentRequest);
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        return CommentMapper.mapToCommentDto(updatedComment, comment.getEvent(), confirmed);
    }

    @Override
    @Transactional
    public void delete(long userId, long eventId, long commentId) {
        Comment comment = commentRepository.findByIdAndEventIdAndAuthorId(commentId, eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void delete(long eventId, long commentId) {
        Comment comment = commentRepository.findByIdAndEventId(commentId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));
        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> get(long userId, long eventId, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.by(Sort.Direction.DESC, "createdOn"));

        List<Comment> comments = commentRepository.findCommentsByAuthorAndByEvent(userId, eventId, pageable);
        if (comments.isEmpty()) {
            return List.of();
        }

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        return comments.stream()
                .map(comment -> CommentMapper.mapToCommentDto(comment, event, confirmed))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> get(Long userId, Long eventId, String text, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.by(Sort.Direction.DESC, "createdOn"));

        List<Comment> comments = commentRepository.findByAuthorAndByEventAndTextLike(userId, eventId, text, pageable);
        if (comments.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> confirmedByEventId = getConfirmedStatusGroupByEventId(comments);

        return comments.stream()
                .map(comment -> {
                    Event event = comment.getEvent();
                    return CommentMapper.mapToCommentDto(comment,
                            event,
                            confirmedByEventId.getOrDefault(event.getId(), 0L));
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> get(long eventId, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        List<Comment> comments = commentRepository.findCommentsByEvent(eventId, pageable);
        if (comments.isEmpty()) {
            return List.of();
        }

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        return comments.stream()
                .map(comment -> CommentMapper.mapToCommentDto(comment, event, confirmed))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto get(long eventId, long commentId) {
        Comment comment = commentRepository.findByIdAndEventId(commentId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        Event event = comment.getEvent();
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        return CommentMapper.mapToCommentDto(comment, event, confirmed);
    }
}