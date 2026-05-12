package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * FavoriteResponseDto.java
 * This class represents the response payload for a favorite restaurant.
 * It contains the favorite ID, customer ID, restaurant ID, restaurant name, and restaurant image URL.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponseDto {

    private Integer id;
    private Integer customerId;
    private Integer restaurantId;
    private String restaurantName;
    private String restaurantImage;
}