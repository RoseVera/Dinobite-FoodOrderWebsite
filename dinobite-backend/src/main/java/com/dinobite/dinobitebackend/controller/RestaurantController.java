package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.RestaurantRequestDto;
import com.dinobite.dinobitebackend.dto.RestaurantResponseDto;
import com.dinobite.dinobitebackend.dto.RestaurantSearchDto;
import com.dinobite.dinobitebackend.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

/**
 * RestaurantController.java
 * This class handles HTTP requests related to restaurants.
 * It provides endpoints for creating, updating, deleting, and fetching restaurant details.
 */
@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Validated
public class RestaurantController {

    // Constructor-based dependency injection
    private final RestaurantService restaurantService;

    /**
     * Create a new restaurant
     *
     * @param requestDto The request DTO containing restaurant details
     * @return ResponseEntity with the created restaurant and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RestaurantResponseDto> createRestaurant(
            @RequestBody @Valid RestaurantRequestDto requestDto) {

        RestaurantResponseDto response = restaurantService.createRestaurant(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Search for restaurants based on various criteria.
     *
     * @param name       The name of the restaurant (optional)
     * @param cuisine    The type of cuisine (optional)
     * @param minPrice   The minimum price (optional)
     * @param maxPrice   The maximum price (optional)
     * @return A list of restaurants matching the search criteria
     */
    @GetMapping("/search")
    public List<RestaurantResponseDto> searchRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        RestaurantSearchDto searchDto = RestaurantSearchDto.builder()
                .name(name)
                .cuisine(cuisine)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        return restaurantService.searchRestaurants(searchDto);
    }

    /**
     * Fetch all restaurants.
     *
     * @return A list of all restaurants
     */
    @GetMapping
    public List<RestaurantResponseDto> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    /**
     * Fetch all restaurants with a specific cuisine type.
     *
     * @param cuisineType The type of cuisine
     * @return A list of restaurants with the specified cuisine type
     */
    @GetMapping("/cuisine/{cuisineType}")
    public List<RestaurantResponseDto> filterByCuisine(
            @PathVariable String cuisineType) {

        return restaurantService.filterByCuisine(cuisineType);
    }

    /**
     * Fetch a restaurant by its ID.
     *
     * @param restaurantId The ID of the restaurant
     * @return The restaurant details
     */
    @GetMapping("/{restaurantId}")
    public RestaurantResponseDto getRestaurant(
            @PathVariable Integer restaurantId) {

        return restaurantService.getRestaurantDetails(restaurantId);
    }

    /**
     * Fetch a restaurant by its user ID.
     *
     * @param userId The ID of the user
     * @return The restaurant details
     */
    @GetMapping("/users/{userId}")
    public RestaurantResponseDto getRestaurantByUser(
            @PathVariable Integer userId) {
        return restaurantService.getRestaurantByUserId(userId);
    }

    /**
     * Update an existing restaurant.
     *
     * @param restaurantId The ID of the restaurant to update
     * @param requestDto   The updated restaurant details
     * @return The updated restaurant details
     */
    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
            @PathVariable Integer restaurantId,
            @RequestBody @Valid RestaurantRequestDto requestDto) {

        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, requestDto));
    }

    /**
     * Delete a restaurant by its ID.
     *
     * @param restaurantId The ID of the restaurant to delete
     */
    @DeleteMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable Integer restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
    }
}