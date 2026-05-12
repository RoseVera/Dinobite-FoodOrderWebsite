package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a Customer entity with details such as id, user, address, phone, and birthdate.
 * This entity is mapped to the "customers" table in the database.
 * Fields:
 * - id: The unique identifier for the customer (auto-generated).
 * - user: The associated user entity (one-to-one relationship).
 * - address: The address of the customer (max length: 500).
 * - phone: The phone number of the customer (validated with a regex pattern).
 * - birthDate: The birthdate of the customer (must be in the past).
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    @Column(length = 500)
    @Size(max = 500)
    private String address;

    @Column(length = 25)
    @Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$")
    private String phone;

    @Column(nullable = false)
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Column(name = "last_spin_date")
    private LocalDateTime lastSpinDate;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Coupon> coupons;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Favorite> favorites;
}