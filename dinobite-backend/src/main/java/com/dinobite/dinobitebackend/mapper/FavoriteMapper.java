package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.FavoriteRequestDto;
import com.dinobite.dinobitebackend.dto.FavoriteResponseDto;
import com.dinobite.dinobitebackend.model.Favorite;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Favorite entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between FavoriteRequestDto, FavoriteResponseDto, and Favorite entities.
 */
@Mapper(componentModel = "spring",
        uses = {CustomerMapper.class, RestaurantMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FavoriteMapper {

    /**
     * Maps a Favorite entity to a FavoriteResponseDto.
     * Extracts the customer and restaurant IDs from the associated entities.
     *
     * @param favorite the Favorite entity.
     * @return the mapped FavoriteResponseDto.
     */
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "restaurant.user.name", target = "restaurantName")
    @Mapping(source = "restaurant.logo", target = "restaurantImage")
    FavoriteResponseDto toResponseDto(Favorite favorite);

    /**
     * Maps a FavoriteRequestDto to a Favorite entity.
     * Ignores the ID field in the request DTO
     * and maps the customer and restaurant IDs to their respective entities.
     *
     * @param requestDto the FavoriteRequestDto.
     * @return the mapped Favorite entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "idToCustomer")
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "idToRestaurant")
    Favorite toEntity(FavoriteRequestDto requestDto);

    /**
     * Updates an existing Favorite entity with values from a FavoriteRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto    the FavoriteRequestDto.
     * @param entity the existing Favorite entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "idToCustomer")
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "idToRestaurant")
    void updateEntityFromDto(FavoriteRequestDto dto, @MappingTarget Favorite entity);

    /**
     * Maps a list of Favorite entities to a list of FavoriteResponseDto.
     *
     * @param favorites the list of Favorite entities.
     * @return the list of mapped FavoriteResponseDto.
     */
    List<FavoriteResponseDto> toResponseDtoList(List<Favorite> favorites);

    /**
     * Helper method to create a Favorite entity reference from an ID.
     * This method is used to create a reference to a Favorite entity based on its ID.
     *
     * @param id the ID of the Favorite entity.
     * @return a Favorite entity with the specified ID.
     */
    @Named("idToFavorite")
    default Favorite idToFavorite(Integer id) {
        if (id == null) return null;
        return Favorite.builder().id(id).build();
    }
}