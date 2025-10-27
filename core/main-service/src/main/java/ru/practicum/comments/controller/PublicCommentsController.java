package ru.practicum.comments.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.in.CommentPublicParam;
import ru.practicum.comments.dto.output.CommentFullDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
public class PublicCommentsController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getCommentsByEventId(@PathVariable Long eventId,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "10") @Min(0) Integer size) {
        log.info("Get comments by public user for event with id {}.", eventId);
        CommentPublicParam param = new CommentPublicParam(eventId, from, size);
        return commentService.getCommentsByEventId(param);
    }
}