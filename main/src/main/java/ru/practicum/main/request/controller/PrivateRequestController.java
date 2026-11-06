package ru.practicum.main.request.controller;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {
    private final RequestService requestService;

    /**
     * Получение информации о запросах на участие в событии текущего пользователя.
     *
     * @param userId  id текущего пользователя.
     * @param eventId id события.
     * @return список запросов на участие в событии текущего пользователя;
     * пустой список, если по заданным фильтрам не найдено ни одной заявки.
     */
    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable @PositiveOrZero long userId,
                                                     @PathVariable @PositiveOrZero long eventId
    ) {
        return requestService.get(userId, eventId);
    }

//    если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
//    нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
//    статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
//    если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя.
     *
     * @param userId        id текущего пользователя.
     * @param eventId       id события текущего пользователя.
     * @param statusRequest изменение статуса запроса на участие в событии текущего пользователя.
     * @return результат подтверждения/отклонения заявок на участие в событии.
     */
    @PatchMapping
    public EventRequestStatusUpdateResult updateStatus(@PathVariable @PositiveOrZero long userId,
                                                       @PathVariable @PositiveOrZero long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest statusRequest
    ) {
        log.info("PATCH /users/{}/events/{}/requests - запрос на изменение статуса заявок на участие в событии с id={} "
                + "пользователя с id={} добавленного пользователем с id={}", eventId, userId, eventId, userId);
        return requestService.update(userId, eventId, statusRequest);
    }
}