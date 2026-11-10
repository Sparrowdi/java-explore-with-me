package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentRequest;
import ru.practicum.main.comment.dto.UpdateCommentRequest;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    /**
     * Добавление комментария к событию.
     *
     * @param userId            id текущего пользователя.
     * @param eventId           id события, к которому создаётся комментарий.
     * @param newCommentRequest данные комментария.
     * @return созданный комментарий с присвоенным идентификатором;
     * статус код 404, если не найдено опубликованное событие с наступившей датой;
     * статус код 409, если не найдено участие пользователя в событии.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable @PositiveOrZero long userId,
                                 @PathVariable @PositiveOrZero long eventId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest
    ) {
        log.info("POST /users/{}/events/{}/comments - запрос на добавление комментария \"{}\" пользователем с id={} к "
                + "событию с id={}", userId, eventId, newCommentRequest.getText(), userId, eventId);
        return commentService.add(userId, eventId, newCommentRequest);
    }

    /**
     * Изменение комментария к событию текущего пользователя.
     *
     * @param userId               id текущего пользователя.
     * @param eventId              id события, к которому редактируется комментарий.
     * @param commentId            id редактируемого комментария.
     * @param updateCommentRequest новые данные комментария.
     * @return измененный комментарий;
     * статус код 404, если не найден комментарйи пользователя к событию.
     */
    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable @PositiveOrZero long userId,
                                    @PathVariable @PositiveOrZero long eventId,
                                    @PathVariable @PositiveOrZero long commentId,
                                    @Valid @RequestBody UpdateCommentRequest updateCommentRequest
    ) {
        log.info(
                "PATCH /users/{}/events/{}/comments/{} - запрос на изменение комментария с id={} пользователем с id={}",
                userId, eventId, commentId, commentId, userId);
        return commentService.update(userId, eventId, commentId, updateCommentRequest);
    }

    /**
     * Удаление комментария к событию текущего пользователя.
     * Статус код 404, если не найден комментарйи пользователя к событию.
     *
     * @param userId    id текущего пользователя.
     * @param eventId   id события, для которого удаляется комментарий.
     * @param commentId id удаляемого комментария.
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @PositiveOrZero long userId,
                              @PathVariable @PositiveOrZero long eventId,
                              @PathVariable @PositiveOrZero long commentId
    ) {
        log.info("DELETE /users/{}/events/{}/comments/{} - запрос на удаление комментария с id = {} пользователем "
                + "с id={} к событию с id={}", userId, eventId, commentId, commentId, userId, eventId);
        commentService.delete(userId, eventId, commentId);
    }

    /**
     * Получение комментариев к событию текущего пользователя.
     *
     * @param userId  id текущего пользователя.
     * @param eventId id события, к которому относятся комментарии.
     * @param from    количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size    количество элементов в наборе.
     * @return список комментариев к событию текущего пользователя;
     * пустой список, если по заданным фильтрам не найдено ни одного события.
     */
    @GetMapping
    public List<CommentDto> getComments(@PathVariable @PositiveOrZero long userId,
                                        @PathVariable @PositiveOrZero long eventId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return commentService.get(userId, eventId, from, size);
    }
}