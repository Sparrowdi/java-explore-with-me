package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentRequest;
import ru.practicum.main.comment.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto add(long userId, long eventId, NewCommentRequest newCommentRequest);

    CommentDto update(long userId, long eventId, long commentId, UpdateCommentRequest updateCommentRequest);

    void delete(long userId, long eventId, long commentId);

    void delete(long eventId, long commentId);

    List<CommentDto> get(long userId, long eventId, int from, int size);

    List<CommentDto> get(Long userId, Long eventId, String text, int from, int size);

    List<CommentDto> get(long eventId, int from, int size);

    CommentDto get(long eventId, long commentId);
}
