package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryRequest;
import ru.practicum.main.category.dto.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryRequest newCategoryRequest);

    void delete(long catId);

    CategoryDto update(long catId, UpdateCategoryRequest updateCategoryRequest);

    List<CategoryDto> get(int from, int size);

    CategoryDto get(long catId);
}
