package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * CommentRequestDto.java
 * This class represents the request payload for creating or updating a comment.
 * It contains the comment text and the associated order ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {

    @NotBlank
    @Size(min = 1, max = 5000)
    private String commentText;

    @NotNull
    private Integer orderId;
}