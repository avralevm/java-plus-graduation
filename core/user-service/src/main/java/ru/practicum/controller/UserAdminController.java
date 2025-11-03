package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.client.UserAdminFeignClient;
import ru.practicum.service.UserService;
import ru.practicum.user.in.NewUserRequest;
import ru.practicum.user.in.UserAdminParam;
import ru.practicum.user.output.UserDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
public class UserAdminController implements UserAdminFeignClient {
    private final UserService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                @Positive @RequestParam(required = false, defaultValue = "10") @Min(0) int size) {
        return service.getAll(new UserAdminParam(ids, from, size));
    }

    @GetMapping("/by-ids")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getByIds(@RequestParam List<Long> ids) {
        return service.getByIds(ids);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest newUserRequest) {
        return service.add(newUserRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}