package ru.practicum.main.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.main.request.model.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    // Идентификаторы запросов на участие в событии текущего пользователя
    private List<Long> requestIds;
    // Новый статус запроса на участие в событии текущего пользователя
    private RequestStatus status;
}