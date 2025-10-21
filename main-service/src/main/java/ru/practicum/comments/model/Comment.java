package ru.practicum.comments.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String text;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    User author;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "modified_on")
    LocalDateTime modifiedOn;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    State state;
}