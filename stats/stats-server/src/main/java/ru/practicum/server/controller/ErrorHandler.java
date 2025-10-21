package ru.practicum.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

@RestControllerAdvice("ru.practicum.server.controller")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(IllegalArgumentException ex) {
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Bad request",
                ex.getMessage(),
                Arrays.toString(ex.getStackTrace()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e, HttpStatus status) {
        log.info("500 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ApiError(status, "Error ....", e.getMessage(), stackTrace);
    }
}

