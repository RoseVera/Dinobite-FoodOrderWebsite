package com.dinobite.dinobitebackend.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * FoodResponseDto.java
 * This class represents the response payload for a food item.
 * It contains the food ID, restaurant ID, category ID, name, description, price, availability status,
 * image URL, and nutritional information (protein, carbs, fats, sugar).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponseDto {

    private Integer id;
    private Integer restaurantId;
    private String restaurantName;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private String description;
    private Float price;
    private Boolean availability;
    private String image;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fats;
    private BigDecimal sugar;
}