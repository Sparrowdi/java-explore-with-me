package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> get(long userId, int from, int size);

    EventFullDto add(long userId, NewEventRequest newEventRequest);

    EventFullDto get(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEventRequest);

    List<EventFullDto> get(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    EventFullDto update(long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
