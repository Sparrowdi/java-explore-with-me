package ru.practicum.main.comment.mapper;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentRequest;
import ru.practicum.main.comment.dto.UpdateCommentRequest;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

public final class CommentMapper {
    public static Comment mapToComment(NewCommentRequest newCommentRequest, User author, Event event) {
        Comment comment = new Comment();
        comment.setText(newCommentRequest.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment, Event event, long confirmed) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                UserMapper.mapToUserDto(comment.getAuthor()),
                EventMapper.mapToEventShortDto(event, confirmed),
                comment.getCreatedOn(),
                comment.getUpdatedOn()
        );
    }

    public static Comment updateCommentFields(Comment comment, UpdateCommentRequest updateCommentRequest) {
        comment.setText(updateCommentRequest.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return comment;
    }
}
