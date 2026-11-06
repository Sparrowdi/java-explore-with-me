package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.EventSort;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> get(String text,
                            List<Long> categories,
                            Boolean paid,
                            LocalDateTime rangeStart,
                            LocalDateTime rangeEnd,
                            boolean onlyAvailable,
                            EventSort sort,
                            int from,
                            int size,
                            String ip,
                            String path
    );

    EventFullDto get(long id, String ip, String path);
}
