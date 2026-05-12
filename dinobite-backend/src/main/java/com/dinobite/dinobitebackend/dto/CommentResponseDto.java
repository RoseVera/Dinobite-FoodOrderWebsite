package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * CommentResponseDto.java
 * This class represents the response payload for a comment.
 * It contains the comment ID, text, associated order ID, and creation timestamp.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {

    private Integer id;
    private String commentText;
    private Integer orderId;
    private String createdAt;
}