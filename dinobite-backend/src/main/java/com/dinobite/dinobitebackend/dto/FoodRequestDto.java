package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

/**
 * FoodRequestDto.java
 * This class represents the request payload for creating or updating a food item.
 * It contains the restaurant ID, category ID, name, description, price, availability status,
 * image URL, and nutritional information (protein, carbs, fats, sugar).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRequestDto {

    @NotNull
    private Integer restaurantId;

    @NotNull
    private Integer categoryId;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull
    @Positive
    private Float price;

    @NotNull
    private Boolean availability;

    @URL
    @Size(max = 255)
    private String image;

    @PositiveOrZero
    private BigDecimal protein;

    @PositiveOrZero
    private BigDecimal carbs;

    @PositiveOrZero
    private BigDecimal fats;

    @PositiveOrZero
    private BigDecimal sugar;
}