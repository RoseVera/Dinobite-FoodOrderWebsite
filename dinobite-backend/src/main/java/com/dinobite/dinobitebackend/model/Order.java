package com.dinobite.dinobitebackend.model;

import com.dinobite.dinobitebackend.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an order entity in the database.
 * This class defines the structure of the 'orders' table.
 * Fields:
 * - id: The unique identifier of the order.
 * - customer: The customer who placed the order.
 * - restaurant: The restaurant from which the order was placed.
 * - courier: The courier responsible for delivering the order.
 * - status: The status of the order (e.g., pending, delivered).
 * - totalPrice: The total price of the order.
 * - placeAt: The date and time when the order was placed.
 * - deliveredAt: The date and time when the order was delivered.
 * - courierRate: The rating given by the customer to the courier (1-5).
 * - restaurantRate: The rating given by the customer to the restaurant (1-5).
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    @Positive
    private Float totalPrice;

    @Column(nullable = false)
    private LocalDateTime placeAt;

    @Column
    private LocalDateTime deliveredAt;

    @Min(1)
    @Max(5)
    private Integer courierRate;

    @Min(1)
    @Max(5)
    private Integer restaurantRate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}