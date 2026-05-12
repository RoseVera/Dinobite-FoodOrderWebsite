package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.CommentRequestDto;
import com.dinobite.dinobitebackend.dto.CommentResponseDto;
import com.dinobite.dinobitebackend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * CommentController.java
 * This class handles HTTP requests related to comments on orders.
 * It provides endpoints for creating, retrieving, updating, and deleting comments.
 */
@RestController
@RequestMapping("/api/v1/orders/{orderId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    // Constructor-based dependency injection
    private final CommentService commentService;

    /**
     * Create a new comment
     * @param orderId The ID of the order
     * @param requestDto The request DTO containing comment details
     * @return ResponseEntity with the created comment and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Integer orderId,
            @RequestBody @Valid CommentRequestDto requestDto) {

        requestDto.setOrderId(orderId);
        CommentResponseDto response = commentService.createComment(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Fetch a comment from the Order
     * @param orderId The ID of the order
     * @param page The page number for pagination
     * @param size The number of comments per page
     * @return List of CommentResponseDto containing comment details
     */
    @GetMapping
    public List<CommentResponseDto> getCommentsByOrder(
            @PathVariable Integer orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return commentService.getCommentsByOrderId(orderId);
    }

    /**
     * Get the count of comments for an order
     * @param orderId The ID of the order
     * @return Map containing the count of comments
     */
    @GetMapping("/count")
    public Map<String, Long> getCommentCount(
            @PathVariable Integer orderId) {

        return Collections.singletonMap("count",
                commentService.getCommentCountByOrder(orderId));
    }

    /**
     * Update a comment
     * @param orderId The ID of the order
     * @param commentId The ID of the comment
     * @param requestDto The request DTO containing updated comment details
     * @return ResponseEntity with the updated comment
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Integer orderId,
            @PathVariable Integer commentId,
            @RequestBody @Valid CommentRequestDto requestDto) {

        requestDto.setOrderId(orderId);
        return ResponseEntity.ok(commentService.updateComment(commentId, requestDto));
    }

    /**
     * Delete a comment
     * @param orderId The ID of the order
     * @param commentId The ID of the comment
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Integer orderId,
            @PathVariable Integer commentId) {

        commentService.deleteComment(commentId, orderId);
    }

    /**
     * Delete all comments for a specific order
     *
     * @param orderId The ID of the order
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllCommentsByOrderId(
            @PathVariable Integer orderId) {

        commentService.deleteAllCommentsByOrderId(orderId);
    }
}