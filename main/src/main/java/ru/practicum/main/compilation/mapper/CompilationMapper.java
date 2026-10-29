package ru.practicum.main.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationRequest;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static Compilation mapToCompilation(NewCompilationRequest newCompilationRequest, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events.stream().map(Event::getId).collect(Collectors.toSet()));
        compilation.setPinned(newCompilationRequest.isPinned());
        compilation.setTitle(newCompilationRequest.getTitle());
        return compilation;
    }

    public static CompilationDto mapToCompilationDto(Compilation compilation, List<Event> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(events.stream().map(EventMapper::mapToEventShortDto).toList())
                .build();
    }

    public static Compilation updateCompilationFields(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.hasEvents()) {
            compilation.setEvents(new HashSet<>(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.hasPinned()) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.hasTitle()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        return compilation;
    }
}