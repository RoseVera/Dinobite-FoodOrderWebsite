package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * CustomerRequestDto.java
 * This class represents the request payload for creating or updating a customer.
 * It contains the user ID, address, phone number, and birthdate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDto {

    @NotNull
    private Integer userId;

    @Size(max = 500,message = "Address should be less than 500 char")
    private String address;

    @Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$",message="Phone should be in a valid format")
    @Size(max = 25)
    private String phone;

    @NotNull
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
}