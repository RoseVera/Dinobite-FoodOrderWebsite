package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.FavoriteRequestDto;
import com.dinobite.dinobitebackend.dto.FavoriteResponseDto;
import com.dinobite.dinobitebackend.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FavoriteController.java
 * This class handles HTTP requests related to customer favorites.
 * It provides endpoints for adding, removing, and fetching customer favorites.
 */
@RestController
@RequestMapping("/api/v1/customers/{customerId}/favorites")
@RequiredArgsConstructor
@Validated
public class FavoriteController {

    // Constructor-based dependency injection
    private final FavoriteService favoriteService;

    /**
     * Fetch all favorites for a specific customer ID.
     *
     * @param customerId the ID of the customer
     * @return a list of favorite restaurants for the customer
     */
    @GetMapping
    public List<FavoriteResponseDto> getFavorites(
            @PathVariable Integer customerId) {

        return favoriteService.getCustomerFavorites(customerId);
    }

    /**
     * Add a restaurant to the customer's favorites.
     *
     * @param customerId the ID of the customer
     * @param requestDto the request DTO containing restaurant details
     * @return the added favorite restaurant details
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponseDto addFavorite(
            @PathVariable Integer customerId,
            @RequestBody @Valid FavoriteRequestDto requestDto) {

        requestDto.setCustomerId(customerId);
        return favoriteService.addFavorite(requestDto);
    }

    /**
     * Remove a restaurant from the customer's favorites.
     *
     * @param customerId   the ID of the customer
     * @param restaurantId the ID of the restaurant to remove
     */
    @DeleteMapping("/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(
            @PathVariable Integer customerId,
            @PathVariable Integer restaurantId) {

        favoriteService.removeFavorite(customerId, restaurantId);
    }
}