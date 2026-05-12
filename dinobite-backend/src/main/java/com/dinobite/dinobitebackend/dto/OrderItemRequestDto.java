package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * OrderItemRequestDto.java
 * This class represents the request payload for creating or updating an order item.
 * It contains the order ID, food ID, quantity, price, and an optional order note.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDto {

    @NotNull
    private Integer orderId;

    @NotNull
    private Integer foodId;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer quantity;

    @NotNull
    @Positive
    private Float price;

    @Size(max = 5000)
    private String orderNote;
}