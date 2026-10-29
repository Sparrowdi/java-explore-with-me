package ru.practicum.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Изменение информации о подборке событий.
 */
@Data
public class UpdateCompilationRequest {
    /**
     * Список идентификаторов событий, входящих в подборку.
     */
    private List<Long> events;
    /**
     * Закреплена ли подборка на главной странице сайта.
     */
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "заголовок подборки событий должен содержать от 1 до 50 символов")
    private String title;

    public boolean hasEvents() {
        return events != null;
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }
}