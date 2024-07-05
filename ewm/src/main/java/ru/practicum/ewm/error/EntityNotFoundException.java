package ru.practicum.ewm.error;

public class EntityNotFoundException extends RuntimeException {
    private String reason;

    public EntityNotFoundException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
