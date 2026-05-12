package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Represents a category of items in a restaurant menu.
 * This entity is mapped to the "categories" table in the database.
 * Fields:
 * - id: The unique identifier of the category.
 * - restaurant: The restaurant to which this category belongs.
 * - name: The name of the category.
 * Constraints:
 * - The name must not be blank and must be between 1 and 300 characters.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    private Restaurant restaurant;

    @Column(length = 300, nullable = false)
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 1, max = 300, message = "Category name must be between 1 and 300 characters")
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Food> foods;
}