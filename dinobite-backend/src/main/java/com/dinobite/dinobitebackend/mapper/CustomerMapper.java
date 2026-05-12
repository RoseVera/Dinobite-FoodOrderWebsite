package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.CustomerRequestDto;
import com.dinobite.dinobitebackend.dto.CustomerResponseDto;
import com.dinobite.dinobitebackend.model.Customer;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Customer entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between CustomerRequestDto, CustomerResponseDto, and Customer entities.
 */
@Mapper(componentModel = "spring",
        uses = {UserMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {

    /**
     * Maps a Customer entity to a CustomerResponseDto.
     * Extracts the user's ID from the associated User entity.
     *
     * @param customer the Customer entity.
     * @return the mapped CustomerResponseDto.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "birthDate", source = "birthDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "lastSpinDate", source = "lastSpinDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CustomerResponseDto toResponseDto(Customer customer);

    /**
     * Maps a CustomerRequestDto to a Customer entity.
     * Ignores the ID field in the request DTO and maps the user ID to the User entity.
     *
     * @param requestDto the CustomerRequestDto.
     * @return the mapped Customer entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "idToUser")
    Customer toEntity(CustomerRequestDto requestDto);

    /**
     * Updates an existing Customer entity with values from a CustomerRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto the CustomerRequestDto.
     * @param entity the existing Customer entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(CustomerRequestDto dto, @MappingTarget Customer entity);

    /**
     * Maps a list of Customer entities to a list of CustomerResponseDto.
     *
     * @param customers the list of Customer entities.
     * @return the list of mapped CustomerResponseDto.
     */
    List<CustomerResponseDto> toResponseDtoList(List<Customer> customers);

    /**
     * Helper method to convert an Integer ID to a Customer entity.
     * This method is used to create a reference to a Customer entity based on its ID.
     *
     * @param id the ID of the Customer entity.
     * @return a Customer entity with the specified ID.
     */
    @Named("idToCustomer")
    default Customer idToCustomer(Integer id) {
        if (id == null) return null;
        return Customer.builder().id(id).build();
    }
}