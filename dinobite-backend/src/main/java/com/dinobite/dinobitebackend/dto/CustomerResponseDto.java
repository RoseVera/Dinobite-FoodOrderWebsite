package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * CustomerResponseDto.java
 * This class represents the response payload for a customer.
 * It contains the customer ID, user ID, address, phone number, and birthdate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDto {

    private Integer id;
    private Integer userId;
    private String address;
    private String phone;
    private String birthDate;
    private String lastSpinDate;
}