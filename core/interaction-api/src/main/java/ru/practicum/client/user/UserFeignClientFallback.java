package ru.practicum.client.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.user.output.UserDto;

import java.util.List;

@Component
public class UserFeignClientFallback implements UserAdminFeignClient {
    @Override
    public List<UserDto> getByIds(List<Long> ids) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: RequestService не доступен");
    }

    @Override
    public UserDto getById(Long id) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallback response: RequestService не доступен");
    }
}