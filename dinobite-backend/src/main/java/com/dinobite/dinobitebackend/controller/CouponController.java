package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.CouponRequestDto;
import com.dinobite.dinobitebackend.dto.CouponResponseDto;
import com.dinobite.dinobitebackend.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * CouponController.java
 * This class handles HTTP requests related to coupons for customers.
 * It provides endpoints for creating, retrieving, and deactivating coupons.
 */
@RestController
@RequestMapping("/api/v1/customers/{customerId}/coupons")
@RequiredArgsConstructor
@Validated
public class CouponController {

    // Constructor-based dependency injection
    private final CouponService couponService;

    /**
     * Create a new coupon
     * @param customerId The ID of the customer
     * @param requestDto The request DTO containing coupon details
     * @return ResponseEntity with the created coupon and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CouponResponseDto> createCoupon(
            @PathVariable Integer customerId,
            @RequestBody @Valid CouponRequestDto requestDto) {

        requestDto.setCustomerId(customerId);
        CouponResponseDto response = couponService.createCoupon(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Fetch all active coupons for a customer
     * @param customerId The ID of the customer
     * @return List of active coupons for the customer
     */
    @GetMapping("/active")
    public List<CouponResponseDto> getActiveCoupons(
            @PathVariable Integer customerId) {

        return couponService.getActiveCouponsByCustomer(customerId);
    }

    /**
     * Deactivate a coupon
     * @param customerId The ID of the customer
     * @param couponId The ID of the coupon to deactivate
     * @return CouponResponseDto containing the deactivated coupon details
     */
    @PatchMapping("/{couponId}/deactivate")
    public CouponResponseDto deactivateCoupon(
            @PathVariable Integer customerId,
            @PathVariable Integer couponId) {

        return couponService.deactivateCoupon(couponId);
    }
}