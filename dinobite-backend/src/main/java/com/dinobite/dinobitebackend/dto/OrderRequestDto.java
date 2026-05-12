package com.dinobite.dinobitebackend.dto;
import com.dinobite.dinobitebackend.enums.OrderStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * OrderRequestDto.java
 * This class represents the request payload for creating or updating an order.
 * It contains the customer ID, restaurant ID, optional courier ID, and total price.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    @NotNull
    private Integer customerId;

    @NotNull
    private Integer restaurantId;

    private Integer courierId;

    @NotNull
    @Positive
    private Float totalPrice;

    @NotNull
    private OrderStatus status;

    @Min(1)
    @Max(5)
    private Integer courierRate;

    @Min(1)
    @Max(5)
    private Integer restaurantRate;
}