package ru.practicum.comments.service;

import ru.practicum.comments.dto.in.*;
import ru.practicum.comments.dto.output.CommentFullDto;
import ru.practicum.comments.dto.output.CommentShortDto;

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