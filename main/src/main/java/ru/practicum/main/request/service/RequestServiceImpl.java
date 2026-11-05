package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> get(long userId, long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        return requestRepository.findByEventId(eventId)
                .stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult update(long userId, long eventId, EventRequestStatusUpdateRequest statusRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        List<Request> requests = requestRepository.findByEventIdAndIdIn(eventId, statusRequest.getRequestIds());

        // статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Status can only be changed for requests that are in a pending state");
            }
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        List<Request> toSave = new ArrayList<>(requests);
        if (statusRequest.getStatus() == RequestStatus.REJECTED) {
            for (Request request : requests) {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(RequestMapper.mapToRequestDto(request));
                toSave.add(request);
            }
        } else { // CONFIRMED
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            int limit = event.getParticipantLimit();

            // если для события лимит заявок равен 0 или отключена пре-модерация заявок,
            // то подтверждение заявок не требуется
            if (!event.isRequestModeration() || limit == 0) {
                return new EventRequestStatusUpdateResult(confirmed, rejected);
            }

            // нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
            // ожидается код ошибки 409
            if (limit > 0 && confirmedCount >= limit) {
                throw new ConflictException("Participation requests limit requests reached.");
            }

            for (Request request : requests) {
                // если при подтверждении данной заявки, лимит заявок для события исчерпан,
                // то все неподтверждённые заявки необходимо отклонить
                if (limit > 0 && confirmedCount >= limit) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(RequestMapper.mapToRequestDto(request));
                    toSave.add(request);
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedCount++;
                    confirmed.add(RequestMapper.mapToRequestDto(request));
                    toSave.add(request);
                }
            }
        }
        requestRepository.saveAll(toSave);
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> get(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        return requestRepository.findByRequesterIdAndEventInitiatorIdNot(userId)
                .stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto add(long userId, long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot add a request to participate in his event.");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event must be published.");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists.");
        }

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmed >= event.getParticipantLimit()) {
            throw new ConflictException("Participation requests limit requests reached.");
        }
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(!event.isRequestModeration() || event.getParticipantLimit() == 0
                ? RequestStatus.CONFIRMED : RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto update(long userId, long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        return RequestMapper.mapToRequestDto(request);
    }
}