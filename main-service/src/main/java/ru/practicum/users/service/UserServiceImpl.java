package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.DuplicateException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.dto.in.NewUserRequest;
import ru.practicum.users.dto.in.UserAdminParam;
import ru.practicum.users.dto.output.UserDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.storage.UserRepository;

import java.util.List;

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