package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationRequest;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.pagination.FromSizeRequest;
import ru.practicum.main.request.model.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    private Map<Long, Long> getConfirmedRequestsCountByEventId(List<Long> eventIds) {
        return eventIds.isEmpty()
                ? Map.of()
                : requestRepository.countConfirmedByEventIds(
                        eventIds,
                        RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    @Override
    @Transactional
    public CompilationDto add(NewCompilationRequest newCompilationRequest) {
        List<Event> events = newCompilationRequest.getEvents() == null
                ? List.of()
                : eventRepository.findAllById(newCompilationRequest.getEvents());
        Compilation compilation = CompilationMapper.mapToCompilation(newCompilationRequest, events);
        compilation = compilationRepository.save(compilation);

        Map<Long, Long> confirmedByEventId = getConfirmedRequestsCountByEventId(
                events.stream().map(Event::getId).toList()
        );
        return CompilationMapper.mapToCompilationDto(compilation, events, confirmedByEventId);
    }

    @Override
    @Transactional
    public void delete(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%d was not found", compId)));
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%d was not found", compId)));

        Compilation updatedCompilation = CompilationMapper
                .updateCompilationFields(compilation, updateCompilationRequest);

        List<Event> events = eventRepository.findAllById(updatedCompilation.getEvents());
        Map<Long, Long> confirmedByEventId = getConfirmedRequestsCountByEventId(
                events.stream().map(Event::getId).toList()
        );
        return CompilationMapper.mapToCompilationDto(updatedCompilation, events, confirmedByEventId);
    }

    @Override
    public List<CompilationDto> get(Boolean pinned, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size);
        List<Compilation> compilations = compilationRepository.findCompilations(pinned, pageable);

        if (compilations.isEmpty()) {
            return List.of();
        }

        Set<Long> eventIds = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());

        Map<Long, Event> eventsById = eventIds.isEmpty() ? Map.of()
                : eventRepository.findAllById(eventIds).stream()
                .collect(Collectors.toMap(Event::getId, event -> event));

        Map<Long, Long> confirmedByEventId = getConfirmedRequestsCountByEventId(new ArrayList<>(eventIds));

        return compilations.stream()
                .map(compilation -> {
                    List<Event> events = compilation.getEvents().stream()
                            .map(eventsById::get)
                            .filter(Objects::nonNull)
                            .toList();
                    return CompilationMapper.mapToCompilationDto(compilation, events, confirmedByEventId);
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto get(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%d was not found", compId)));
        List<Event> events = compilation.getEvents().isEmpty()
                ? List.of()
                : eventRepository.findAllById(compilation.getEvents());
        Map<Long, Long> confirmedByEventId = getConfirmedRequestsCountByEventId(
                events.stream().map(Event::getId).toList()
        );
        return CompilationMapper.mapToCompilationDto(compilation, events, confirmedByEventId);
    }
}