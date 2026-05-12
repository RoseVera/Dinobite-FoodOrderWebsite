package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.FoodRequestDto;
import com.dinobite.dinobitebackend.dto.FoodResponseDto;
import com.dinobite.dinobitebackend.model.Food;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Food entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between FoodRequestDto, FoodResponseDto, and Food entities.
 */
@Mapper(componentModel = "spring",
        uses = {RestaurantMapper.class, CategoryMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FoodMapper {

    /**
     * Maps a Food entity to a FoodResponseDto.
     * Extracts the restaurant and category IDs from the associated entities.
     *
     * @param food the Food entity.
     * @return the mapped FoodResponseDto.
     */
    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "restaurant.user.name", target = "restaurantName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    FoodResponseDto toResponseDto(Food food);

    /**
     * Maps a FoodRequestDto to a Food entity.
     * Ignores the ID field in the request DTO
     * and maps the restaurant and category IDs to their respective entities.
     *
     * @param requestDto the FoodRequestDto.
     * @return the mapped Food entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "idToRestaurant")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "idToCategory")
    Food toEntity(FoodRequestDto requestDto);

    /**
     * Updates an existing Food entity with values from a FoodRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto    the FoodRequestDto.
     * @param entity the existing Food entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "idToRestaurant")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "idToCategory")
    void updateFoodFromDto(FoodRequestDto dto, @MappingTarget Food entity);

    /**
     * Maps a list of Food entities to a list of FoodResponseDto.
     *
     * @param foods the list of Food entities.
     * @return the list of mapped FoodResponseDto.
     */
    List<FoodResponseDto> toResponseDtoList(List<Food> foods);

    /**
     * Helper method to create a Food entity reference from an ID.
     * This method is used to create a reference to a Food entity based on its ID.
     *
     * @param id the ID of the Food entity.
     * @return a Food entity with only the ID field set.
     */
    @Named("idToFood")
    default Food idToFood(Integer id) {
        if (id == null) return null;
        return Food.builder().id(id).build();
    }
}