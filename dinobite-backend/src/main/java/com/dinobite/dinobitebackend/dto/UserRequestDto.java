package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * UserRequestDto.java
 * This class represents the request payload for user registration.
 * It contains the user's name, email, password, and user type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @Email
    @NotBlank
    private String mail;

    @NotBlank
    @Size(min = 2, max = 255)
    private String name;
}