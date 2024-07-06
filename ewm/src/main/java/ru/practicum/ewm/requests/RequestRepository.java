package ru.practicum.ewm.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.requests.model.ParticipationRequest;
import ru.practicum.ewm.requests.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester(Long id);

    Long countByEventAndStatusIs(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByRequesterAndEvent(Long userId, Long eventId);

    List<ParticipationRequest> findAllByEventAndIdIn(Long eventId, List<Long> requestIds);

    List<ParticipationRequest> findAllByEvent(Long eventId);
}
