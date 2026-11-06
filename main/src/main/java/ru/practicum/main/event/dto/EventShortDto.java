package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * Краткая информация о событии.
 */
@Data
@AllArgsConstructor
@Builder
public class EventShortDto {
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
     * Нужно ли оплачивать участие.
     */
    private boolean paid;
    /**
     * Заголовок.
     */
    private String title;
    /**
     * Количество просмотров события.
     */
    private Long views;
}