package com.dinobite.dinobitebackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Represents a comment entity associated with an order in the database.
 * This entity is mapped to the "comments" table in the database.
 * Fields:
 * - id: The unique identifier for the comment (auto-generated).
 * - order: The order associated with this comment (lazy-loaded).
 * - commentText: The text content of the comment (1 to 5000 characters).
 * - createdAt: The timestamp when the comment was created (auto-generated and non-updatable).
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @Column(length = 5000, nullable = false)
    @NotBlank(message = "Comment text cannot be blank")
    @Size(min = 1, max = 5000, message = "Comment text must be between 1 and 5000 characters")
    private String commentText;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}