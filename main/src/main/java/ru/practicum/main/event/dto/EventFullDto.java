package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * Полная информация о событии.
 */
@Data
@AllArgsConstructor
@Builder
public class EventFullDto {
    /**
     * Краткое описание.
     */
    private String annotation;
    /**
     * Категория.
     */
    private CategoryDto category;
    /**
     * Количество одобренных заявок на участие в данном событии.
     */
    private Long confirmedRequests;
    /**
     * Дата и время создания события.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    /**
     * Полное описание события.
     */
    private String description;
    /**
     * Дата и время, на которые намечено событие.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Long id;
    /**
     * Пользователь.
     */
    private UserShortDto initiator;
    /**
     * Место проведения события.
     */
    private Location location;
    /**
     * Нужно ли оплачивать участие.
     */
    private boolean paid;
    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения.
     */
    private Integer participantLimit;
    /**
     * Дата и время публикации события.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    private Boolean requestModeration;
    /**
     * Список состояний жизненного цикла события.
     */
    private EventState state;
    /**
     * Заголовок.
     */
    private String title;
    /**
     * Количество просмотров события.
     */
    private Long views;
}