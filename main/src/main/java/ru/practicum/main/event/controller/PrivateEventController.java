package ru.practicum.main.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;

    /**
     * Получение событий, добавленных текущим пользователем.
     *
     * @param userId id текущего пользователя.
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size   количество элементов в наборе.
     * @return список событий, добавленных текущим пользователем:
     * пустой список, если по фильтрам ничего не найдено.
     */
    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable @PositiveOrZero long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return eventService.get(userId, from, size);
    }

    /**
     * Добавление нового события.
     *
     * @param userId          id текущего пользователя.
     * @param newEventRequest данные добавляемого события.
     * @return созданное событие с присвоенным идентификатором.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @PositiveOrZero long userId,
                                 @Valid @RequestBody NewEventRequest newEventRequest
    ) {
        log.info("POST /users/{}/events - запрос на создание события \"{}\"", userId, newEventRequest.getTitle());
        return eventService.add(userId, newEventRequest);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем.
     * Событие доступно только инициатору.
     * Если событие с указанным eventId не существует или принадлежит другому пользователю — возвращается 404 Not Found.
     *
     * @param userId  id текущего пользователя.
     * @param eventId id события.
     * @return полная информация о событии;
     * статус код 404, если события не найдено.
     */
    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable @PositiveOrZero long userId,
                                 @PathVariable @PositiveOrZero long eventId
    ) {
        return eventService.get(userId, eventId);
    }

    /**
     * Изменение события, добавленного текущим пользователем.
     *
     * @param userId             id текущего пользователя.
     * @param eventId            id редактируемого события.
     * @param updateEventUserRequest новые данные события.
     * @return измененное событие.
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @PositiveOrZero long userId,
                                    @PathVariable @PositiveOrZero long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest
    ) {
        log.info("PATCH /users/{}/events/{} - запрос на изменение события с id={} добавленного пользователем с id={}", userId, eventId, eventId, userId);
        return eventService.update(userId, eventId, updateEventUserRequest);
    }
}