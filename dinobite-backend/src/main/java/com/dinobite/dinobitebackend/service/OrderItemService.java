package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.OrderItemRequestDto;
import com.dinobite.dinobitebackend.dto.OrderItemResponseDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.OrderItemMapper;
import com.dinobite.dinobitebackend.model.Food;
import com.dinobite.dinobitebackend.model.Order;
import com.dinobite.dinobitebackend.model.OrderItem;
import com.dinobite.dinobitebackend.repository.FoodRepository;
import com.dinobite.dinobitebackend.repository.OrderItemRepository;
import com.dinobite.dinobitebackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing OrderItem entities.
 * Handles creation, retrieval, updating, and deletion of order items.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderItemService {

    // Repository for accessing OrderItem data.
    private final OrderItemRepository orderItemRepository;

    // Repository for accessing Order data.
    private final OrderRepository orderRepository;

    // Repository for accessing Food data.
    private final FoodRepository foodRepository;

    // Mapper for converting between OrderItem entities and DTOs.
    private final OrderItemMapper orderItemMapper;

    /**
     * Creates a new order item.
     *
     * @param requestDto the DTO containing order item data.
     * @return OrderItemResponseDto representing the created order item.
     * @throws BusinessException if the food is already in order.
     * @throws ResourceNotFoundException if the order or food is not found.
     */
    public OrderItemResponseDto createOrderItem(OrderItemRequestDto requestDto) {
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Food food = foodRepository.findById(requestDto.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        orderItemRepository.findByOrderAndFood(order.getId(), food.getId())
                .ifPresent(item -> {
                    throw new BusinessException("This food already exists in the order");
                });

        OrderItem orderItem = orderItemMapper.toEntity(requestDto);
        orderItem.setOrder(order);
        orderItem.setFood(food);
        OrderItem savedItem = orderItemRepository.save(orderItem);

        log.info("Added food {} to order {}", food.getId(), order.getId());
        return orderItemMapper.toResponseDto(savedItem);
    }

    /**
     * Retrieves a list of order items for a given order.
     *
     * @param orderId the ID of the order.
     * @return a list of OrderItemResponseDto representing the order items.
     */
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItems(Integer orderId) {
        return orderItemMapper.toResponseDtoList(
                orderItemRepository.findByOrderId(orderId)
        );
    }

    /**
     * Updates an existing order item.
     *
     * @param id the ID of the order item to update.
     * @param requestDto the DTO containing updated order item data.
     * @return OrderItemResponseDto representing the updated order item.
     * @throws ResourceNotFoundException if the order item is not found.
     * @throws BusinessException if the order ID does not match.
     */
    public OrderItemResponseDto updateOrderItem(Integer id, OrderItemRequestDto requestDto) {
        OrderItem existingItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        if (!existingItem.getOrder().getId().equals(requestDto.getOrderId())) {
            throw new BusinessException("Cannot change order of existing item");
        }

        if (!existingItem.getFood().getId().equals(requestDto.getFoodId())) {
            Food food = foodRepository.findById(requestDto.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));
            existingItem.setFood(food);
        }

        orderItemMapper.updateEntityFromDto(requestDto, existingItem);

        OrderItem updatedItem = orderItemRepository.save(existingItem);
        log.info("Updated order item with ID: {}", id);
        return orderItemMapper.toResponseDto(updatedItem);
    }

    /**
     * Deletes an order item.
     *
     * @param itemId the ID of the order item to delete.
     * @throws ResourceNotFoundException if the order item is not found.
     */
    public void deleteOrderItem(Integer itemId) {
        if (!orderItemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Order item not found");
        }

        orderItemRepository.deleteById(itemId);
        log.info("Removed order item with ID: {}", itemId);
    }

    /**
     * Deletes all order items associated with a specific order ID.
     *
     * @param orderId the ID of the order whose items are to be deleted.
     * @throws ResourceNotFoundException if no order items are found for the given order ID.
     */
    public void deleteOrderItemsByOrderId(Integer orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        if (!items.isEmpty()) {
            orderItemRepository.deleteByOrderId(orderId);
            log.info("Deleted {} order items for order ID: {}", items.size(), orderId);
        } else {
            log.info("No order items found for order ID: {} — nothing to delete", orderId);
        }
    }

    /**
     * Calculates the total price of an order.
     *
     * @param orderId the ID of the order.
     * @return the total price of the order.
     * @throws BusinessException if the order has no items.
     */
    public Float calculateOrderTotal(Integer orderId) {
        return orderItemRepository.calculateOrderTotal(orderId)
                .orElseThrow(() -> new BusinessException("Order has no items"));
    }
}