package com.dinobite.dinobitebackend.dto;

import lombok.*;

import java.util.List;

/**
 * RestaurantResponseDto.java
 * This class represents the response payload for a restaurant.
 * It contains the restaurant ID, name, user ID, business owner details, contact information,
 * operating hours, cuisine type, delivery range, logo URL, and lists of categories and foods.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponseDto {

    private Integer id;
    private String name;
    private Integer userId;
    private String businessOwner;
    private String ownerMail;
    private String phone;
    private String hours;
    private String cuisine;
    private Integer deliveryRange;
    private String address;
    private String logo;
    private List<CategoryResponseDto> categories;
    private List<FoodResponseDto> foods;
}