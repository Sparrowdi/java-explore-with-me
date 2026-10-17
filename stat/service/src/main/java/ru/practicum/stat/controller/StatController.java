package ru.practicum.stat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    /***
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем
     * @param endpointHit тело запроса содержит информацию о названии сервиса, uri и ip пользователя
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHit endpointHit) {
        log.info("POST /hit - на uri={} сервиса с идентификатором={} был отправлен запрос пользователем с ip = {} в {}",
                endpointHit.getUri(),
                endpointHit.getApp(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
        statService.hit(endpointHit);
    }

    /***
     * @param start Дата и время начала диапазона, за который нужно выгрузить статистику
     *              (в формате "yyyy-MM-dd HH:mm:ss")
     * @param end Дата и время конца диапазона, за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
     * @param uris Список uri для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return возвращает статистику по посещениям
     */
    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique
    ) {
        log.info("GET /stats - запрос статистики по посещениям с {} по {} для uris={}, уникальные посещения={}",
                start, end, uris, unique
        );
        return statService.getStats(start, end, uris, unique);
    }
}
