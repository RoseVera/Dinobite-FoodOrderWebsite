package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.OrderRequestDto;
import com.dinobite.dinobitebackend.dto.OrderResponseDto;
import com.dinobite.dinobitebackend.enums.OrderStatus;
import com.dinobite.dinobitebackend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * OrderController.java
 * This class handles HTTP requests related to orders.
 * It provides endpoints for creating, updating, deleting, and fetching orders.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    // Constructor-based dependency injection
    private final OrderService orderService;

    /**
     * Create a new order
     *
     * @param requestDto The request DTO containing order details
     * @return ResponseEntity with the created order and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto requestDto) {

        OrderResponseDto response = orderService.createOrder(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Updates an existing order by its ID.
     *
     * @param orderId the ID of the order to update.
     * @param requestDto the new order details.
     * @return the updated OrderResponseDto.
     */
    @PutMapping("/{orderId}")
    public OrderResponseDto updateOrder(
            @PathVariable Integer orderId,
            @RequestBody @Valid OrderRequestDto requestDto) {
        return orderService.updateOrder(orderId, requestDto);
    }

   
    /**
     * Deletes an order by its ID.
     *
     * @param orderId the ID of the order to delete.
     */
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Integer orderId) {
        orderService.deleteOrder(orderId);
    }

    /**
     * Updates the status of a specific order.
     *
     * @param orderId the ID of the order.
     * @param status the new order status.
     * @return the updated OrderResponseDto.
     */
    @PatchMapping("/{orderId}/status")
    public OrderResponseDto updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam OrderStatus status) {

        return orderService.updateOrderStatus(orderId, status);
    }

    /**
     * Fetches all orders for a specific customer ID.
     *
     * @param customerId the ID of the customer
     * @param status     the order status to filter by (optional)
     * @return a list of OrderResponseDto for the customer
     */
    @GetMapping("/customers/{customerId}")
    public List<OrderResponseDto> getCustomerOrders(
            @PathVariable Integer customerId,
            @RequestParam(required = false) OrderStatus status) {

        return orderService.getCustomerOrders(customerId, status);
    }

    /**
     * Fetches all orders for a specific restaurant ID.
     *
     * @param restaurantId the ID of the restaurant
     * @param status       the order status to filter by (optional)
     * @return a list of OrderResponseDto for the restaurant
     */
    @GetMapping("/restaurants/{restaurantId}")
    public List<OrderResponseDto> getRestaurantOrders(
            @PathVariable Integer restaurantId,
            @RequestParam(required = false) OrderStatus status) {

        return orderService.getRestaurantOrders(restaurantId, status);
    }

    /**
     * Fetches all orders for a specific courier ID.
     *
     * @param courierId the ID of the courier
     * @param status    the order status to filter by (optional)
     * @return a list of OrderResponseDto for the courier
     */
    @GetMapping("/couriers/{courierId}")
    public List<OrderResponseDto> getCourierOrders(
            @PathVariable Integer courierId,
            @RequestParam(required = false) OrderStatus status) {

        return orderService.getCourierOrders(courierId, status);
    }


}