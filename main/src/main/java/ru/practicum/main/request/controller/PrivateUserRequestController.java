package ru.practicum.main.request.controller;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateUserRequestController {
    private final RequestService requestService;

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях.
     *
     * @param userId id текущего пользователя.
     * @return список заявок текущего пользователя на участие в чужих событиях;
     * пустой список,  если по заданным фильтрам не найдено ни одной заявки.
     */
    @GetMapping
    public List<ParticipationRequestDto> getOtherEventsRequests(@PathVariable @PositiveOrZero long userId) {
        return requestService.get(userId);
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии.
     *
     * @param userId  id текущего пользователя.
     * @param eventId id события.
     * @return созданный запрос на участие с присвоенным идентификатором;
     * код ошибки 409, если:
     * пользователь пытается добавить повторный запрос;
     * пользователь является инициатором события, и пытается добавить запрос на участие в своём событии;
     * событие не опубликовано;
     * у события достигнут лимит запросов на участие.
     * Для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @PositiveOrZero long userId,
                                              @RequestParam @PositiveOrZero long eventId
    ) {
        log.info(
                "POST /users/{}/requests-запрос пользователя с id={} на создание запроса на участие в событии с id={}",
                userId, userId, eventId
        );
        return requestService.add(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии.
     *
     * @param userId    id текущего пользователя.
     * @param requestId id запроса на участие.
     * @return
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable @PositiveOrZero long userId,
                                                 @PathVariable @PositiveOrZero long requestId
    ) {
        log.info(
                "PATCH /users/{}/requests/{}/cancel - запрос пользователя с id={} на отмену запроса на участие с id={}",
                userId, requestId, userId, requestId
        );
        return requestService.update(userId, requestId);
    }
}