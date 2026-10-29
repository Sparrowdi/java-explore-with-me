package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.validation.EventDateValid;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest implements UpdateEventRequest {
    // Новая аннотация
    @Size(min = 20, max = 2000, message = "краткое описание события должно содержать от 20 до 2000 символов")
    private String annotation;
    // Новая категория
    private Long category;
    // Информация о событии
    @Size(min = 20, max = 7000, message = "полное описание события должно содержать от 20 до 7000 символов")
    private String description;
    // Новые дата и время на которые намечено событие
    @EventDateValid
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    // Широта и долгота места проведения события
    @Valid
    private Location location;
    // Новое значение флага о платности мероприятия
    private Boolean paid;
    // Новый лимит пользователей. Значение 0 - означает отсутствие ограничения.
    @PositiveOrZero(message = "количество участников не может быть отрицательным")
    private Integer participantLimit;
    // Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события;
    // false - будут подтверждаться автоматически.
    private Boolean requestModeration;
    // Изменение состояния события
    private AdminStateAction stateAction;
    // Новый заголовок
    @Size(min = 3, max = 120, message = "заголовок события должен содержать от 3 до 120 символов")
    private String title;

    public boolean hasAnnotation() {
        return !(annotation == null || annotation.isBlank());
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasEventDate() {
        return eventDate != null;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasParticipantLimit() {
        return participantLimit != null;
    }

    public boolean hasRequestModeration() {
        return requestModeration != null;
    }

    public boolean hasStateAction() {
        return stateAction != null;
    }

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }
}