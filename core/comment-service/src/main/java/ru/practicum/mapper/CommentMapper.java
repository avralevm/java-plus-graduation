package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.in.NewCommentDto;
import ru.practicum.comment.output.CommentFullDto;
import ru.practicum.comment.output.CommentShortDto;
import ru.practicum.event.output.EventShortDto;
import ru.practicum.model.Comment;
import ru.practicum.user.output.UserDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "modifiedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true)
    Comment toComment(NewCommentDto commentDto, Long eventId, Long authorId);

    @Mapping(source = "comment.id", target = "id")
    CommentFullDto toCommentDto(Comment comment, UserDto author, EventShortDto event);

    @Mapping(source = "comment.id", target = "id")
    CommentShortDto toCommentShortDto(Comment comment, UserDto author, EventShortDto event);
}