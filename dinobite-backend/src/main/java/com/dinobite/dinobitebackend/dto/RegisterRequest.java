package com.dinobite.dinobitebackend.dto;
import jakarta.validation.constraints.*;
import com.dinobite.dinobitebackend.enums.UserType;
import lombok.Data;

/**
 * RegisterRequest.java
 * This class represents the request payload for user registration.
 * It contains the user's name, email, password, and user type.
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String mail;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "User type is required")
    private UserType type;
}