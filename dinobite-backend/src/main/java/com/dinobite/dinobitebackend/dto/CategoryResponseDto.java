package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * CategoryResponseDto.java
 * This class represents the response payload for a category.
 * It contains the category ID, name, and associated restaurant ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {

    private Integer id;
    private String name;
    private Integer restaurantId;
}