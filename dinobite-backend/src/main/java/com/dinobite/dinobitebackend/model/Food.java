package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

/**
 * Represents a food item in the database.
 * This entity class maps to the "foods" table in the database.
 * Fields:
 * - id: The unique identifier of the food item.
 * - category: The category to which the food item belongs.
 * - restaurant: The restaurant that offers the food item.
 * - name: The name of the food item.
 * - description: The description of the food item.
 * - price: The price of the food item.
 * - availability: The availability status of the food item.
 * - image: The URL of the image of the food item.
 * - protein: The amount of protein in the food item.
 * - carbs: The amount of carbohydrates in the food item.
 * - fats: The amount of fats in the food item.
 * - sugar: The amount of sugar in the food item.
 */
@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    private Restaurant restaurant;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String name;

    @Column(length = 5000)
    @Size(max = 5000)
    private String description;

    @Column(nullable = false)
    @Positive
    private Float price;

    @Column(nullable = false)
    @Builder.Default
    private Boolean availability = true;

    @Column()
    @URL
    private String image;

    @Column(precision = 5, scale = 2)
    @PositiveOrZero
    private BigDecimal protein;

    @Column(precision = 5, scale = 2)
    @PositiveOrZero
    private BigDecimal carbs;

    @Column(precision = 5, scale = 2)
    @PositiveOrZero
    private BigDecimal fats;

    @Column(precision = 5, scale = 2)
    @PositiveOrZero
    private BigDecimal sugar;
}