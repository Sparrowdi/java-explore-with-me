package ru.practicum.main.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUserController {
    private final UserService userService;

    /**
     * Получение информации о пользователях.
     *
     * @param ids  id пользователей; если не указан или пуст — возвращаются все пользователи.
     * @param from количество элементов, которые нужно пропустить (по умолчанию 0).
     * @param size количество элементов в наборе (по умолчанию 10).
     * @return список пользователей: всех, если ids не указан;
     * или только с указанными идентификаторами, если ids передан;
     * пустой список, если по фильтрам ничего не найдено.
     */
    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return userService.get(ids, from, size);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUserRequest данные добавляемого пользователя.
     * @return созданный пользователь с присвоенным идентификатором.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("POST /admin/users - запрос на создание пользователя \"{}\"", newUserRequest.getName());
        return userService.add(newUserRequest);
    }

    /**
     * Удаление пользователя.
     *
     * @param userId id пользователя.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @PositiveOrZero long userId) {
        log.info("DELETE /admin/users/{} - запрос на удаление пользователя с id = {}", userId, userId);
        userService.delete(userId);
    }
}