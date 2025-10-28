package ru.practicum.requests.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    User requester;
    @Column
    @Enumerated(EnumType.STRING)
    Status status;
    @Column
    LocalDateTime created;
}
