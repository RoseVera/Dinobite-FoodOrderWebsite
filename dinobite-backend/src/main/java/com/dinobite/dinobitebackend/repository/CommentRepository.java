package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Comment entities.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * Finds and returns a list of comments based on the provided order ID.
     *
     * @param orderId The ID of the order to search for comments
     * @return A list of comments associated with the specified order ID
     */
    List<Comment> findByOrderId(Integer orderId);

    /**
     * Counts the number of comments associated with a specific order ID.
     *
     * @param orderId The ID of the order to count comments for
     * @return The count of comments for the specified order ID
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.order.id = :orderId")
    long countByOrderId(@Param("orderId") Integer orderId);

    /**
     * Retrieves a comment by its ID and the ID of the order it belongs to.
     *
     * @param commentId The ID of the comment to retrieve
     * @param orderId The ID of the order the comment belongs to
     * @return An Optional containing the comment if found, otherwise empty
     */
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.order.id = :orderId")
    Optional<Comment> findByIdAndOrderId(
            @Param("commentId") Integer commentId,
            @Param("orderId") Integer orderId);

    /**
     * Checks if a comment with the given ID and order ID exists.
     *
     * @param commentId The ID of the comment to check
     * @param orderId The order ID associated with the comment
     * @return true if a comment with the specified IDs exists, false otherwise
     */
    boolean existsByIdAndOrderId(Integer commentId, Integer orderId);

    /**
     * Deletes all comments associated with a specific order ID.
     *
     * @param orderId The ID of the order whose comments are to be deleted
     */
    @Transactional
    void deleteByOrderId(Integer orderId);
}