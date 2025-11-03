package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.exception.DuplicateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.storage.UserRepository;
import ru.practicum.user.in.NewUserRequest;
import ru.practicum.user.in.UserAdminParam;
import ru.practicum.user.output.UserDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll(UserAdminParam params) {
        if (params.getSize() == 0) {
            if (params.getIds() != null && !params.getIds().isEmpty()) {
                return repository.findAllByIdIn(params.getIds()).stream()
                        .skip(params.getFrom())
                        .map(mapper::toUserDto)
                        .toList();
            } else {
                return repository.findAll().stream()
                        .skip(params.getFrom())
                        .map(mapper::toUserDto)
                        .toList();
            }

        } else if (params.getFrom() < params.getSize()) {
            Page<User> usersPage;
            int pageNumber = params.getFrom() / params.getSize();
            Pageable pageable = PageRequest.of(pageNumber, params.getSize());

            if (params.getIds() != null && !params.getIds().isEmpty()) {
                usersPage = repository.findAllByIdIn(params.getIds(), pageable);
            } else {
                usersPage = repository.findAll(pageable);
            }

            return usersPage.stream()
                    .map(mapper::toUserDto)
                    .toList();
        } else {
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getByIds(List<Long> ids) {
        List<User> users = repository.findAllByIdIn(ids);
        return users.stream()
                .map(mapper::toUserDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", id));
        }
        return mapper.toUserDto(user.get());
    }

    @Transactional
    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        if (repository.existsByEmail(newUserRequest.getEmail())) {
            throw new DuplicateException("Email already exists: " + newUserRequest.getEmail());
        }
        User user = repository.save(mapper.toUser(newUserRequest));
        log.info("User was created: {}", user);
        return mapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("User with id=%d was not found", id));
        }
        repository.deleteById(id);
        log.info("User with id={}, was deleted", id);
    }
}