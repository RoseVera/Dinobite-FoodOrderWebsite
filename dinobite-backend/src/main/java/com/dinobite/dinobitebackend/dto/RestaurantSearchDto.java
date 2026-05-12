package com.dinobite.dinobitebackend.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * RestaurantSearchDto.java
 * This class represents the request payload for searching restaurants.
 * It contains the restaurant name, cuisine type, and price range.
 * It is used to filter the list of restaurants based on the provided criteria.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantSearchDto {
    private String name;
    private String cuisine;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}