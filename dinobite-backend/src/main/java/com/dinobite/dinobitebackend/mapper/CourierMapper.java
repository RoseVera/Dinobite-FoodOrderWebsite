package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.CourierRequestDto;
import com.dinobite.dinobitebackend.dto.CourierResponseDto;
import com.dinobite.dinobitebackend.model.Courier;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Courier entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between CourierRequestDto, CourierResponseDto, and Courier entities.
 */
@Mapper(componentModel = "spring",
        uses = {UserMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CourierMapper {

    /**
     * Maps a Courier entity to a CourierResponseDto.
     * Extracts the user's ID from the associated User entity.
     *
     * @param courier the Courier entity.
     * @return the mapped CourierResponseDto.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "birthDate", source = "birthDate", dateFormat = "yyyy-MM-dd")
    CourierResponseDto toResponseDto(Courier courier);

    /**
     * Maps a CourierRequestDto to a Courier entity.
     * Ignores the ID field in the request DTO and maps the user ID to the User entity.
     *
     * @param requestDto the CourierRequestDto.
     * @return the mapped Courier entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "idToUser")
    @Mapping(target = "status", ignore = true)
    Courier toEntity(CourierRequestDto requestDto);

    /**
     * Updates an existing Courier entity with values from a CourierRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto the CourierRequestDto.
     * @param entity the existing Courier entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "idToUser")
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDto(CourierRequestDto dto, @MappingTarget Courier entity);

    /**
     * Maps a list of Courier entities to a list of CourierResponseDto.
     *
     * @param couriers the list of Courier entities.
     * @return the list of mapped CourierResponseDto.
     */
    List<CourierResponseDto> toResponseDtoList(List<Courier> couriers);

    /**
     * Helper method to convert an Integer ID to a Courier entity.
     * This method is used to create a reference to a Courier entity based on its ID.
     *
     * @param id the ID of the Courier entity.
     * @return a Courier entity with only the ID field set.
     */
    @Named("idToCourier")
    default Courier idToCourier(Integer id) {
        if (id == null) return null;
        return Courier.builder().id(id).build();
    }
}