package ru.practicum.ewm.error;

public class UserWrongFieldsException extends RuntimeException {
    private String reason;

    public UserWrongFieldsException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
