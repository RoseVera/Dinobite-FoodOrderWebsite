package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.CommentRequestDto;
import com.dinobite.dinobitebackend.dto.CommentResponseDto;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.CommentMapper;
import com.dinobite.dinobitebackend.model.Comment;
import com.dinobite.dinobitebackend.model.Order;
import com.dinobite.dinobitebackend.repository.CommentRepository;
import com.dinobite.dinobitebackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Comment entities.
 * Handles creation, retrieval, updating, and deletion of comments associated with orders.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {

    // Repository for accessing Comment data.
    private final CommentRepository commentRepository;

    // Repository for accessing Order data, used to validate the existence of an order.
    private final OrderRepository orderRepository;

    // Mapper for converting between Comment entities and DTOs.
    private final CommentMapper commentMapper;

    /**
     * Creates a new comment associated with an order.
     *
     * @param requestDto the DTO containing comment details including the order ID.
     * @return the created Comment as a response DTO.
     * @throws ResourceNotFoundException if the order does not exist.
     */
    public CommentResponseDto createComment(CommentRequestDto requestDto) {
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + requestDto.getOrderId()));

        Comment comment = commentMapper.toEntity(requestDto);
        comment.setOrder(order);
        Comment savedComment = commentRepository.save(comment);

        log.info("Created comment for order ID: {}", order.getId());
        return commentMapper.toResponseDto(savedComment);
    }

    /**
     * Retrieves all comments associated with a specific order.
     *
     * @param orderId the ID of the order for which to retrieve comments.
     * @return a list of CommentResponseDto representing the comments.
     */
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByOrderId(Integer orderId) {
        return commentMapper.toResponseDtoList(
                commentRepository.findByOrderId(orderId)
        );
    }

    /**
     * Retrieves the count of comments associated with a specific order.
     *
     * @param orderId the ID of the order for which to count comments.
     * @return the count of comments.
     */
    @Transactional(readOnly = true)
    public long getCommentCountByOrder(Integer orderId) {
        return commentRepository.countByOrderId(orderId);
    }

    /**
     * Updates an existing comment.
     *
     * @param commentId the ID of the comment to update.
     * @param requestDto the DTO containing updated comment details.
     * @return the updated Comment as a response DTO.
     * @throws ResourceNotFoundException if the comment does not exist.
     */
    public CommentResponseDto updateComment(Integer commentId, CommentRequestDto requestDto) {
        Comment existingComment = commentRepository.findByIdAndOrderId(commentId, requestDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + commentId + " for order id: " + requestDto.getOrderId()));

        existingComment.setCommentText(requestDto.getCommentText());
        Comment updatedComment = commentRepository.save(existingComment);

        log.info("Updated comment with ID: {}", commentId);
        return commentMapper.toResponseDto(updatedComment);
    }

    /**
     * Deletes a comment associated with an order.
     *
     * @param commentId the ID of the comment to delete.
     * @param orderId   the ID of the order associated with the comment.
     * @throws ResourceNotFoundException if the comment does not exist.
     */
    public void deleteComment(Integer commentId, Integer orderId) {
        if (!commentRepository.existsByIdAndOrderId(commentId, orderId)) {
            throw new ResourceNotFoundException(
                    "Comment not found with id: " + commentId + " for order id: " + orderId);
        }
        commentRepository.deleteById(commentId);
        log.info("Deleted comment with ID: {}", commentId);
    }

    /**
     * Deletes all comments associated with a specific order.
     *
     * @param orderId the ID of the order for which to delete comments.
     * @throws ResourceNotFoundException if no comments are found for the order.
     */
    public void deleteAllCommentsByOrderId(Integer orderId) {
        List<Comment> comments = commentRepository.findByOrderId(orderId);
        if (!comments.isEmpty()) {
            commentRepository.deleteByOrderId(orderId);
            log.info("Deleted {} comments for order ID: {}", comments.size(), orderId);
        } else {
            log.info("No comments found for order ID: {} — nothing to delete", orderId);
        }
    }
}