package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.OrderRequestDto;
import com.dinobite.dinobitebackend.dto.OrderResponseDto;
import com.dinobite.dinobitebackend.enums.OrderStatus;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.OrderMapper;
import com.dinobite.dinobitebackend.model.Courier;
import com.dinobite.dinobitebackend.model.Customer;
import com.dinobite.dinobitebackend.model.Order;
import com.dinobite.dinobitebackend.model.Restaurant;
import com.dinobite.dinobitebackend.repository.CourierRepository;
import com.dinobite.dinobitebackend.repository.CustomerRepository;
import com.dinobite.dinobitebackend.repository.OrderRepository;
import com.dinobite.dinobitebackend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing Order entities.
 * Handles creation, retrieval, updating, and deletion of orders.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    // Repository for accessing Order data.
    private final OrderRepository orderRepository;

    // Repository for accessing Customer data.
    private final CustomerRepository customerRepository;

    // Repository for accessing Restaurant data.
    private final RestaurantRepository restaurantRepository;

    // Repository for accessing Courier data.
    private final CourierRepository courierRepository;

    // Mapper for converting between Order entities and DTOs.
    private final OrderMapper orderMapper;

    // Service for managing OrderItem entities.
    private final OrderItemService orderItemService;

    // Service for managing Comment entities.
    private final CommentService commentService;

    /**
     * Creates a new order.
     *
     * @param requestDto the DTO containing order data.
     * @return OrderResponseDto representing the created order.
     * @throws ResourceNotFoundException if the customer, restaurant, or courier is not found.
     */
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Order order = orderMapper.toEntity(requestDto);
        order.setCustomer(customer);
        order.setRestaurant(restaurant);

        if (requestDto.getCourierId() != null) {
            Courier courier = courierRepository.findById(requestDto.getCourierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));
            order.setCourier(courier);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Created order with ID: {}", savedOrder.getId());
        return orderMapper.toResponseDto(savedOrder);
    }

    /**
     * Updates an existing order.
     *
     * @param orderId the ID of the order to update.
     * @param requestDto the DTO containing updated order data.
     * @return OrderResponseDto representing the updated order.
     * @throws ResourceNotFoundException if the order, customer, restaurant, or courier is not found.
     */
    public OrderResponseDto updateOrder(Integer orderId, OrderRequestDto requestDto) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        orderMapper.updateOrderFromDto(requestDto, existingOrder);
        existingOrder.setStatus(requestDto.getStatus());
        existingOrder.setCustomer(customer);
        existingOrder.setRestaurant(restaurant);

        existingOrder.setCourierRate(requestDto.getCourierRate());
        existingOrder.setRestaurantRate(requestDto.getRestaurantRate());

        if (requestDto.getCourierId() != null) {
            Courier courier = courierRepository.findById(requestDto.getCourierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));
            existingOrder.setCourier(courier);
        } else {
            existingOrder.setCourier(null);
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("Updated order withfvdfvdz ID: {}", orderId);
        return orderMapper.toResponseDto(updatedOrder);
    }

    /**
     * Deletes an order.
     *
     * @param orderId the ID of the order to delete.
     * @throws ResourceNotFoundException if the order is not found.
     */
    public void deleteOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        orderItemService.deleteOrderItemsByOrderId(orderId);
        commentService.deleteAllCommentsByOrderId(orderId);

        orderRepository.delete(order);
        log.info("Deleted order with ID: {}", orderId);
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId the ID of the order to update.
     * @param status the new status for the order.
     * @return OrderResponseDto representing the updated order.
     * @throws ResourceNotFoundException if the order is not found.
     */
    public OrderResponseDto updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);

        if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        } else {
            order.setDeliveredAt(null);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Updated order {} status to {}", orderId, status);
        return orderMapper.toResponseDto(updatedOrder);
    }

    /**
     * Retrieves a list of orders for a given customer, optionally filtered by status.
     *
     * @param customerId the ID of the customer.
     * @param status the status to filter orders by (optional).
     * @return a list of OrderResponseDto representing the customer's orders.
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getCustomerOrders(Integer customerId, OrderStatus status) {
        List<Order> orders;

        if (status != null) {
            orders = orderRepository.findByCustomerIdAndStatus(customerId, status);
        } else {
            orders = orderRepository.findByCustomerId(customerId);
        }

        return orderMapper.toResponseDtoList(orders);
    }

    /**
     * Retrieves a list of orders for a given restaurant, optionally filtered by status.
     *
     * @param restaurantId the ID of the restaurant.
     * @param status the status to filter orders by (optional).
     * @return a list of OrderResponseDto representing the restaurant's orders.
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getRestaurantOrders(Integer restaurantId, OrderStatus status) {
        List<Order> orders;

        if (status != null) {
            orders = orderRepository.findByRestaurantIdAndStatus(restaurantId, status);
        } else {
            orders = orderRepository.findByRestaurantId(restaurantId);
        }

        return orderMapper.toResponseDtoList(orders);
    }

    /**
     * Retrieves a list of orders for a given courier, optionally filtered by status.
     *
     * @param courierId the ID of the courier.
     * @param status the status to filter orders by (optional).
     * @return a list of OrderResponseDto representing the courier's orders.
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getCourierOrders(Integer courierId, OrderStatus status) {
        List<Order> orders;

        if (status != null) {
            orders = orderRepository.findByCourierIdAndStatus(courierId, status);
        } else {
            orders = orderRepository.findByCourierId(courierId);
        }

        return orderMapper.toResponseDtoList(orders);
    }

}