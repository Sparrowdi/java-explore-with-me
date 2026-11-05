package ru.practicum.stat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(ValidationException ex) {
        log.warn(ex.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}