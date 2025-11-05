package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.UserAdminFeignClient;
import ru.practicum.service.UserService;
import ru.practicum.user.output.UserDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("internal/api/users")
public class UserClientController implements UserAdminFeignClient {
    private final UserService service;

    @GetMapping("/by-ids")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getByIds(@RequestParam List<Long> ids) {
        return service.getByIds(ids);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getById(@PathVariable Long id) {
        return service.getById(id);
    }
}