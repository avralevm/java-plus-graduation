package ru.practicum.client.user;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.FeignConfig;
import ru.practicum.user.output.UserDto;

import javax.naming.ServiceUnavailableException;
import java.util.List;

@FeignClient(name = "user-service",
        path = "internal/api/users",
        configuration = FeignConfig.class,
        fallback = UserFeignClientFallback.class)
public interface UserAdminFeignClient {
    @GetMapping("/by-ids")
    List<UserDto> getByIds(@RequestParam @UniqueElements List<Long> ids);

    @GetMapping("/{id}")
    UserDto getById(@PathVariable("id") Long id);
}