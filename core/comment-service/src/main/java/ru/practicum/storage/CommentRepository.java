package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;
import ru.practicum.event.state.State;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventIdAndAuthorIdAndState(Long eventId, Long authorId, State state, Pageable pageable);

    List<Comment> findByEventIdAndState(Long eventId, State state, Pageable pageable);

    List<Comment> findByEventIdAndState(Long eventId, State state);

    List<Comment> findByEventIdAndAuthorIdAndState(Long eventId, Long authorId, State state);

    List<Comment> findByAuthorIdAndState(Long eventId, State status, Pageable pageable);

    List<Comment> findByAuthorIdAndState(Long eventId, State status);

    List<Comment> findByAuthorId(Long eventId);

    List<Comment> findByAuthorId(Long eventId, Pageable pageable);

    List<Comment> findByCreatedOnBetween(LocalDateTime start, LocalDateTime end);

    List<Comment> findByCreatedOnBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Comment> findByStateAndCreatedOnBetween(State state, LocalDateTime start, LocalDateTime end);

    List<Comment> findByStateAndCreatedOnBetween(State state, LocalDateTime start, LocalDateTime end, Pageable pageable);
}