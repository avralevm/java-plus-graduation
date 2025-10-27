package ru.practicum.comments.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.in.CommentAdminParam;
import ru.practicum.comments.dto.output.CommentFullDto;
import ru.practicum.comments.model.StateFilter;
import ru.practicum.comments.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
public class AdminCommentsController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getComments(
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(0) Integer size,
            @RequestParam(defaultValue = "ALL") StateFilter state,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd) {

        log.info("Get comments by admin with state = {} from {} to {} ", state, rangeStart, rangeEnd);
        CommentAdminParam param = new CommentAdminParam(from, size, state, rangeStart, rangeEnd);
        return commentService.getComments(param);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto update(@PathVariable Long commentId,
                                 @RequestParam(required = true) String action) {
        log.info("Update ({}) comment with id {} by admin", action, commentId);
        return commentService.update(commentId, action);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        log.info("Delete comment with commentId {} by admin.", commentId);
        commentService.delete(commentId);
    }
}

