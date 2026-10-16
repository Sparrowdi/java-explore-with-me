package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.exception.ValidationException;
import ru.practicum.stat.mapper.HitMapper;
import ru.practicum.stat.model.Hit;
import ru.practicum.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public void hit(EndpointHit endpointHit) {
        Hit hit = HitMapper.toHit(endpointHit);
        statRepository.save(hit);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            String errMsg = "Дата и время начала должны быть раньше конца диапазона выгрузки статистики";
            log.warn(errMsg);
            throw new ValidationException(errMsg);
        }

        boolean hasUris = uris != null && !uris.isEmpty();

        if (unique && hasUris) {
            return statRepository.findUniqueStatsByUris(start, end, uris);
        } else if (unique) {
            return statRepository.findUniqueStats(start, end);
        } else if (hasUris) {
            return statRepository.findStatsByUris(start, end, uris);
        } else {
            return statRepository.findAllStats(start, end);
        }
    }
}