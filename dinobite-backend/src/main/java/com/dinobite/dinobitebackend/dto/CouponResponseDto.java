package com.dinobite.dinobitebackend.dto;

import lombok.*;

/**
 * CouponResponseDto.java
 * This class represents the response payload for a coupon.
 * It contains the coupon ID, code, discount percentage, expiration date, status, and associated customer ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponseDto {

    private Integer id;
    private String code;
    private Integer discountPercent;
    private String expirationDate;
    private Boolean status;
    private Integer customerId;
}