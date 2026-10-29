package ru.practicum.main.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationRequest;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    /**
     * Добавление новой подборки (подборка может не содержать событий).
     *
     * @param newCompilationRequest данные новой подборки.
     * @return созданную подборку событий с присвоенным идентификатором.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationRequest newCompilationRequest) {
        log.info("POST /admin/compilations - запрос на создание подборки событий \"{}\"",
                newCompilationRequest.getTitle()
        );
        return compilationService.add(newCompilationRequest);
    }

    /**
     * Удаление подборки.
     *
     * @param compId id подборки.
     */
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @PositiveOrZero long compId) {
        log.info("DELETE /admin/compilations/{} - запрос на удаление подборки с id = {}", compId);
        compilationService.delete(compId);
    }

    /**
     * Обновить информацию о подборке.
     *
     * @param compId                   id подборки.
     * @param updateCompilationRequest данные для обновления подборки.
     * @return измененная подборка событий.
     */
    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable @PositiveOrZero long compId,
                                 @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest
    ) {
        log.info("PATCH /admin/compilations/{} - запрос на изменение подборки событий с id={}", compId, compId);
        return compilationService.update(compId, updateCompilationRequest);
    }
}