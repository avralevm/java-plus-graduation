package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.output.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "internal/api/users", configuration = FeignConfig.class)
public interface UserAdminFeignClient {
    @GetMapping("/by-ids")
    List<UserDto> getByIds(@RequestParam List<Long> ids);

    @GetMapping("/{id}")
    UserDto getById(@PathVariable("id") Long id);
}