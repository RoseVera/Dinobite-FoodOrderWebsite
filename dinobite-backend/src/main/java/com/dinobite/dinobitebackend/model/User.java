package com.dinobite.dinobitebackend.model;

import com.dinobite.dinobitebackend.enums.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Represents a User entity with details such as id, email, password, name, and type.
 * This entity is mapped to the "users" table in the database with a unique constraint on the "mail" column.
 * The User class includes annotations for JPA mapping, validation constraints,
 * and lombok annotations for getters, setters, constructors, and builder.
 * Fields:
 * - id: unique identifier generated using GenerationType.IDENTITY
 * - mail: user's email address, unique, and validated as a valid email format
 * - password: user's password, validated for minimum length of 8 characters, and hidden from JSON serialization
 * - name: user's name, validated for length between 2 and 255 characters and not blank.
 * - type: user's type, represented as an enum (UserType), with a string representation in the database.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "mail"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String mail;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserType type;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Customer customer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Courier courier;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Restaurant restaurant;
}