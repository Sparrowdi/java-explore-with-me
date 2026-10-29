package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.EventSort;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.pagination.FromSizeRequest;
import ru.practicum.main.request.model.RequestStatus;
import ru.practicum.stat.StatClient;
import ru.practicum.stat.dto.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final StatClient statClient;

    private void saveHit(String ip, String path) {
        EndpointHit hit = new EndpointHit();
        hit.setApp("main-service");
        hit.setIp(ip);
        hit.setUri(path);
        hit.setTimestamp(LocalDateTime.now());
        statClient.hit(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> get(String text,
                                   List<Long> categories,
                                   Boolean paid,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   boolean onlyAvailable,
                                   EventSort sort,
                                   int from,
                                   int size,
                                   String ip,
                                   String path
    ) {
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }

        Sort sortBy = sort == EventSort.VIEWS
                ? Sort.by(Sort.Direction.DESC, "views")
                : Sort.by(Sort.Direction.ASC, "eventDate");

        Pageable pageable = FromSizeRequest.of(from, size, sortBy);

        categories = (categories == null || categories.isEmpty()) ? null : categories;

        List<Event> events = eventRepository.findPublishedEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                EventState.PUBLISHED,
                RequestStatus.CONFIRMED,
                pageable
        );

        saveHit(ip, path);
        return events.stream().map(EventMapper::mapToEventShortDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto get(long id, String ip, String path) {
        Event event = eventRepository.findByIdAndStateWithCategoryAndInitiator(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));

        event.setViews(event.getViews() + 1);
        event = eventRepository.save(event);

        saveHit(ip, path);
        return EventMapper.mapToEventFullDto(event);
    }
}