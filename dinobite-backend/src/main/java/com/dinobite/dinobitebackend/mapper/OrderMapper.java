package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.OrderRequestDto;
import com.dinobite.dinobitebackend.dto.OrderResponseDto;
import com.dinobite.dinobitebackend.model.Order;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Order entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between OrderRequestDto, OrderResponseDto, and Order entities.
 */
@Mapper(componentModel = "spring",
        uses = {CustomerMapper.class, RestaurantMapper.class, CourierMapper.class, OrderItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderMapper {

    /**
     * Maps an Order entity to an OrderResponseDto.
     * Extracts the customer, restaurant, and courier IDs from the associated entities.
     *
     * @param order the Order entity.
     * @return the mapped OrderResponseDto.
     */
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.user.name", target = "customerName")
    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "restaurant.user.name", target = "restaurantName")
    @Mapping(source = "courier.id", target = "courierId")
    @Mapping(source = "courier.user.name", target = "courierName")
    @Mapping(target = "placeAt", source = "placeAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "deliveredAt", source = "deliveredAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "customer.address", target = "customerAddress")
    @Mapping(source = "customer.phone", target = "customerPhone")
    @Mapping(source = "restaurant.address", target = "restaurantAddress")

    OrderResponseDto toResponseDto(Order order);

    /** 
     * Maps an OrderRequestDto to an Order entity.
     * Ignores the ID field in the request DTO and maps the customer,
     * restaurant, and courier IDs to their respective entities.
     *
     * @param requestDto the OrderRequestDto.
     * @return the mapped Order entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "idToCustomer")
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "idToRestaurant")
    @Mapping(target = "courier", source = "courierId", qualifiedByName = "idToCourier")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "placeAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "deliveredAt", expression = "java(null)")
    @Mapping(target = "courierRate", ignore = true)
    @Mapping(target = "restaurantRate", ignore = true)
    Order toEntity(OrderRequestDto requestDto);

    /**
     * Updates an existing Order entity with values from an OrderRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto    the OrderRequestDto.
     * @param entity the existing Order entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = false)
    @Mapping(target = "placeAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "courierRate", source = "courierRate")
    @Mapping(target = "restaurantRate", source = "restaurantRate")
    void updateOrderFromDto(OrderRequestDto dto, @MappingTarget Order entity);

    /**
     * Maps a list of Order entities to a list of OrderResponseDto.
     *
     * @param orders the list of Order entities.
     * @return the list of mapped OrderResponseDto.
     */
    List<OrderResponseDto> toResponseDtoList(List<Order> orders);

    /**
     * Helper method to create a simple Order entity reference from its ID.
     * Useful for setting foreign key relationships without loading full entities.
     *
     * @param id the ID of the Order.
     * @return an Order entity with only the ID field set.
     */
    @Named("idToOrder")
    default Order idToOrder(Integer id) {
        if (id == null) return null;
        return Order.builder().id(id).build();
    }
}