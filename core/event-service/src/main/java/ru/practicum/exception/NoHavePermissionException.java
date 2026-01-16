package ru.practicum.exception;

public class NoHavePermissionException extends RuntimeException {
    public NoHavePermissionException(String message) {
        super(message);
    }
}
