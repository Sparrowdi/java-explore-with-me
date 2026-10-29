package ru.practicum.main.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.main.request.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    // Дата и время создания заявки
    private LocalDateTime created;
    // Идентификатор события
    private Long event;
    // Идентификатор заявки
    private Long id;
    // Идентификатор пользователя, отправившего заявку
    private Long requester;
    // Статус заявки
    private RequestStatus status;
}