package com.dinobite.dinobitebackend.dto;
import com.dinobite.dinobitebackend.enums.CourierStatus;

import com.dinobite.dinobitebackend.enums.CourierStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/**
 * CourierRequestDto.java
 * This class represents the request payload for creating or updating a courier.
 * It contains the user ID, availability status, photo URL, birthdate, and status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierRequestDto {

    @NotNull
    private Integer userId;

    @NotNull
    private Boolean availability;

    @URL(message = "Photo must be a valid URL")
    @Size(max = 255,message = "Photo URL must be at most 255 characters")
    private String photo;

    @NotNull
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull
    private CourierStatus status;
}