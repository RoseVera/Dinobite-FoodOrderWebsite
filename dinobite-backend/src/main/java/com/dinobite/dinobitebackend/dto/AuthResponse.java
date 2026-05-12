package com.dinobite.dinobitebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AuthResponse.java
 * This class represents the authentication response payload.
 * It contains a message, user ID, username, and user type.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private Integer userId;
    private String userName;
    private String userType;
}