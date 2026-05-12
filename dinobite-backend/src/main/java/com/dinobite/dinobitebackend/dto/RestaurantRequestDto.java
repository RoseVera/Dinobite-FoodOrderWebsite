package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

/**
 * RestaurantRequestDto.java
 * This class represents the request payload for creating or updating a restaurant.
 * It contains the user ID, business owner name, owner's email, phone number, hours of operation,
 * cuisine type, delivery range, and logo URL.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequestDto {

    @NotNull
    private Integer userId;

    @NotBlank
    @Size(max = 200)
    private String businessOwner;

    @Email
    @NotBlank(message = "Business owner name is required")
    private String ownerMail;

    @Pattern(
        regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$",
        message = "Phone number should be in a valid format"
    )
    private String phone;

    @NotBlank
    private String hours;

    @NotBlank
     @Size(max = 300, message = "Cuisine type must be at most 300 characters")
    private String cuisine;

    @Min(value = 1, message = "Delivery range must be at least 1 km")
    @Max(value = 50, message = "Delivery range must be no more than 50 km")
    private Integer deliveryRange;

    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    
    @URL(message = "Logo must be a valid URL")
    private String logo;
}