package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.in.CommentParam;
import ru.practicum.comment.in.GetCommentParam;
import ru.practicum.comment.in.NewCommentDto;
import ru.practicum.comment.output.CommentFullDto;
import ru.practicum.comment.output.CommentShortDto;
import ru.practicum.comment.StateFilter;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
public class UserCommentsController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto create(@Valid @RequestBody NewCommentDto newCommentDto,
                                  @PathVariable Long userId,
                                  @PathVariable Long eventId) {
        return commentService.create(newCommentDto, userId, eventId);
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long eventId,
                       @PathVariable Long commentId) {
        log.info("Delete comment with commentId {} user with userId {} for event with eventId {}.", commentId, userId, eventId);
        CommentParam param = new CommentParam(userId, eventId, commentId);
        commentService.delete(param);
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto update(@Valid @RequestBody NewCommentDto newCommentDto,
                                 @PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @PathVariable Long commentId) {
        log.info("Update comment with commentId {} user with userId {} for event with eventId {}.", commentId, userId, eventId);
        CommentParam param = new CommentParam(userId, eventId, commentId);
        return commentService.update(newCommentDto, param);
    }

    @GetMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto getComment(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @PathVariable Long commentId) {
        CommentParam param = new CommentParam(userId, eventId, commentId);
        return commentService.getComment(param);
    }

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getCommentsByEventId(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "10") @Min(0) Integer size) {
        log.info("Get comments user with userId {} for event with eventId {}.", userId, eventId);
        GetCommentParam param = new GetCommentParam(userId, from, size, null);
        return commentService.getCommentsByEventId(eventId, param);
    }

    @GetMapping("/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getComments(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "10") @Min(0) Integer size,
                                            @RequestParam(defaultValue = "ALL") StateFilter state) {
        log.info("Get comments user with userId {}", userId);
        GetCommentParam param = new GetCommentParam(userId, from, size, state);
        return commentService.getComments(param);
    }
}