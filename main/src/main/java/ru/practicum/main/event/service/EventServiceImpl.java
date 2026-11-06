package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.pagination.FromSizeRequest;
import ru.practicum.main.request.model.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private Category getCategory(Long categoryId) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Category with id=%d was not found", categoryId)));
        } else {
            return null;
        }
    }

    private Map<Long, Long> getConfirmedStatusGroupByEventId(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        return requestRepository
                .countConfirmedByEventIds(eventIds, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> get(long userId, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size);

        List<Event> events = eventRepository.findEventsByInitiator(userId, pageable);
        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> confirmedByEventId = getConfirmedStatusGroupByEventId(events);

        return events.stream()
                .map(event -> EventMapper.mapToEventShortDto(
                        event,
                        confirmedByEventId.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto add(long userId, NewEventRequest newEventRequest) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        Category category = categoryRepository.findById(newEventRequest.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id=%d was not found", newEventRequest.getCategory())));

        Event event = EventMapper.mapToEvent(newEventRequest, initiator, category);
        event = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto get(long userId, long eventId) {
        Event event = eventRepository.findByInitiatorIdAndIdWithCategoryAndInitiator(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        return EventMapper.mapToEventFullDto(event, confirmed);
    }

    @Override
    @Transactional
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByInitiatorIdAndIdWithCategoryAndInitiator(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        Category category = getCategory(updateEventUserRequest.getCategory());

        Event updatedEvent = EventMapper.updateEventFields(event, updateEventUserRequest, category);
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW -> updatedEvent.setState(EventState.PENDING);
                case CANCEL_REVIEW -> updatedEvent.setState(EventState.CANCELED);
            }
        }
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        return EventMapper.mapToEventFullDto(updatedEvent, confirmed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> get(List<Long> users,
                                  List<EventState> states,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  int from,
                                  int size
    ) {
        users = (users == null || users.isEmpty()) ? null : users;
        states = (states == null || states.isEmpty()) ? null : states;

        Pageable pageable = FromSizeRequest.of(from, size);

        List<Event> events;
        if (categories == null || categories.isEmpty()) {
            events = eventRepository.findByInitiatorInAndStateInAndEventDateBetween(
                    users,
                    states,
                    rangeStart,
                    rangeEnd,
                    pageable
            );
        } else {
            events = eventRepository.findByInitiatorInAndStateInAndCategoryInAndEventDateBetween(
                    users,
                    states,
                    categories,
                    rangeStart,
                    rangeEnd,
                    pageable
            );
        }

        Map<Long, Long> confirmedByEventId = getConfirmedStatusGroupByEventId(events);

        return events.stream()
                .map(event -> EventMapper.mapToEventFullDto(
                        event,
                        confirmedByEventId.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto update(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
//        дата начала изменяемого события должна быть не ранее чем за час от даты публикации;
//        событие можно публиковать, только если оно в состоянии ожидания публикации;
//        событие можно отклонить, только если оно еще не опубликовано.
        if (updateEventAdminRequest.getEventDate() != null && event.getPublishedOn() != null
                && !updateEventAdminRequest.getEventDate().isBefore(event.getPublishedOn().plusHours(1L))
        ) {
            throw new ConflictException(String.format(
                    "Event date %tF %tT modified event must be no earlier than one hour from the publication date",
                    updateEventAdminRequest.getEventDate(), updateEventAdminRequest.getEventDate()));
        }
        if (updateEventAdminRequest.getStateAction() == AdminStateAction.PUBLISH_EVENT
                && event.getState() != EventState.PENDING
        ) {
            throw new ConflictException("Cannot publish the event because it's not in the right state: "
                    + event.getState());
        }
        if (updateEventAdminRequest.getStateAction() == AdminStateAction.REJECT_EVENT
                && event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot reject the event because it's not in the right state: "
                    + event.getState());
        }

        Category category = getCategory(updateEventAdminRequest.getCategory());

        Event updatedEvent = EventMapper.updateEventFields(event, updateEventAdminRequest, category);
        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT -> {
                    updatedEvent.setState(EventState.PUBLISHED);
                    updatedEvent.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> updatedEvent.setState(EventState.CANCELED);
            }
        }
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        return EventMapper.mapToEventFullDto(updatedEvent, confirmed);
    }
}