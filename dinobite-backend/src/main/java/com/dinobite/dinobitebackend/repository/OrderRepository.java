package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.enums.OrderStatus;
import com.dinobite.dinobitebackend.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Order entities in the database.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Retrieves a list of orders based on the customer ID.
     *
     * @param customerId The ID of the customer to find orders for
     * @return A list of orders associated with the customer
     */
    @EntityGraph(attributePaths = {"customer", "restaurant", "courier"})
    List<Order> findByCustomerId(Integer customerId);

    /**
     * Retrieves a list of orders based on the restaurant ID.
     *
     * @param restaurantId The ID of the restaurant to find orders for
     * @return A list of orders associated with the restaurant
     */
    @EntityGraph(attributePaths = {"customer", "restaurant", "courier"})
    List<Order> findByRestaurantId(Integer restaurantId);

    /**
     * Retrieves a list of orders based on the courier ID.
     *
     * @param courierId The ID of the courier to find orders for
     * @return A list of orders associated with the courier
     */
    @EntityGraph(attributePaths = {"customer", "restaurant", "courier"})
    List<Order> findByCourierId(Integer courierId);

    /**
     * Retrieves a list of orders based on the customer ID and status.
     *
     * @param customerId The ID of the customer to find orders for
     * @param status     The status of the orders to find
     * @return A list of orders associated with the customer and status
     */
    @EntityGraph(attributePaths = {"restaurant", "courier"})
    List<Order> findByCustomerIdAndStatus(Integer customerId, OrderStatus status);

    /**
     * Retrieves a list of orders based on the restaurant ID and status.
     *
     * @param restaurantId The ID of the restaurant to find orders for
     * @param status       The status of the orders to find
     * @return A list of orders associated with the restaurant and status
     */
    @EntityGraph(attributePaths = {"restaurant", "courier"})
    List<Order> findByRestaurantIdAndStatus(Integer restaurantId, OrderStatus status);

    /**
     * Retrieves a list of orders based on the courier ID and status.
     *
     * @param courierId The ID of the courier to find orders for
     * @param status    The status of the orders to find
     * @return A list of orders associated with the courier and status
     */
    @EntityGraph(attributePaths = {"courier"})
    List<Order> findByCourierIdAndStatus(Integer courierId, OrderStatus status);
}