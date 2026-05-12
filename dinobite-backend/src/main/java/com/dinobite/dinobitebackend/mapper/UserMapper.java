package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.UserCreateDto;
import com.dinobite.dinobitebackend.dto.UserRequestDto;
import com.dinobite.dinobitebackend.dto.UserResponseDto;
import com.dinobite.dinobitebackend.model.User;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between User entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between UserRequestDto, UserResponseDto, and User entities.
 */
@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    /**
     * Maps a User entity to a UserResponseDto.
     * Extracts the user ID and name from the associated user entity.
     *
     * @param user the User entity.
     * @return the mapped UserResponseDto.
     */
    UserResponseDto toResponseDto(User user);

    /**
     * Maps a UserRequestDto to a User entity.
     * Ignores the ID field in the request DTO and maps the user ID to its respective entity.
     *
     * @param requestDto the UserRequestDto.
     * @return the mapped User entity.
     */
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreateDto requestDto);

    /**
     * Updates an existing User entity with values from a UserRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto    the UserRequestDto.
     * @param entity the existing User entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User entity);

    /**
     * Maps a list of User entities to a list of UserResponseDto.
     *
     * @param users the list of User entities.
     * @return the list of mapped UserResponseDto.
     */
    List<UserResponseDto> toResponseDtoList(List<User> users);

    /**
     * Helper method to convert an Integer ID to a User entity.
     * This method is used in the mapping process to convert IDs to User entities.
     *
     * @param id the ID of the User entity.
     * @return the User entity with the specified ID, or null if the ID is null.
     */
    @Named("idToUser")
    default User idToUser(Integer id) {
        if (id == null) return null;
        return User.builder().id(id).build();
    }
}