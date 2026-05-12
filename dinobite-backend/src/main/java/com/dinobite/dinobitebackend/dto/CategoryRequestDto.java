package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * CategoryRequestDto.java
 * This class represents the request payload for creating or updating a category.
 * It contains the category name and the associated restaurant ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {

    @NotBlank
    @Size(min = 1, max = 300)
    private String name;

    @NotNull
    private Integer restaurantId;
}