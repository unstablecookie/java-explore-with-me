package ru.practicum.ewm.error;

public class RequestWrongStatusException extends RuntimeException {
    private String reason;

    public RequestWrongStatusException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
