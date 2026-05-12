package com.dinobite.dinobitebackend.model;

import com.dinobite.dinobitebackend.enums.CourierStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a Courier entity with details such as id, user, availability, photo, birthdate, and status.
 * This entity is mapped to the "couriers" table in the database.
 * Fields:
 * - id: The unique identifier for the courier.
 * - user: The user associated with the courier.
 * - availability: Indicates if the courier is available for service.
 * - photo: The URL or path to the courier's photo.
 * - birthDate: The birthdate of the courier.
 * - status: The status of the courier (ACTIVE, INACTIVE, ON_DELIVERY).
 */
@Entity
@Table(name = "couriers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Boolean availability = false;

    @Column()
    private String photo;

    @Column(nullable = false)
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private CourierStatus status = CourierStatus.ACTIVE;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Order> orders;
}