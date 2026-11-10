package ru.practicum.main.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    /**
     * Получение всех комментариев к событию.
     *
     * @param eventId id события.
     * @param from    количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size    количество элементов в наборе.
     * @return список комментариев;
     * пустой список, если по заданным фильтрам не найдено ни одного комментария.
     */
    @GetMapping
    public List<CommentDto> getComments(@PathVariable @PositiveOrZero long eventId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return commentService.get(eventId, from, size);
    }

    /**
     * Просмотр комментария к событию.
     *
     * @param eventId   id события.
     * @param commentId id комментария.
     * @return комментарий к событию;
     * статус код 404, если комментарий к событию не найден.
     */
    @GetMapping("/{commentId}")
    public CommentDto get(@PathVariable @PositiveOrZero long eventId,
                          @PathVariable @PositiveOrZero long commentId
    ) {
        return commentService.get(eventId, commentId);
    }
}
