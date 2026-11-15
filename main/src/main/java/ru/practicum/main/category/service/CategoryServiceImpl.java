package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryRequest;
import ru.practicum.main.category.dto.UpdateCategoryRequest;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.pagination.FromSizeRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto add(NewCategoryRequest newCategoryRequest) {
        Category category = CategoryMapper.mapToCategory(newCategoryRequest);
        category = categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public void delete(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto update(long catId, UpdateCategoryRequest updateCategoryRequest) {
        Category updatedCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
        updatedCategory.setName(updateCategoryRequest.getName());
        updatedCategory = categoryRepository.save(updatedCategory);
        return CategoryMapper.mapToCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> get(int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size);

        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::mapToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto get(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
        return CategoryMapper.mapToCategoryDto(category);
    }
}