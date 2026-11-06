package ru.practicum.main.event.dto;

import ru.practicum.main.event.model.Location;

import java.time.LocalDateTime;

public interface UpdateEventRequest {
    // Новая аннотация
    String getAnnotation();

    // Новая категория
    Long getCategory();

    // Информация о событии
    String getDescription();

    // Новые дата и время на которые намечено событие
    LocalDateTime getEventDate();

    // Широта и долгота места проведения события
    Location getLocation();

    // Новое значение флага о платности мероприятия
    Boolean getPaid();

    // Новый лимит пользователей. Значение 0 - означает отсутствие ограничения.
    Integer getParticipantLimit();

    // Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события;
    // false - будут подтверждаться автоматически.
    Boolean getRequestModeration();

    // Новый заголовок
    String getTitle();

    boolean hasAnnotation();

    boolean hasCategory();

    boolean hasDescription();

    boolean hasEventDate();

    boolean hasLocation();

    boolean hasPaid();

    boolean hasParticipantLimit();

    boolean hasRequestModeration();

    boolean hasStateAction();

    boolean hasTitle();
}