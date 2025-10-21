package ru.practicum.users.service;

import ru.practicum.users.dto.in.NewUserRequest;
import ru.practicum.users.dto.in.UserAdminParam;
import ru.practicum.users.dto.output.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll(UserAdminParam params);

    UserDto add(NewUserRequest newUserRequest);

    void delete(Long id);
}
