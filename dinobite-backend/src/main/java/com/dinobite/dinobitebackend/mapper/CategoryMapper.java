package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.CategoryRequestDto;
import com.dinobite.dinobitebackend.dto.CategoryResponseDto;
import com.dinobite.dinobitebackend.model.Category;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Category entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between CategoryRequestDto, CategoryResponseDto, and Category entities.
 */
@Mapper(componentModel = "spring",
        uses = {RestaurantMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CategoryMapper {

    /**
     * Maps a Category entity to a CategoryResponseDto.
     * Extracts the restaurant's ID from the associated Restaurant entity.
     *
     * @param category the Category entity.
     * @return the mapped CategoryResponseDto.
     */
    @Mapping(source = "restaurant.id", target = "restaurantId")
    CategoryResponseDto toResponseDto(Category category);

    /**
     * Maps a CategoryRequestDto to a Category entity.
     * Ignores the ID field in the request DTO and maps the restaurant ID to the Restaurant entity.
     *
     * @param requestDto the CategoryRequestDto.
     * @return the mapped Category entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "idToRestaurant")
    Category toEntity(CategoryRequestDto requestDto);

    /**
     * Updates an existing Category entity with values from a CategoryRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto the CategoryRequestDto.
     * @param entity the existing Category entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "idToRestaurant")
    void updateEntityFromDto(CategoryRequestDto dto, @MappingTarget Category entity);

    /**
     * Maps a list of Category entities to a list of CategoryResponseDto.
     *
     * @param categories the list of Category entities.
     * @return the list of mapped CategoryResponseDto.
     */
    List<CategoryResponseDto> toResponseDtoList(List<Category> categories);

    /**
     * Helper method to create a Category entity reference from an ID.
     * This is useful for setting references without fully loading the entity.
     *
     * @param id the ID of the Category.
     * @return a Category entity with only the ID field set.
     */
    @Named("idToCategory")
    default Category idToCategory(Integer id) {
        if (id == null) return null;
        return Category.builder().id(id).build();
    }
}