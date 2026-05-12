package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.CouponRequestDto;
import com.dinobite.dinobitebackend.dto.CouponResponseDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.CouponMapper;
import com.dinobite.dinobitebackend.model.Coupon;
import com.dinobite.dinobitebackend.model.Customer;
import com.dinobite.dinobitebackend.repository.CouponRepository;
import com.dinobite.dinobitebackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing Coupon entities.
 * Handles creation, retrieval, and deactivation of coupons associated with customers.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CouponService {

    // Repository for accessing Coupon data.
    private final CouponRepository couponRepository;

    // Repository for accessing Customer data, used to validate the existence of a customer.
    private final CustomerRepository customerRepository;

    // Mapper for converting between Coupon entities and DTOs.
    private final CouponMapper couponMapper;

    /**
     * Retrieves all active coupons associated with a specific customer.
     *
     * @param customerId the ID of the customer for which to retrieve coupons.
     * @return a list of CouponResponseDto representing the active coupons.
     */
    @Transactional(readOnly = true)
    public List<CouponResponseDto> getActiveCouponsByCustomer(Integer customerId) {
        return couponMapper.toResponseDtoList(
                couponRepository.findActiveCouponsByCustomer(customerId)
        );
    }

    /**
     * Creates a new coupon associated with a customer.
     *
     * @param requestDto the DTO containing coupon details including the customer ID.
     * @return the created Coupon as a response DTO.
     * @throws BusinessException if the coupon code already exists.
     * @throws ResourceNotFoundException if the customer does not exist.
     */
    public CouponResponseDto createCoupon(CouponRequestDto requestDto) {
        if (couponRepository.existsByCode(requestDto.getCode())) {
            throw new BusinessException("Coupon code already exists");
        }

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getLastSpinDate() != null &&
                customer.getLastSpinDate().isAfter(LocalDateTime.now().minusWeeks(1))) {
            throw new BusinessException("You can only spin the wheel once per week.");
        }

        Coupon coupon = couponMapper.toEntity(requestDto);
        coupon.setCustomer(customer);
        Coupon savedCoupon = couponRepository.save(coupon);

        customer.setLastSpinDate(LocalDateTime.now());
        customerRepository.save(customer);

        log.info("Created coupon with code: {}", savedCoupon.getCode());
        return couponMapper.toResponseDto(savedCoupon);
    }

    /**
     * Deactivates a coupon by its ID.
     *
     * @param couponId the ID of the coupon to deactivate.
     * @return the deactivated Coupon as a response DTO.
     * @throws ResourceNotFoundException if the coupon does not exist.
     */
    public CouponResponseDto deactivateCoupon(Integer couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        coupon.setStatus(false);
        Coupon updatedCoupon = couponRepository.save(coupon);

        log.info("Deactivated coupon with ID: {}", couponId);
        return couponMapper.toResponseDto(updatedCoupon);
    }
}