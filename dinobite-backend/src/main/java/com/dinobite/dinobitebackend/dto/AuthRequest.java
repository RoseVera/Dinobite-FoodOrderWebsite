package com.dinobite.dinobitebackend.dto;

import lombok.Data;

/**
 * AuthRequest.java
 * This class represents the authentication request payload.
 * It contains the user's email and password.
 */
@Data
public class AuthRequest {
    private String mail;
    private String password;
}
