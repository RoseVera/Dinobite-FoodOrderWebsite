package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a Coupon entity with details such as id, customer, code, discount percentage,
 * expiration date, and status.
 * This entity is mapped to the "coupons" table in the database.
 * Fields:
 * - id: The unique identifier of the coupon.
 * - customer: The customer associated with the coupon.
 * - code: The unique code of the coupon.
 * - discountPercent: The percentage of discount offered by the coupon.
 * - expirationDate: The date when the coupon expires.
 * - status: The status of the coupon (active or inactive).
 */
@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(length = 15, nullable = false, unique = true)
    @NotBlank
    @Size(min = 5, max = 15)
    private String code;

    @Column(nullable = false)
    @Min(1)
    @Max(100)
    private Integer discountPercent;

    @Column(nullable = false)
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean status = true;
}