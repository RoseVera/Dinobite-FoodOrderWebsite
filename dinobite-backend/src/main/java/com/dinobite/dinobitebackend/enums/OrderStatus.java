package com.dinobite.dinobitebackend.enums;

/**
 * Enum representing the status of an order.
 */
public enum OrderStatus {
    PENDING,
    PREPARING,
    READY_FOR_PICKUP,
    ON_THE_WAY,
    DELIVERED,
    CANCELLED
}