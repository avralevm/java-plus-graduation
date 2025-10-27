package ru.practicum.users.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.in.NewUserRequest;
import ru.practicum.users.dto.in.UserAdminParam;
import ru.practicum.users.dto.output.UserDto;
import ru.practicum.users.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                @Positive @RequestParam(required = false, defaultValue = "10") @Min(0) int size) {
        return service.getAll(new UserAdminParam(ids, from, size));
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
