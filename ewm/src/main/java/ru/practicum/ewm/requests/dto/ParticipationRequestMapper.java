package ru.practicum.ewm.requests.dto;

import ru.practicum.ewm.requests.model.ParticipationRequest;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .created(participationRequest.getCreated() != null ? participationRequest.getCreated() : null)
                .event(participationRequest.getEvent() != null ? participationRequest.getEvent() : null)
                .id(participationRequest.getId() != null ? participationRequest.getId() : null)
                .requester(participationRequest.getRequester() != null ? participationRequest.getRequester() : null)
                .status(participationRequest.getStatus() != null ? participationRequest.getStatus() : null)
                .build();
    }
}
