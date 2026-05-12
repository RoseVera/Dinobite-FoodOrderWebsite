package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * OrderResponseDto.java
 * This class represents the response payload for an order.
 * It contains the order ID, customer ID, customer name, restaurant ID, restaurant name,
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Integer id;
    private Integer customerId;
    private String customerName;
    private Integer restaurantId;
    private String restaurantName;
    private Integer courierId;
    private String courierName;
    private String status;
    private Float totalPrice;
    private String placeAt;
    private String deliveredAt;
    private Integer courierRate;
    private Integer restaurantRate;
    private String customerAddress;
    private String customerPhone;
    private String restaurantAddress;

    
}