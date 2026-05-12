package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.CategoryRequestDto;
import com.dinobite.dinobitebackend.dto.CategoryResponseDto;
import com.dinobite.dinobitebackend.service.CategoryService;
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
 * CategoryController.java
 * This class handles HTTP requests related to restaurant categories.
 * It provides endpoints for creating, retrieving, updating, and deleting categories.
 */
@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    // Constructor-based dependency injection
    private final CategoryService categoryService;

    /**
     * Create a new category
     * @param restaurantId The ID of the restaurant
     * @param requestDto The request DTO containing category details
     * @return ResponseEntity with the created category and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @PathVariable Integer restaurantId,
            @RequestBody @Valid CategoryRequestDto requestDto) {

        requestDto.setRestaurantId(restaurantId);
        CategoryResponseDto response = categoryService.createCategory(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Fetch a category by its ID
     * @param restaurantId The ID of the restaurant
     * @param categoryId The ID of the category
     * @return ResponseEntity with the category details
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(
            @PathVariable Integer restaurantId,
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId, restaurantId));
    }

    /**
     * Fetch all categories for a restaurant
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity with a list of categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategoriesByRestaurant(
            @PathVariable Integer restaurantId) {
        return ResponseEntity.ok(categoryService.getCategoriesByRestaurant(restaurantId));
    }

    /**
     * Update a category
     * @param restaurantId The ID of the restaurant
     * @param categoryId The ID of the category
     * @param requestDto The request DTO containing updated category details
     * @return ResponseEntity with the updated category
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Integer restaurantId,
            @PathVariable Integer categoryId,
            @RequestBody @Valid CategoryRequestDto requestDto) {

        requestDto.setRestaurantId(restaurantId);
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, requestDto));
    }

    /**
     * Delete a category
     * @param categoryId The ID of the category
     */
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}