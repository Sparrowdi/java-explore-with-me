package ru.practicum.main.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryRequest {
    @NotBlank(message = "имя категории не может быть пустым")
    @Size(min = 1, max = 50, message = "имя категории должно содержать от 1 до 50 символов")
    private String name;
}