package ru.practicum.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.users.dto.in.NewUserRequest;

import ru.practicum.users.dto.output.UserDto;
import ru.practicum.users.dto.output.UserShortDto;
import ru.practicum.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
