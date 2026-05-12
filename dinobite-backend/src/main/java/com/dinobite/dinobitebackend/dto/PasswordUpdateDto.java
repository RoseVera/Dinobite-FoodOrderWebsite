package com.dinobite.dinobitebackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PasswordUpdateDto.java
 * This class represents the request payload for updating a user's password.
 * It contains the current password and the new password.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateDto {

    @NotBlank
    @Size(min = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword;

    @NotBlank
    @Size(min = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;
}