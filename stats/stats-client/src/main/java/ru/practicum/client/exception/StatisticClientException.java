package ru.practicum.client.exception;

public class StatisticClientException extends RuntimeException {
    public StatisticClientException(String message) {
        super(message);
    }

    public StatisticClientException(String message, Throwable cause) {
        super(message, cause);
    }
}