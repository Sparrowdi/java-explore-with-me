package ru.practicum.main.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;

/**
 * Подборка событий.
 */
@Data
public class NewCompilationRequest {
    /**
     * Список идентификаторов событий, входящих в подборку.
     */
    private List<Long> events;
    /**
     * Закреплена ли подборка на главной странице сайта.
     */
    private boolean pinned;
    @NotBlank(message = "заголовок подборки событий не может быть пустым")
    @Size(min = 1, max = 50, message = "заголовок подборки событий должен содержать от 1 до 50 символов")
    private String title;

}