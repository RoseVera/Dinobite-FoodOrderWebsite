package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.OrderItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing OrderItem entities.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    /**
     * Retrieves a list of OrderItem entities based on the order ID.
     *
     * @param orderId The ID of the order to find items for
     * @return A list of OrderItem entities associated with the order
     */
    @EntityGraph(attributePaths = {"food"})
    List<OrderItem> findByOrderId(Integer orderId);

    /**
     * Retrieves a list of OrderItem entities based on the order ID and food ID.
     *
     * @param orderId The ID of the order to find items for
     * @param foodId  The ID of the food item to find
     * @return An Optional containing the OrderItem entity if found, or empty if not found
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.food.id = :foodId")
    Optional<OrderItem> findByOrderAndFood(@Param("orderId") Integer orderId,
                                           @Param("foodId") Integer foodId);

    /**
     * Calculates the total price of all items in an order based on the order ID.
     * 
     * @param orderId The ID of the order to calculate the total for
     * @return An Optional containing the total price if found, or empty if not found
     */
    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Optional<Float> calculateOrderTotal(@Param("orderId") Integer orderId);

    /**
     * Deletes all OrderItem entities associated with a specific order ID.
     *
     * @param orderId The ID of the order to delete items for
     */
    @Transactional
    void deleteByOrderId(Integer orderId);
}