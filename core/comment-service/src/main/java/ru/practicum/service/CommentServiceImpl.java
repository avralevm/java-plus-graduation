package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventFeignClient;
import ru.practicum.client.UserAdminFeignClient;
import ru.practicum.comment.in.*;
import ru.practicum.comment.output.CommentFullDto;
import ru.practicum.comment.output.CommentShortDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.comment.StateFilter;
import ru.practicum.storage.CommentRepository;
import ru.practicum.event.state.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ConflictException;
import ru.practicum.user.output.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserAdminFeignClient userAdminFeignClient;
    private final EventFeignClient eventFeignClient;

    public CommentShortDto create(NewCommentDto newCommentDto, Long userId, Long eventId) {
        UserDto user = userAdminFeignClient.getById(userId);
        EventShortDto event = eventFeignClient.getEventShortById(eventId);

        Comment comment = commentMapper.toComment(newCommentDto, event.getId(), user.getId());
        if (comment.getState() == null) {
            comment.setState(State.PENDING);
        }
        comment = commentRepository.save(comment);

        return commentMapper.toCommentShortDto(comment, user, event);
    }

    public void delete(CommentParam param) {
        userAdminFeignClient.getById(param.getUserId());
        eventFeignClient.getEventShortById(param.getEventId());

        Comment comment = checkCommentIfExists(param.getCommentId());

        if (!comment.getAuthorId().equals(param.getUserId())) {
            throw new ForbiddenException("User with id " + param.getUserId() + " is not author of comment " + comment.getId());
        }

        commentRepository.deleteById(param.getCommentId());
        log.info("Comment {} was deleted", comment);
    }

    public void delete(Long commentId) {
        checkCommentIfExists(commentId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id = {} was deleted by admin", commentId);
    }

    public CommentFullDto update(NewCommentDto newComment, CommentParam param) {
        UserDto user = userAdminFeignClient.getById(param.getUserId());
        EventShortDto event = eventFeignClient.getEventShortById(param.getEventId());

        Comment existingComment = checkCommentIfExists(param.getCommentId());

        if (!existingComment.getAuthorId().equals(param.getUserId())) {
            throw new ForbiddenException("User with id " + param.getUserId() + " is not author of comment " + existingComment.getId());
        }

        if (existingComment.getState() == State.PUBLISHED) {
            existingComment.setState(State.PENDING);
        } else if (existingComment.getState() == State.CANCELED) {
            throw new ConflictException("Cannot update comment with id: " + existingComment.getId()
                    + " because status: " + existingComment.getState());
        }

        existingComment.setText(newComment.getText());

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment was updated with id={}, old name='{}', new name='{}'",
                param.getCommentId(), existingComment.getText(), newComment.getText());
        return commentMapper.toCommentDto(updatedComment, user, event);
    }

    public CommentFullDto update(Long commentId, String filter) {
        State stateForUpdating = toState(filter);
        Comment existingComment = checkCommentIfExists(commentId);

        if (existingComment.getState() != State.PENDING) {
            throw new ConflictException("Cannot update comment with state not PENDING");
        }

        existingComment.setState(stateForUpdating);

        if (stateForUpdating.equals(State.PUBLISHED)) {
            if (existingComment.getPublishedOn() != null) {
                existingComment.setModifiedOn(LocalDateTime.now());
            } else {
                existingComment.setPublishedOn(LocalDateTime.now());
            }
        }

        Comment updatedComment = commentRepository.save(existingComment);

        UserDto user = userAdminFeignClient.getById(updatedComment.getAuthorId());
        EventShortDto event = eventFeignClient.getEventShortById(updatedComment.getEventId());

        log.info("Comment with id={} was updated with status {}", commentId, stateForUpdating);
        return commentMapper.toCommentDto(updatedComment, user, event);
    }

    public CommentFullDto getComment(CommentParam param) {
        UserDto user = userAdminFeignClient.getById(param.getUserId());
        EventShortDto event = eventFeignClient.getEventShortById(param.getEventId());

        Comment comment = checkCommentIfExists(param.getCommentId());

        if (!comment.getAuthorId().equals(param.getUserId()) && comment.getState() != State.PUBLISHED) {
            throw new ForbiddenException("Cannot get comment with id: " + param.getEventId()
                    + " because it's not status published: " + comment.getState());
        }

        if (!comment.getAuthorId().equals(param.getUserId())) {
            comment.setPublishedOn(null);
            comment.setState(null);
        }
        return commentMapper.toCommentDto(comment, user, event);
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsByEventId(Long eventId, GetCommentParam param) {
        Long userId = param.getUserId();
        Integer from = param.getFrom();
        Integer size = param.getSize();
        List<Comment> comments;

        EventShortDto event = eventFeignClient.getEventShortById(eventId);
        UserDto user = userAdminFeignClient.getById(userId);

        if (size == 0) {
            comments = commentRepository.findByEventIdAndAuthorIdAndState(userId, eventId, State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < size && size > 0) {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            comments = commentRepository.findByEventIdAndAuthorIdAndState(eventId, userId, State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }

        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(comment, user, event))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsByEventId(CommentPublicParam param) {
        EventShortDto event = eventFeignClient.getEventShortById(param.getEventId());

        Integer from = param.getFrom();
        Integer size = param.getSize();
        List<Comment> comments;

        if (size == 0) {
            comments = commentRepository.findByEventIdAndState(param.getEventId(), State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < size && size > 0) {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            comments = commentRepository.findByEventIdAndState(param.getEventId(), State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }

        List<Long> userIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .toList();
        List<UserDto> users = userAdminFeignClient.getByIds(userIds);
        Map<Long, UserDto> userMap = users.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));

        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(comment, userMap.get(comment.getAuthorId()), event))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getComments(GetCommentParam param) {
        UserDto user = userAdminFeignClient.getById(param.getUserId());

        List<Comment> comments;
        if (param.getSize() == 0) {
            comments = getCommentsWithoutPagination(param);
        } else if (param.getFrom() < param.getSize() && param.getSize() > 0) {
            comments = getCommentsWithPagination(param);
        } else {
            return List.of();
        }

        List<Long> eventIds = comments.stream()
                .map(Comment::getEventId)
                .distinct()
                .toList();
        Map<Long, EventShortDto> eventMap = getEvents(eventIds);

        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(comment, user, eventMap.get(comment.getEventId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getComments(CommentAdminParam param) {
        dateValidation(param);

        List<Comment> comments;
        if (param.getSize() == 0) {
            comments = getCommentsWithoutPagination(param);
        } else if (param.getFrom() < param.getSize() && param.getSize() > 0) {
            comments = getCommentsWithPagination(param);
        } else {
            return List.of();
        }

        List<Long> userIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .toList();
        Map<Long, UserDto> userMap = getUsers(userIds);

        List<Long> eventIds = comments.stream()
                .map(Comment::getEventId)
                .distinct()
                .toList();
        Map<Long, EventShortDto> eventMap = getEvents(eventIds);
        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(comment, userMap.get(comment.getAuthorId()), eventMap.get(comment.getEventId())))
                .toList();
    }

    private Map<Long, UserDto> getUsers(List<Long> userIds) {
        return userAdminFeignClient.getByIds(userIds).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }

    private Map<Long, EventShortDto> getEvents(List<Long> eventIds) {
        return eventFeignClient.getEventByIds(eventIds).stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));
    }

    private List<Comment> getCommentsWithoutPagination(GetCommentParam param) {
        List<Comment> result = param.getStatus() == StateFilter.ALL
                ? commentRepository.findByAuthorId(param.getUserId())
                : commentRepository.findByAuthorIdAndState(param.getUserId(), toState(param.getStatus()));

        return result.stream()
                .skip(param.getFrom())
                .toList();
    }

    private List<Comment> getCommentsWithPagination(GetCommentParam param) {
        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return param.getStatus() == StateFilter.ALL
                ? commentRepository.findByAuthorId(param.getUserId(), pageRequest)
                : commentRepository.findByAuthorIdAndState(param.getUserId(), toState(param.getStatus()), pageRequest);
    }

    private List<Comment> getCommentsWithoutPagination(CommentAdminParam param) {
        List<Comment> result = param.getStatus() == StateFilter.ALL
                ? commentRepository.findByCreatedOnBetween(param.getStart(), param.getEnd())
                : commentRepository.findByStateAndCreatedOnBetween(
                toState(param.getStatus()), param.getStart(), param.getEnd());

        return result.stream()
                .skip(param.getFrom())
                .toList();
    }

    private List<Comment> getCommentsWithPagination(CommentAdminParam param) {
        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return param.getStatus() == StateFilter.ALL
                ? commentRepository.findByCreatedOnBetween(param.getStart(), param.getEnd(), pageRequest)
                : commentRepository.findByStateAndCreatedOnBetween(
                toState(param.getStatus()), param.getStart(), param.getEnd(), pageRequest);
    }

    private State toState(StateFilter filter) {
        return switch (filter) {
            case PENDING -> State.PENDING;
            case PUBLISHED -> State.PUBLISHED;
            case CANCELED -> State.CANCELED;
            case ALL -> throw new IllegalArgumentException("ALL is not a valid State");
        };
    }

    private State toState(String filter) {
        return switch (filter) {
            case "APPROVE" -> State.PUBLISHED;
            case "REJECT" -> State.CANCELED;
            default -> throw new IllegalArgumentException(
                    "Parameter action must be APPROVE or REJECT, but action = " + filter);
        };
    }

    private Comment checkCommentIfExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));
    }

    private static void dateValidation(CommentAdminParam param) {
        if (param.getStart().isAfter(param.getEnd())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
}