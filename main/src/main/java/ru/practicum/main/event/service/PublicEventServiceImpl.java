package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.pagination.FromSizeRequest;
import ru.practicum.main.request.model.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.stat.StatClient;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final StatClient statClient;
    private final RequestRepository requestRepository;

    private void saveHit(String ip, String path) {
        EndpointHit hit = new EndpointHit();
        hit.setApp("main-service");
        hit.setIp(ip);
        hit.setUri(path);
        hit.setTimestamp(LocalDateTime.now());
        statClient.hit(hit);
    }

    private long getUniqueViewsForEvent(LocalDateTime createdOn, String path) {
        List<ViewStats> stats = statClient.getStats(createdOn, LocalDateTime.now(), List.of(path), true);
        return stats.isEmpty() ? 0L : stats.get(0).getHits();
    }

    @Override
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
        } else if (!rangeStart.isBefore(rangeEnd)) {
            throw new BadRequestException("Range start must be no later than range end");
        }

        Sort sortBy = sort == EventSort.VIEWS
                ? Sort.by(Sort.Direction.DESC, "views")
                : Sort.by(Sort.Direction.ASC, "eventDate");

        Pageable pageable = FromSizeRequest.of(from, size, sortBy);

        categories = (categories == null || categories.isEmpty()) ? null : categories;

        List<Event> events;
        if (categories == null || categories.isEmpty()) {
            events = eventRepository.findPublishedEventsWithoutCategories(
                    text,
                    paid,
                    rangeStart,
                    rangeEnd,
                    onlyAvailable,
                    EventState.PUBLISHED,
                    RequestStatus.CONFIRMED,
                    pageable
            );
        } else {
            events = eventRepository.findPublishedEventsWithCategories(
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
        }

        if (events.isEmpty()) {
            saveHit(ip, path);
            return List.of();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, Long> confirmedByEventId = requestRepository
                .countConfirmedByEventIds(eventIds, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        saveHit(ip, path);

        return events.stream()
                .map(event -> EventMapper.mapToEventShortDto(
                        event,
                        confirmedByEventId.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto get(long id, String ip, String path) {
        Event event = eventRepository.findByIdAndStateWithCategoryAndInitiator(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));

        saveHit(ip, path);

        long uniqueViews = getUniqueViewsForEvent(event.getCreatedOn(), path);
        long confirmed = requestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED);
        EventFullDto dto = EventMapper.mapToEventFullDto(event, confirmed);

        dto.setViews(uniqueViews);
        return dto;
    }
}