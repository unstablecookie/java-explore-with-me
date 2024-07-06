package ru.practicum.ewm.requests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "event")
    private Long event;
    @Column(name = "requester")
    private Long requester;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;
}
