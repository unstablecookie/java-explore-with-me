package ru.practicum.ewm.requests;

import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserRequestsForEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateUserRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateResult,
                                                      Long userId, Long eventId);
}
