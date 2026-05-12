package com.dinobite.dinobitebackend.dto;

import com.dinobite.dinobitebackend.enums.UserType;
import lombok.*;

/**
 * UserResponseDto.java
 * This class represents the response payload for a user.
 * It contains the user ID, email, name, and user type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Integer id;
    private String mail;
    private String name;
    private UserType type;
}