package com.dinobite.dinobitebackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * CouponRequestDto.java
 * This class represents the request payload for creating or updating a coupon.
 * It contains the coupon code, discount percentage, expiration date, and associated customer ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRequestDto {

    @NotBlank
    @Size(min = 5, max = 15)
    private String code;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer discountPercent;

    @NotNull
    @Future
    private LocalDateTime expirationDate;

    @NotNull
    private Integer customerId;
}