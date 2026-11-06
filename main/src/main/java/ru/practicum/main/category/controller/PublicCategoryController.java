package ru.practicum.main.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    /**
     * Получение категорий.
     *
     * @param from количество категорий, которые нужно пропустить для формирования текущего набора.
     * @param size количество категорий в наборе.
     * @return список категорий;
     * пустой список, если по заданным фильтрам не найдено ни одной категории.
     */
    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return categoryService.get(from, size);
    }

    /**
     * Получение информации о категории по её идентификатору.
     *
     * @param catId id категории.
     * @return статус код 404, если категория не найдена.
     */
    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable @PositiveOrZero long catId) {
        return categoryService.get(catId);
    }
}