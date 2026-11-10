package ru.practicum.main.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCommentRequest {
    @NotBlank(message = "текст комментария не может быть пустым")
    @Size(max = 7000, message = "текст комментария должен содержать до 7000 символов")
    private String text;
}
