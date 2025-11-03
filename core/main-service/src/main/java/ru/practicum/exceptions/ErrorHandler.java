package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.ErrorResponse;
import ru.practicum.exception.IncorrectlyMadeRequestException;
import ru.practicum.exception.OperationNotAllowedException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoHavePermissionException.class)
    public ResponseEntity<ErrorResponse> handleNoHavePermission(NoHavePermissionException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "This user no have permission to access this object",
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(FORMATTER)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(DateException.class)
    public ResponseEntity<ErrorResponse> handleDateException(DateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(FORMATTER)
        );
        log.error("DateException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {

        ErrorResponse response = new ErrorResponse(
                "Forbidden operation",
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(FORMATTER)
        );

        log.error("Forbidden error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}