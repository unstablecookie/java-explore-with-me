package ru.practicum.ewm.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.EntityNotFoundException;
import ru.practicum.ewm.error.InputParametersException;
import ru.practicum.ewm.error.IntegrityConflictException;
import ru.practicum.ewm.error.RequestWrongStatusException;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.State;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.dto.ParticipationRequestMapper;
import ru.practicum.ewm.requests.model.ParticipationRequest;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.users.UserRepository;
import ru.practicum.ewm.users.model.User;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d was not found", userId),
                        "The required object was not found.")
        );
        return requestRepository.findAllByRequester(userId).stream()
                .map(x -> ParticipationRequestMapper.toParticipationRequestDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d was not found", userId),
                        "The required object was not found.")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId),
                        "The required object was not found.")
        );
        if (event.getInitiator().getId().equals(userId)) {
            throw new IntegrityConflictException("Initiator cannot be a participant", "Integrity constraint has been violated.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IntegrityConflictException("Event was not published yet", "Integrity constraint has been violated.");
        }
        if (event.getParticipantLimit() != 0 && requestRepository.countByEventAndStatusIs(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new IntegrityConflictException("Participant limit exceed", "Integrity constraint has been violated.");
        }
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now(ZoneOffset.UTC))
                .event(eventId)
                .requester(userId)
                .status(RequestStatus.PENDING)
                .build();
        if (event.getRequestModeration().equals(Boolean.FALSE) || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        }
        participationRequest = requestRepository.save(participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = requestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Request with id=%d was not found", requestId),
                        "The required object was not found.")
        );
        participationRequest.setStatus(RequestStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequestsForEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId),
                        "The required object was not found.")
        );
        if (!event.getInitiator().getId().equals(userId)) {
            throw new InputParametersException(String.format("User id: %d is not an initiator of event id %d", userId, eventId),
                    "Incorrectly made request.");
        }
        return requestRepository.findAllByEvent(eventId).stream()
                .map(x -> ParticipationRequestMapper.toParticipationRequestDto(x))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateUserRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                      Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId),
                        "The required object was not found.")
        );
        List<ParticipationRequest> requests = requestRepository.findAllByEventAndIdIn(eventId,
                eventRequestStatusUpdateRequest.getRequestIds());
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(Boolean.FALSE)) {
            return new EventRequestStatusUpdateResult();
        }
        if (requests.stream().anyMatch(x -> !x.getStatus().equals(RequestStatus.PENDING))) {
            throw new RequestWrongStatusException("Request must have status PENDING", "Incorrectly made request.");
        }
        if (event.getParticipantLimit() != 0 && requestRepository.countByEventAndStatusIs(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new IntegrityConflictException("The participant limit has been reached",
                    "For the requested operation the conditions are not met.");
        }
        updateRequestStatus(eventRequestStatusUpdateRequest.getStatus(), requests, event);
        requestRepository.saveAll(requests);
        eventRepository.save(event);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        requests.stream()
                .map(x -> ParticipationRequestMapper.toParticipationRequestDto(x))
                .forEach(x -> {
            if (x.getStatus().equals(RequestStatus.CONFIRMED)) {
                result.getConfirmedRequests().add(x);
            } else if (x.getStatus().equals(RequestStatus.REJECTED)) {
                result.getRejectedRequests().add(x);
            }
        });
        return result;
    }

    private void updateRequestStatus(RequestStatus status, List<ParticipationRequest> requests, Event event) {
        if (status.equals(RequestStatus.CONFIRMED)) {
            requests.stream().forEach(x -> {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    x.setStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                    x.setStatus(RequestStatus.REJECTED);
                }
            });
        } else {
            requests.stream().forEach(x -> x.setStatus(status));
        }
    }
}
