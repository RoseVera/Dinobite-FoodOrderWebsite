package com.dinobite.dinobitebackend.mapper;

import com.dinobite.dinobitebackend.dto.CouponRequestDto;
import com.dinobite.dinobitebackend.dto.CouponResponseDto;
import com.dinobite.dinobitebackend.model.Coupon;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Coupon entities and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * It defines methods for converting between CouponRequestDto, CouponResponseDto, and Coupon entities.
 */
@Mapper(componentModel = "spring",
        uses = {CustomerMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CouponMapper {

    /**
     * Maps a Coupon entity to a CouponResponseDto.
     * Extracts the customer's ID from the associated Customer entity.
     *
     * @param coupon the Coupon entity.
     * @return the mapped CouponResponseDto.
     */
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(target = "expirationDate", source = "expirationDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CouponResponseDto toResponseDto(Coupon coupon);

    /**
     * Maps a CouponRequestDto to a Coupon entity.
     * Ignores the ID field in the request DTO and maps the customer ID to the Customer entity.
     *
     * @param requestDto the CouponRequestDto.
     * @return the mapped Coupon entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "idToCustomer")
    @Mapping(target = "status", constant = "true")
    Coupon toEntity(CouponRequestDto requestDto);

    /**
     * Updates an existing Coupon entity with values from a CouponRequestDto.
     * Ignores the ID to preserve the existing entity's identity.
     *
     * @param dto the CouponRequestDto.
     * @param entity the existing Coupon entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "idToCustomer")
    @Mapping(target = "status", constant = "true")
    void updateEntityFromDto(CouponRequestDto dto, @MappingTarget Coupon entity);

    /**
     * Maps a list of Coupon entities to a list of CouponResponseDto.
     *
     * @param coupons the list of Coupon entities.
     * @return the list of mapped CouponResponseDto.
     */
    List<CouponResponseDto> toResponseDtoList(List<Coupon> coupons);

    /**
     * Helper method to convert an Integer ID to a Coupon entity.
     * This is used to avoid loading the entire Coupon entity when only the ID is needed.
     *
     * @param id the ID of the Coupon.
     * @return a Coupon entity with only the ID field set.
     */
    @Named("idToCoupon")
    default Coupon idToCoupon(Integer id) {
        if (id == null) return null;
        return Coupon.builder().id(id).build();
    }
}