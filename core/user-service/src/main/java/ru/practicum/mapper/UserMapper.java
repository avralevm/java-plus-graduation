package ru.practicum.mapper;

import org.mapstruct.Mapper;

import ru.practicum.model.User;
import ru.practicum.user.in.NewUserRequest;
import ru.practicum.user.output.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);
}