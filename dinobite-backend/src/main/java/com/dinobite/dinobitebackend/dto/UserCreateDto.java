package com.dinobite.dinobitebackend.dto;

import com.dinobite.dinobitebackend.enums.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {
    @Email
    @NotBlank
    private String mail;

    @NotBlank
    @Size(min = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    @NotNull
    private UserType type;
}