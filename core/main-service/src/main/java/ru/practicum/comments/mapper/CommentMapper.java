package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.in.NewCommentDto;
import ru.practicum.comment.output.CommentFullDto;
import ru.practicum.comment.output.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CommentMapper {

    @Mapping(target = "event", source = "event")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "modifiedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true)
    Comment toComment(NewCommentDto commentDto, Event event, Long authorId);

    CommentFullDto toCommentDto(Comment comment);

    CommentShortDto toCommentShortDto(Comment comment);
}