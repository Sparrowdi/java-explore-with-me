package ru.practicum.main.event.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDateValid {
    String message() default "должно содержать дату, которая еще не наступила";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}