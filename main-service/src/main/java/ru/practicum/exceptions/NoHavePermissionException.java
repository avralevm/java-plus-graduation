package ru.practicum.exceptions;

public class NoHavePermissionException extends RuntimeException {
    public NoHavePermissionException(String message) {
        super(message);
    }
}
