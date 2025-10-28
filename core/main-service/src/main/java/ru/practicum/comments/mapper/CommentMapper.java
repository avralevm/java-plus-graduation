package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comments.dto.in.NewCommentDto;
import ru.practicum.comments.dto.output.CommentFullDto;
import ru.practicum.comments.dto.output.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

@Mapper(componentModel = "spring", uses = {EventMapper.class, UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "event", source = "event")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "modifiedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true)
    Comment toComment(NewCommentDto commentDto, Event event, User user);

    CommentFullDto toCommentDto(Comment comment);

    CommentShortDto toCommentShortDto(Comment comment);
}