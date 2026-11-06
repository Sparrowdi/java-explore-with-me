package ru.practicum.main.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    /**
     * Получение подборок событий.
     *
     * @param pinned искать только закрепленные/не закрепленные подборки.
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size   количество элементов в наборе.
     * @return подборки по заданным фильтрам;
     * пустой список, если по заданным фильтрам не найдено ни одной подборки.
     */
    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return compilationService.get(pinned, from, size);
    }

    /**
     * Получение подборки событий по его id.
     *
     * @param compId id подборки.
     * @return подборку событий;
     * статус код 404, если подборки с заданным id не найдено.
     */
    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable @PositiveOrZero long compId) {
        return compilationService.get(compId);
    }
}