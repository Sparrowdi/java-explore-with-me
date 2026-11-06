package ru.practicum.main.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryRequest;
import ru.practicum.main.category.dto.UpdateCategoryRequest;
import ru.practicum.main.category.service.CategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    /**
     * Добавление новой категории.
     *
     * @param newCategoryRequest данные добавляемой категории.
     * @return созданную категорию с присвоенным идентификатором.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryRequest newCategoryRequest) {
        log.info("POST /admin/categories - запрос на создание категории \"{}\"", newCategoryRequest.getName());
        return categoryService.add(newCategoryRequest);
    }

    /**
     * Удаление категории
     *
     * @param catId id категории
     */
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @PositiveOrZero long catId) {
        log.info("DELETE /admin/categories/{} - запрос на удаление категории с id = {}", catId, catId);
        categoryService.delete(catId);
    }

    /**
     * Изменение категории.
     *
     * @param catId                 id категории.
     * @param updateCategoryRequest данные изменяемой категории.
     * @return измененную категорию.
     */
    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable @PositiveOrZero long catId,
                                      @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest
    ) {
        log.info("PATCH /admin/categories - запрос на изменение категории с id = {}", catId);
        return categoryService.update(catId, updateCategoryRequest);
    }
}