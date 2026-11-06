package ru.practicum.main.event.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDateValid, LocalDateTime> {
    private static final int MIN_HOURS_BEFORE_EVENT = 2;

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        if (eventDate == null) {
            return true;
        }
        LocalDateTime minAllowedDate = LocalDateTime.now().plusHours(MIN_HOURS_BEFORE_EVENT);
        return eventDate.isAfter(minAllowedDate);
    }
}