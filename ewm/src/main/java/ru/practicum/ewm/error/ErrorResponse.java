package ru.practicum.ewm.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;
import java.util.Date;

public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String reason;
    private int code;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;

    public ErrorResponse(HttpStatus httpStatus, String message, String reason) {
        timestamp = new Date();
        this.code = httpStatus.value();
        this.status = httpStatus.name();
        this.message = message;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return message + timestamp;
    }
}
