package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.RestaurantResponseDto;
import com.dinobite.dinobitebackend.dto.RestaurantRequestDto;
import com.dinobite.dinobitebackend.model.Restaurant;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Restaurant entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between RestaurantRequestDto, RestaurantResponseDto, and Restaurant entities.
 */
@Mapper(componentModel = "spring",
        uses = {UserMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RestaurantMapper {

    /**
     * Maps a Restaurant entity to a RestaurantResponseDto.
     * Extracts the user ID and name from the associated user entity.
     *
     * @param restaurant the Restaurant entity.
     * @return the mapped RestaurantResponseDto.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "foods", ignore = true)
    RestaurantResponseDto toResponseDto(Restaurant restaurant);

    /**
     * Maps a RestaurantRequestDto to a Restaurant entity.
     * Ignores the ID field in the request DTO and maps the user ID to its respective entity.
     *
     * @param requestDto the RestaurantRequestDto.
     * @return the mapped Restaurant entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "idToUser")
    Restaurant toEntity(RestaurantRequestDto requestDto);

    /**
     * Updates an existing Restaurant entity with values from a RestaurantRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto    the RestaurantRequestDto.
     * @param entity the existing Restaurant entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "foods", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateEntityFromDto(RestaurantRequestDto dto, @MappingTarget Restaurant entity);

    /**
     * Maps a list of Restaurant entities to a list of RestaurantResponseDto.
     *
     * @param restaurants the list of Restaurant entities.
     * @return the list of mapped RestaurantResponseDto.
     */
    List<RestaurantResponseDto> toResponseDtoList(List<Restaurant> restaurants);

    /**
     * Helper method to convert an Integer ID to a Restaurant entity.
     * This method is used for mapping the user ID to the Restaurant entity.
     *
     * @param id the ID of the Restaurant entity.
     * @return the Restaurant entity with the specified ID.
     */
    @Named("idToRestaurant")
    default Restaurant idToRestaurant(Integer id) {
        if (id == null) return null;
        return Restaurant.builder().id(id).build();
    }
}