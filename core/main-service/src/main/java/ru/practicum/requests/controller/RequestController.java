package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDtoOut> getUserRequests(@PathVariable Long userId) {
        log.info("Received GET request for requests of user with id: {}", userId);

        List<ParticipationRequestDtoOut> requests = requestService.findByUserId(userId);
        log.info("Returning {} requests for user with id: {}", requests.size(), userId);

        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDtoOut createRequest(@PathVariable Long userId,
                                                    @RequestParam Long eventId) {
        log.info("Received POST request to create request for userId: {}, eventId: {}", userId, eventId);

        ParticipationRequestDtoOut request = requestService.create(userId, eventId);
        log.info("Created request with id: {} for userId: {}, eventId: {}", request.getId(), userId, eventId);

        return request;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDtoOut cancelRequest(@PathVariable Long userId,
                                                    @PathVariable Long requestId) {
        log.info("Received PATCH request to cancel request with id: {} for userId: {}", requestId, userId);

        ParticipationRequestDtoOut request = requestService.cancel(userId, requestId);
        log.info("Cancelled request with id: {} for userId: {}", request.getId(), userId);

        return request;
    }
}

