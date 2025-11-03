package ru.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.in.NewUserRequest;
import ru.practicum.user.output.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserAdminFeignClient {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getAll(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                         @Positive @RequestParam(required = false, defaultValue = "10") @Min(0) int size);

    @GetMapping("/by-ids")
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getByIds(@RequestParam List<Long> ids);

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    UserDto getById(@PathVariable("id") Long id);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto add(@Valid @RequestBody NewUserRequest newUserRequest);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") Long id);
}