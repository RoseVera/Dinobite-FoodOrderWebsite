package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.FoodRequestDto;
import com.dinobite.dinobitebackend.dto.FoodResponseDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.FoodMapper;
import com.dinobite.dinobitebackend.model.Category;
import com.dinobite.dinobitebackend.model.Food;
import com.dinobite.dinobitebackend.model.Restaurant;
import com.dinobite.dinobitebackend.repository.CategoryRepository;
import com.dinobite.dinobitebackend.repository.FoodRepository;
import com.dinobite.dinobitebackend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Food entities.
 * Handles creation, retrieval, updating, and deletion of food items.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FoodService {

    // Repository for accessing Food data.
    private final FoodRepository foodRepository;

    // Repository for accessing Restaurant data.
    private final RestaurantRepository restaurantRepository;

    // Repository for accessing Category data.
    private final CategoryRepository categoryRepository;

    // Mapper for converting between Food entities and DTOs.
    private final FoodMapper foodMapper;

    /**
     * Creates a new food item.
     *
     * @param requestDto the DTO containing food data.
     * @return FoodResponseDto representing the created food item.
     * @throws BusinessException if food with the same name already exists in the restaurant.
     * @throws ResourceNotFoundException if the restaurant or category is not found.
     */
    public FoodResponseDto createFood(FoodRequestDto requestDto) {
        if (foodRepository.existsByNameAndRestaurantId(requestDto.getName(), requestDto.getRestaurantId())) {
            throw new BusinessException("Food name already exists in this restaurant");
        }

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Food food = foodMapper.toEntity(requestDto);
        food.setRestaurant(restaurant);
        food.setCategory(category);
        Food savedFood = foodRepository.save(food);

        log.info("Created food with ID: {}", savedFood.getId());
        return foodMapper.toResponseDto(savedFood);
    }

    /**
     * Updates an existing food item.
     *
     * @param foodId the ID of the food item to update.
     * @param requestDto the DTO containing updated food data.
     * @return FoodResponseDto representing the updated food item.
     * @throws ResourceNotFoundException if the food item is not found.
     * @throws BusinessException if the food does not belong to the specified restaurant.
     */
    public FoodResponseDto updateFood(Integer foodId, FoodRequestDto requestDto) {
        Food existingFood = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        if (!existingFood.getRestaurant().getId().equals(requestDto.getRestaurantId())) {
            throw new BusinessException("Food does not belong to specified restaurant");
        }

        foodMapper.updateFoodFromDto(requestDto, existingFood);
        Food updatedFood = foodRepository.save(existingFood);

        log.info("Updated food with ID: {}", foodId);
        return foodMapper.toResponseDto(updatedFood);
    }

    /**
     * Deletes a food item.
     *
     * @param foodId the ID of the food item to delete.
     * @param restaurantId the ID of the restaurant to which the food item belongs.
     * @throws ResourceNotFoundException if the food item is not found.
     * @throws BusinessException if the food does not belong to the specified restaurant.
     */
    public void deleteFood(Integer foodId, Integer restaurantId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        if (!food.getRestaurant().getId().equals(restaurantId)) {
            throw new BusinessException("Food does not belong to specified restaurant");
        }

        foodRepository.delete(food);
        log.info("Deleted food with ID: {} from restaurant ID: {}", foodId, restaurantId);
    }

    /**
     * Retrieves a list of available foods for a given restaurant.
     *
     * @param restaurantId the ID of the restaurant.
     * @return a list of FoodResponseDto representing available foods.
     */
    @Transactional(readOnly = true)
    public List<FoodResponseDto> getAvailableFoodsByRestaurant(Integer restaurantId) {
        return foodMapper.toResponseDtoList(
                foodRepository.findAvailableByRestaurantId(restaurantId)
        );
    }

    /**
     * Retrieves a list of foods by category for a given restaurant.
     *
     * @param restaurantId the ID of the restaurant.
     * @param categoryId the ID of the category.
     * @return a list of FoodResponseDto representing foods in the specified category.
     */
    @Transactional(readOnly = true)
    public List<FoodResponseDto> getFoodsByCategory(Integer restaurantId, Integer categoryId) {
        List<Food> foods = foodRepository.findByRestaurantIdAndCategoryId(restaurantId, categoryId);
        return foodMapper.toResponseDtoList(foods);
    }

    /**
     * Toggles the availability of a food item.
     *
     * @param foodId the ID of the food item.
     * @return FoodResponseDto representing the updated food item.
     * @throws ResourceNotFoundException if the food item is not found.
     */
    public FoodResponseDto toggleAvailability(Integer foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        food.setAvailability(!food.getAvailability());
        Food updatedFood = foodRepository.save(food);

        log.info("Toggled availability for food ID: {} to {}", foodId, food.getAvailability());
        return foodMapper.toResponseDto(updatedFood);
    }
}