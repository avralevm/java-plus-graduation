package ru.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorResponse;
import ru.practicum.exception.NotFoundException;

import java.io.IOException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = response.body() != null ? new String(response.body().asInputStream().readAllBytes()) : "";
            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
            HttpStatus status = HttpStatus.valueOf(response.status());

            return switch (status) {
                case NOT_FOUND -> new NotFoundException(error.getMessage());
                case CONFLICT -> new ConflictException(error.getMessage());
                default -> new RuntimeException("Feign error: " + error.getMessage());
            };
        } catch (IOException e) {
            return new RuntimeException("Error decoding Feign response", e);
        }
    }
}