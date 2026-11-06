package ru.practicum.main.event.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    // широта места проведения события
    @NotNull(message = "широта места проведения события не может быть null")
    private Double lat;

    // долгота места проведения события
    @NotNull(message = "долгота места проведения события не может быть null")
    private Double lon;
}