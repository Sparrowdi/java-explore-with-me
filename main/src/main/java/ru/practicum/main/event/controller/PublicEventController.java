package ru.practicum.main.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.EventSort;
import ru.practicum.main.event.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {
    private final PublicEventService publicEventService;

    /**
     * Получение событий с возможностью фильтрации.
     *
     * @param text          текст для поиска в содержимом аннотации и подробном описании события.
     * @param categories    список идентификаторов категорий в которых будет вестись поиск.
     * @param paid          поиск только платных/бесплатных событий.
     * @param rangeStart    дата и время не раньше которых должно произойти событие.
     * @param rangeEnd      дата и время не позже которых должно произойти событие.
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие.
     * @param sort          вариант сортировки: по дате события или по количеству просмотров.
     * @param from          количество событий, которые нужно пропустить для формирования текущего набора.
     * @param size          количество событий в наборе.
     * @return список событий по заданным фильтрам;
     * пустой список, если по заданным фильтрам не найдено ни одного события.
     */
    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         HttpServletRequest request
    ) {
        log.info("GET /events - запрос на получение информации об опубликованных событиях:\n"
                        + "text = {},\n"
                        + "categories = {},\n"
                        + "paid = {},\n"
                        + "rangeStart = {},\n"
                        + "rangeEnd = {},\n"
                        + "onlyAvailable = {},\n"
                        + "sort = {},\n"
                        + "from = {},\n"
                        + "size = {},\n",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return publicEventService.get(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                request.getRemoteAddr(),
                request.getRequestURI()
        );
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору.
     * Событие должно быть опубликовано (PUBLISHED).
     * Информация о событии включает количество просмотров и количество подтверждённых заявок.
     * Информация о запросе сохраняется в сервисе статистики.
     *
     * @param id id события.
     * @return полная информация о событии;
     * статус код 404, если события не найдено.
     */
    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable @PositiveOrZero long id, HttpServletRequest request) {
        log.info("GET /events/{} - запрос на получение информации об опубликованном событии с id={} ip={} path={}", id, id, request.getRemoteAddr(), request.getRequestURI());
        return publicEventService.get(id, request.getRemoteAddr(), request.getRequestURI());
    }
}