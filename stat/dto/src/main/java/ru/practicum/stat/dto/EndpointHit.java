package ru.practicum.stat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {
    // идентификатор записи
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    // идентификатор сервиса, для которого записывается информация
    private String app;
    // URI для которого был осуществлен запрос
    private String uri;
    // IP-адрес пользователя, осуществившего запрос
    private String ip;
    // дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
