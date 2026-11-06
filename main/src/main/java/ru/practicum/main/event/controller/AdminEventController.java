package ru.practicum.main.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {
    private final EventService eventService;

    /**
     * Поиск событий.
     *
     * @param users      список id пользователей, чьи события нужно найти.
     * @param states     список состояний в которых находятся искомые события.
     * @param categories список id категорий в которых будет вестись поиск.
     * @param rangeStart дата и время не раньше которых должно произойти событие.
     * @param rangeEnd   дата и время не позже которых должно произойти событие.
     * @param from       количество событий, которые нужно пропустить для формирования текущего набора.
     * @param size       количество событий в наборе.
     * @return полную информацию обо всех событиях, подходящих под переданные условия;
     * пустой список, если по заданным фильтрам не найдено ни одного события.
     */
    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return eventService.get(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /**
     * Редактирование данных любого события и его статуса(отклонение/публикация) администратором.
     * Валидация данных не требуется.
     *
     * @param eventId                 id события.
     * @param updateEventAdminRequest данные для изменения информации о событии.
     * @return отредактированное событие;
     * код ошибки 409, если:
     * дата начала изменяемого события ранее чем за час от даты публикации;
     * событие пытаются опубликовать, а оно не в состоянии ожидания публикации;
     * событие пытаются отклонить, а оно еще не опубликовано.
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @PositiveOrZero long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest
    ) {
        log.info("PATCH /admin/events/{} - запрос на изменение события с id={} и его статуса (отклонение/публикация)",
                eventId);
        return eventService.update(eventId, updateEventAdminRequest);
    }
}