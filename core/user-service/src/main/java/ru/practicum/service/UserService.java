package ru.practicum.service;

import ru.practicum.user.in.NewUserRequest;
import ru.practicum.user.in.UserAdminParam;
import ru.practicum.user.output.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(UserAdminParam params);

    List<UserDto> getByIds(List<Long> ids);

    UserDto getById(Long id);

    UserDto add(NewUserRequest newUserRequest);

    void delete(Long id);
}