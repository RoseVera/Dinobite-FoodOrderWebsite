package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.FoodRequestDto;
import com.dinobite.dinobitebackend.dto.FoodResponseDto;
import com.dinobite.dinobitebackend.service.FoodService;
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
 * FoodController.java
 * This class handles HTTP requests related to food items in a restaurant.
 * It provides endpoints for creating, updating, deleting, and fetching food items.
 */
@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/foods")
@RequiredArgsConstructor
@Validated
public class FoodController {

    // Constructor-based dependency injection
    private final FoodService foodService;

    /**
     * Create a new food item for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param requestDto   the request DTO containing food details
     * @return ResponseEntity with the created food item and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FoodResponseDto> createFood(
            @PathVariable Integer restaurantId,
            @RequestBody @Valid FoodRequestDto requestDto) {

        requestDto.setRestaurantId(restaurantId);
        FoodResponseDto response = foodService.createFood(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Update an existing food item for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param foodId       the ID of the food item to update
     * @param requestDto   the updated food details
     * @return the updated food item
     */
    @PutMapping("/{foodId}")
    public FoodResponseDto updateFood(
            @PathVariable Integer restaurantId,
            @PathVariable Integer foodId,
            @RequestBody @Valid FoodRequestDto requestDto) {

        requestDto.setRestaurantId(restaurantId);
        return foodService.updateFood(foodId, requestDto);
    }

    /**
     * Delete a food item for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param foodId       the ID of the food item to delete
     */
    @DeleteMapping("/{foodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(
            @PathVariable Integer restaurantId,
            @PathVariable Integer foodId) {

        foodService.deleteFood(foodId, restaurantId);
    }

    /**
     * Fetch all available food items for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of available food items
     */
    @GetMapping
    public List<FoodResponseDto> getAvailableFoods(
            @PathVariable Integer restaurantId) {

        return foodService.getAvailableFoodsByRestaurant(restaurantId);
    }

    /**
     * Toggles the availability status of a specific food item.
     *
     * @param restaurantId the ID of the restaurant.
     * @param foodId the ID of the food.
     * @return the updated FoodResponseDto with a new availability status.
     */
    @PatchMapping("/{foodId}/availability")
    public FoodResponseDto toggleAvailability(
            @PathVariable Integer restaurantId,
            @PathVariable Integer foodId) {

        return foodService.toggleAvailability(foodId);
    }

    /**
     * Fetch all food items by category for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @param categoryId   the ID of the category
     * @return a list of food items in the specified category
     */
    @GetMapping("/categories/{categoryId}")
    public List<FoodResponseDto> getFoodsByCategory(
            @PathVariable Integer restaurantId,
            @PathVariable Integer categoryId) {

        return foodService.getFoodsByCategory(restaurantId, categoryId);
    }
}