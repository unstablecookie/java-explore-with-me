package ru.practicum.ewm.error;

public class IntegrityConflictException extends RuntimeException {
    private String reason;

    public IntegrityConflictException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
