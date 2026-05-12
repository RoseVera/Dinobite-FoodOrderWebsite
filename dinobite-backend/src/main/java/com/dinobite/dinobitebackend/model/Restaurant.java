package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Restaurant entity with details such as user, foods, categories,
 * business owner, email, phone, hours, cuisine, delivery range, and logo.
 * This entity is mapped to the "restaurants" table in the database.
 * Fields:
 * - id: The unique identifier for the restaurant.
 * - user: The user associated with the restaurant.
 * - foods: The list of food items offered by the restaurant.
 * - categories: The list of categories the restaurant belongs to.
 * - businessOwner: The name of the business owner.
 * - ownerMail: The email address of the owner.
 * - phone: The phone number of the restaurant.
 * - hours: The operating hours of the restaurant.
 * - cuisine: The type of cuisine offered by the restaurant.
 * - deliveryRange: The delivery range of the restaurant in kilometers.
 * - logo: The URL or path to the restaurant's logo.
 */
@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Food> foods = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Category> categories = new ArrayList<>();

    @Column(length = 200, nullable = false)
    @NotBlank
    private String businessOwner;

    @Column(nullable = false)
    @Email
    @NotBlank
    private String ownerMail;

    @Column(length = 25, nullable = false)
    @Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$")
    private String phone;

    @Column(length = 25, nullable = false)
    private String hours;

    @Column(length = 300, nullable = false)
    @NotBlank
    private String cuisine;

    @Column(nullable = false)
    @Min(1)
    @Max(50)
    private Integer deliveryRange;

    @Column(length = 500)
    @Size(max = 500)
    private String address;

    @Column()
    @URL
    private String logo;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Favorite> favorites;
}