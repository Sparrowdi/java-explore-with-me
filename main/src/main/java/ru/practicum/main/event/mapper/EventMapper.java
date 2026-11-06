package ru.practicum.main.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventRequest;
import ru.practicum.main.event.dto.UpdateEventRequest;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    public static EventFullDto mapToEventFullDto(Event event, long confirmedRequests) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto mapToEventShortDto(Event event, long confirmedRequests) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Event mapToEvent(NewEventRequest newEventRequest, User initiator, Category category) {
        Event event = new Event();
        event.setTitle(newEventRequest.getTitle());
        event.setAnnotation(newEventRequest.getAnnotation());
        event.setDescription(newEventRequest.getDescription());
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
//        event.setPublishedOn(); // еще не опубликовано
        event.setEventDate(newEventRequest.getEventDate());
        event.setLocation(newEventRequest.getLocation());
        event.setPaid(newEventRequest.isPaid());
        event.setParticipantLimit(newEventRequest.getParticipantLimit());
        event.setRequestModeration(newEventRequest.isRequestModeration());
//        event.setViews(); // заполняем в entity
        return event;
    }

    public static Event updateEventFields(Event event, UpdateEventRequest updateEventRequest, Category category) {
        if (updateEventRequest.hasAnnotation()) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.hasCategory()) {
            event.setCategory(category);
        }
        if (updateEventRequest.hasDescription()) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.hasEventDate()) {
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.hasLocation()) {
            event.setLocation(updateEventRequest.getLocation());
        }
        if (updateEventRequest.hasPaid()) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.hasRequestModeration()) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.hasTitle()) {
            event.setTitle(updateEventRequest.getTitle());
        }
        return event;
    }
}