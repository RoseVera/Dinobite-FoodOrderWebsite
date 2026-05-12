package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Represents an order item entity mapped to the "order_items" table in the database.
 * This entity class defines the structure of an order item,
 * including its relationships with the Order and Food entities.
 * Fields:
 * - id: The unique identifier of the order item.
 * - order: The order to which this item belongs (many-to-one relationship).
 * - food: The food item associated with this order item (many-to-one relationship).
 * - quantity: The quantity of the food item in the order (must be between 1 and 100).
 * - price: The price of the food item (must be a positive value).
 * - orderNote: Additional notes or comments for the order item (up to 5000 characters).
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    @Min(1)
    @Max(100)
    private Integer quantity;

    @Column(nullable = false)
    @Positive
    private Float price;

    @Column(length = 5000)
    @Size(max = 5000)
    private String orderNote;
}