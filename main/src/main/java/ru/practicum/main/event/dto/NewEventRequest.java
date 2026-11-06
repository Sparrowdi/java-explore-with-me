package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.validation.EventDateValid;

import java.time.LocalDateTime;

@Data
public class NewEventRequest {
    @NotBlank(message = "краткое описание события не может быть пустым")
    @Size(min = 20, max = 2000, message = "краткое описание события должно содержать от 20 до 2000 символов")
    private String annotation;
    @NotNull(message = "id категории, к которой относится событие, должно быть заполнено")
    private Long category;
    @NotBlank(message = "полное описание события не может быть пустым")
    @Size(min = 20, max = 7000, message = "полное описание события должно содержать от 20 до 7000 символов")
    private String description;
    @NotNull(message = "дата и время, на которые намечено событие, должны быть заполнены")
    @EventDateValid
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull(message = "место проведения события должно быть заполнено")
    @Valid
    private Location location;
    // Нужно ли оплачивать участие в событии.
    private boolean paid = false;
    // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения.
    @PositiveOrZero(message = "количество участников не может быть отрицательным")
    private int participantLimit = 0;
    // Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события;
    // false - будут подтверждаться автоматически.
    private boolean requestModeration = true;
    @NotBlank(message = "заголовок события не может быть пустым")
    @Size(min = 3, max = 120, message = "заголовок события должен содержать от 3 до 120 символов")
    private String title;
}