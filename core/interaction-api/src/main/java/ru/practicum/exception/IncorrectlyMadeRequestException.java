package ru.practicum.exception;

public class IncorrectlyMadeRequestException extends RuntimeException {
    public IncorrectlyMadeRequestException(String message) {
        super(message);
    }
}
