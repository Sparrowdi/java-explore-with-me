package ru.practicum.stat.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.model.Hit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HitMapper {
    public static Hit toHit(EndpointHit endpointHit) {
        return Hit.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}
