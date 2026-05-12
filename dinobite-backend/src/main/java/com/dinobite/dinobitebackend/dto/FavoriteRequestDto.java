package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * FavoriteRequestDto.java
 * This class represents the request payload for adding or removing a restaurant from the favorites list.
 * It contains the customer ID and the restaurant ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRequestDto {

    @NotNull
    private Integer customerId;

    @NotNull
    private Integer restaurantId;
}