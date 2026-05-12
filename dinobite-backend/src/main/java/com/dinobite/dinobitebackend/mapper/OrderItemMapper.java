package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.OrderItemRequestDto;
import com.dinobite.dinobitebackend.dto.OrderItemResponseDto;
import com.dinobite.dinobitebackend.model.OrderItem;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between OrderItem entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between OrderItemRequestDto, OrderItemResponseDto, and OrderItem entities.
 */
@Mapper(componentModel = "spring",
        uses = {OrderMapper.class, FoodMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderItemMapper {

    /**
     * Maps an OrderItem entity to an OrderItemResponseDto.
     * Extracts the order and food IDs from the associated entities.
     *
     * @param orderItem the OrderItem entity.
     * @return the mapped OrderItemResponseDto.
     */
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "food.id", target = "foodId")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(source = "food.image", target = "foodImage")
    @Mapping(target = "totalPrice", expression = "java(orderItem.getPrice() * orderItem.getQuantity())")
    OrderItemResponseDto toResponseDto(OrderItem orderItem);

    /**
     * Maps an OrderItemRequestDto to an OrderItem entity.
     * Ignores the ID field in the request DTO
     * and maps the order and food IDs to their respective entities.
     *
     * @param requestDto the OrderItemRequestDto.
     * @return the mapped OrderItem entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", source = "orderId", qualifiedByName = "idToOrder")
    @Mapping(target = "food", source = "foodId", qualifiedByName = "idToFood")
    OrderItem toEntity(OrderItemRequestDto requestDto);

    /**
     * Updates an existing OrderItem entity with values from an OrderItemRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto    the OrderItemRequestDto.
     * @param entity the existing OrderItem entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "food", ignore = true)
    void updateEntityFromDto(OrderItemRequestDto dto, @MappingTarget OrderItem entity);

    /**
     * Maps a list of OrderItem entities to a list of OrderItemResponseDto.
     *
     * @param orderItems the list of OrderItem entities.
     * @return the list of mapped OrderItemResponseDto.
     */
    List<OrderItemResponseDto> toResponseDtoList(List<OrderItem> orderItems);

    /**
     * Helper method to create an OrderItem entity reference from an ID.
     * This is used to avoid loading the entire OrderItem entity when only the ID is needed.
     *
     * @param id the ID of the OrderItem.
     * @return an OrderItem entity with only the ID field set.
     */
    @Named("idToOrderItem")
    default OrderItem idToOrderItem(Integer id) {
        if (id == null) return null;
        return OrderItem.builder().id(id).build();
    }
}