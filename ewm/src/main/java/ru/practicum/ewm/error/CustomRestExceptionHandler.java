package ru.practicum.ewm.error;

import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserWrongFieldsException.class)
    protected ResponseEntity<ErrorResponse> handleUserMissMatchException(UserWrongFieldsException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), ex.getReason()), status);
    }

    @ExceptionHandler(RequestWrongStatusException.class)
    protected ResponseEntity<ErrorResponse> handleRequestWrongStatusException(RequestWrongStatusException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), ex.getReason()), status);
    }

    @ExceptionHandler(IntegrityConflictException.class)
    protected ResponseEntity<ErrorResponse> handleIntegrityConflictException(IntegrityConflictException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), ex.getReason()), status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
                                                        WebRequest request) {
        String error = ex.getMessage().substring(0,ex.getMessage().length() - (ex.getValue().toString().length() + 2)) + ex.getValue();
        return new ResponseEntity<>(new ErrorResponse(status, error, "Incorrectly made request."), status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, error, "Incorrectly made request."), status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
                                                                           WebRequest request) {
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, error, "Incorrectly made request."), status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), "Incorrectly made request."), status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), ex.getReason()), status);
    }

    @ExceptionHandler(EventStatusException.class)
    protected ResponseEntity<ErrorResponse> handleEventStatusException(EventStatusException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), ex.getReason()), status);
    }

    @ExceptionHandler(InputParametersException.class)
    protected ResponseEntity<ErrorResponse> handleInputParametersException(InputParametersException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), "Incorrectly made request."), status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage(), "Integrity constraint has been violated."), status);
    }
}
