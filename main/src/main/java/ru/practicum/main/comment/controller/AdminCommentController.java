package ru.practicum.main.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    /**
     * Поиск комментариев.
     *
     * @param userId  автор комментариев.
     * @param eventId id события, к которому относятся комментарии.
     * @param text    текст, который должны содержать комментарии.
     * @param from    количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size    количество элементов в наборе.
     * @return список комментариев;
     * пустой список, если по заданным фильтрам не найдено ни одного комментария.
     */
    @GetMapping("/comments")
    public List<CommentDto> get(@RequestParam(required = false) Long userId,
                                @RequestParam(required = false) Long eventId,
                                @RequestParam(required = false) String text,
                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return commentService.get(userId, eventId, text, from, size);
    }

    /**
     * Удаление любого комментария к событию. Статус код 404, если комментарий не найден.
     *
     * @param eventId   id события, для которого удаляется комментарий.
     * @param commentId id удаляемого комментария.
     */
    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @PositiveOrZero long eventId,
                              @PathVariable @PositiveOrZero long commentId
    ) {
        log.info("DELETE /admin/events/{}/comments/{} - запрос на удаление комментария с id = {} к событию с id={}",
                eventId, commentId, commentId, eventId);
        commentService.delete(eventId, commentId);
    }
}
