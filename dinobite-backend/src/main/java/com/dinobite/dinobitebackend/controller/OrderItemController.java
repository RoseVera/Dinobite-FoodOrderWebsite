package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.OrderItemRequestDto;
import com.dinobite.dinobitebackend.dto.OrderItemResponseDto;
import com.dinobite.dinobitebackend.service.OrderItemService;
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
 * OrderItemController.java
 * This class handles HTTP requests related to order items.
 * It provides endpoints for creating, updating, deleting, and fetching order items.
 */
@RestController
@RequestMapping("/api/v1/orders/{orderId}/items")
@RequiredArgsConstructor
@Validated
public class OrderItemController {

    // Constructor-based dependency injection
    private final OrderItemService orderItemService;

    /**
     * Create a new order item for a specific order.
     *
     * @param orderId   the ID of the order
     * @param requestDto the request DTO containing order item details
     * @return ResponseEntity with the created order item and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderItemResponseDto> createOrderItem(
            @PathVariable Integer orderId,
            @RequestBody @Valid OrderItemRequestDto requestDto) {

        requestDto.setOrderId(orderId);
        OrderItemResponseDto response = orderItemService.createOrderItem(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Update an existing order item for a specific order.
     *
     * @param orderId   the ID of the order
     * @param itemId    the ID of the order item to update
     * @param requestDto the updated order item details
     * @return the updated OrderItemResponseDto
     */
    @PutMapping("/{itemId}")
    public OrderItemResponseDto updateOrderItem(
            @PathVariable Integer orderId,
            @PathVariable Integer itemId,
            @RequestBody @Valid OrderItemRequestDto requestDto) {

        requestDto.setOrderId(orderId);
        return orderItemService.updateOrderItem(itemId, requestDto);
    }

    /**
     * Fetch all order items for a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of OrderItemResponseDto
     */
    @GetMapping
    public List<OrderItemResponseDto> getOrderItems(
            @PathVariable Integer orderId) {

        return orderItemService.getOrderItems(orderId);
    }

    /**
     * Calculate the total price of all order items for a specific order.
     *
     * @param orderId the ID of the order
     * @return a map containing the total price
     */
    @GetMapping("/total")
    public Map<String, Float> calculateOrderTotal(
            @PathVariable Integer orderId) {

        return Collections.singletonMap("total",
                orderItemService.calculateOrderTotal(orderId));
    }

    /**
     * Delete an order item by its ID.
     *
     * @param orderId the ID of the order
     * @param itemId  the ID of the order item to delete
     */
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrderItem(
            @PathVariable Integer orderId,
            @PathVariable Integer itemId) {

        orderItemService.deleteOrderItem(itemId);
    }

    /**
     * Delete all order items for a specific order.
     *
     * @param orderId the ID of the order
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllOrderItemsByOrderId(
            @PathVariable Integer orderId) {

        orderItemService.deleteOrderItemsByOrderId(orderId);
    }
}