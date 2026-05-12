package com.dinobite.dinobitebackend.exception;

/**
 * Custom exception class for business logic errors.
 * This class extends RuntimeException and is used to indicate
 * errors that occur during the execution of business logic.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}