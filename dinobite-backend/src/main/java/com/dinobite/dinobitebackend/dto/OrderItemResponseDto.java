package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * OrderItemResponseDto.java
 * This class represents the response payload for an order item.
 * It contains the order item ID, order ID, food ID, food name, food image URL,
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {

    private Integer id;
    private Integer orderId;
    private Integer foodId;
    private String foodName;
    private String foodImage;
    private Integer quantity;
    private Float price;
    private String orderNote;
    private Float totalPrice;
}