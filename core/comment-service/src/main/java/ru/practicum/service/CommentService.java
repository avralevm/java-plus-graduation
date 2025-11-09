package ru.practicum.service;

import ru.practicum.comment.in.*;
import ru.practicum.comment.output.CommentFullDto;
import ru.practicum.comment.output.CommentShortDto;

import java.util.List;

public interface CommentService {
    CommentShortDto create(NewCommentDto newCommentDto, Long userId, Long eventId);

    void delete(CommentParam param);

    void delete(Long commentId);

    CommentFullDto update(NewCommentDto newComment, CommentParam param);

    CommentFullDto update(Long commentId, String filter);

    CommentFullDto getComment(CommentParam param);

    List<CommentFullDto> getCommentsByEventId(Long eventId, GetCommentParam param);

    List<CommentFullDto> getCommentsByEventId(CommentPublicParam param);

    List<CommentFullDto> getComments(GetCommentParam param);

    List<CommentFullDto> getComments(CommentAdminParam param);
}