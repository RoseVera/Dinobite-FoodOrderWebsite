package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * Represents a favorite entity that links a customer to a restaurant.
 * This entity is mapped to the "favorites"
 * table in the database with unique constraints on customer_id and restaurant_id.
 * The fields include an auto-generated id, a lazy-loaded customer association,
 * a restaurant association, and JSON back reference for customer.
 *
 * @Entity Indicates that this class is an entity to be managed by JPA.
 * @Table Specifies the table name and unique constraints for the entity.
 * @Getter/@Setter Lombok annotations to generate getters and setters for the fields.
 * @NoArgsConstructor/@AllArgsConstructor/@Builder Lombok annotations for constructors and builder pattern.
 */
@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"customer_id", "restaurant_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

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
}