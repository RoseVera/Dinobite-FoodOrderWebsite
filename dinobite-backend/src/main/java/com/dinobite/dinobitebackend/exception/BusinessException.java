package com.dinobite.dinobitebackend.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Custom exception class for business logic errors.
 * This class extends RuntimeException and is used to indicate
 * errors that occur during the execution of business logic.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}