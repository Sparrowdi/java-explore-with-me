package ru.practicum.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStats {
    // название сервиса
    private String app;
    // URI сервиса
    private String uri;
    // количество просмотров
    private Long hits;
}
