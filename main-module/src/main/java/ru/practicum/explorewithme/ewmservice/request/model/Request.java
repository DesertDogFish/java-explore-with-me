package ru.practicum.explorewithme.ewmservice.request.model;

import lombok.*;
import ru.practicum.explorewithme.ewmservice.event.model.Event;
import ru.practicum.explorewithme.ewmservice.request.model.enums.RequestStatus;
import ru.practicum.explorewithme.ewmservice.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(exclude = {"id", "event", "requester"})
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status", nullable = false)
    private RequestStatus status;
}
