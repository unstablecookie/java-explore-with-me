package ru.practicum.ewm.error;

public class EventStatusException extends RuntimeException {
    private String reason;

    public EventStatusException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
