package org.evanke.test.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ErrorResponse("User not found", HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return new ErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.toString(), errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse("Bad request", HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        return new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
    }

    static class ErrorResponse {
        private final String error;
        private final String httpStatus;
        private final String message;
        private final String timestamp;

        public ErrorResponse(String error, String httpStatus, String message) {
            this.error = error;
            this.httpStatus = httpStatus;
            this.message = message;
            this.timestamp = LocalDateTime.now().toString();
        }

        public String getError() { return error; }
        public String getHttpStatus() { return httpStatus; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }
}
