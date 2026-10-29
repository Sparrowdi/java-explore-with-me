package ru.practicum.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;

/**
 * Подборка событий.
 */
@Data
@AllArgsConstructor
@Builder
public class CompilationDto {
    /**
     * Список событий, входящих в подборку.
     */
    private List<EventShortDto> events;
    private Long id;
    /**
     * Закреплена ли подборка на главной странице сайта.
     */
    private boolean pinned;
    private String title;
}