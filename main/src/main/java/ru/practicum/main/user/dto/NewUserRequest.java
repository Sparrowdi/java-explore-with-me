package ru.practicum.main.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "email некорректен")
    @Size(min = 6, max = 254, message = "email должен содержать от 6 до 254 символов")
    private String email;
    @NotBlank(message = "имя пользователя не может быть пустым")
    @Size(min = 2, max = 250, message = "имя пользователя должно содержать от 2 до 250 символов")
    private String name;
}
