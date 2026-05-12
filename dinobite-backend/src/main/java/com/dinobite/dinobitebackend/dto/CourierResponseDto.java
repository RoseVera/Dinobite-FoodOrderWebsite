package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * CourierResponseDto.java
 * This class represents the response payload for a courier.
 * It contains the courier ID, user ID, availability status, photo URL, birthdate, and status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierResponseDto {

    private Integer id;
    private Integer userId;
    private Boolean availability;
    private String photo;
    private String birthDate;
    private String status;
}