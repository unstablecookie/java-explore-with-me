package ru.practicum.statistics.server.error;

public class InputParametersException extends RuntimeException {
    private String reason;

    public InputParametersException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
