package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.RestaurantRequestDto;
import com.dinobite.dinobitebackend.dto.RestaurantResponseDto;
import com.dinobite.dinobitebackend.dto.RestaurantSearchDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.CategoryMapper;
import com.dinobite.dinobitebackend.mapper.FoodMapper;
import com.dinobite.dinobitebackend.mapper.RestaurantMapper;
import com.dinobite.dinobitebackend.model.Category;
import com.dinobite.dinobitebackend.model.Food;
import com.dinobite.dinobitebackend.model.Restaurant;
import com.dinobite.dinobitebackend.model.User;
import com.dinobite.dinobitebackend.repository.CategoryRepository;
import com.dinobite.dinobitebackend.repository.FoodRepository;
import com.dinobite.dinobitebackend.repository.RestaurantRepository;
import com.dinobite.dinobitebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Restaurant entities.
 * Handles creation, retrieval, updating, and deletion of restaurants.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RestaurantService {

    // Repository for accessing Restaurant data.
    private final RestaurantRepository restaurantRepository;

    // Repository for accessing User data.
    private final UserRepository userRepository;

    // Repository for accessing Category data.
    private final CategoryRepository categoryRepository;

    // Repository for accessing Food data.
    private final FoodRepository foodRepository;

    // Mapper for converting between Restaurant entities and DTOs.
    private final RestaurantMapper restaurantMapper;

    // Mapper for converting between Category entities and DTOs.
    private final FoodMapper foodMapper;

    // Mapper for converting between Food entities and DTOs.
    private final CategoryMapper categoryMapper;

    /**
     * Creates a new restaurant.
     *
     * @param requestDto the DTO containing restaurant data.
     * @return RestaurantResponseDto representing the created restaurant.
     * @throws BusinessException if a restaurant already exists for the user ID.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Restaurant restaurant = restaurantMapper.toEntity(requestDto);
        restaurant.setUser(user);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        log.info("Created restaurant with ID: {}", savedRestaurant.getId());
        return restaurantMapper.toResponseDto(savedRestaurant);
    }

    /**
     * Searches for restaurants based on the provided search criteria.
     *
     * @param searchDto the DTO containing search criteria.
     * @return a list of RestaurantResponseDto matching the search criteria.
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> searchRestaurants(RestaurantSearchDto searchDto) {
        List<Restaurant> restaurants;

        if (searchDto == null || allParamsNull(searchDto)) {
            restaurants = restaurantRepository.findAllWithUser();
        } else {
            restaurants = restaurantRepository.searchRestaurants(
                    searchDto.getName(),
                    searchDto.getCuisine(),
                    searchDto.getMinPrice(),
                    searchDto.getMaxPrice()
            );
        }

        return restaurants.stream()
                .map(this::enrichRestaurantWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all restaurants.
     *
     * @return a list of RestaurantResponseDto representing all restaurants.
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAllWithUser();

        return restaurants.stream()
                .map(this::enrichRestaurantWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Checks if all parameters in the search DTO are null.
     *
     * @param dto the RestaurantSearchDto to check.
     * @return true if all parameters are null, false otherwise.
     */
    private boolean allParamsNull(RestaurantSearchDto dto) {
        return dto.getName() == null &&
                dto.getCuisine() == null &&
                dto.getMinPrice() == null &&
                dto.getMaxPrice() == null;
    }

    /**
     * Filters restaurants by cuisine type.
     *
     * @param cuisineType the cuisine type to filter by.
     * @return a list of RestaurantResponseDto matching the cuisine type.
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> filterByCuisine(String cuisineType) {
        List<Restaurant> restaurants = restaurantRepository.findByCuisineContainingIgnoreCase(cuisineType);

        return restaurants.stream()
                .map(this::enrichRestaurantWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * Enriches a restaurant with its categories and foods.
     *
     * @param restaurant the Restaurant entity to enrich.
     * @return RestaurantResponseDto representing the enriched restaurant.
     */
    private RestaurantResponseDto enrichRestaurantWithDetails(Restaurant restaurant) {
        List<Category> categories = categoryRepository.findByRestaurantId(restaurant.getId());
        List<Food> foods = foodRepository.findByRestaurantId(restaurant.getId());

        RestaurantResponseDto response = restaurantMapper.toResponseDto(restaurant);
        response.setCategories(categoryMapper.toResponseDtoList(categories));
        response.setFoods(foodMapper.toResponseDtoList(foods));

        return response;
    }

    /**
     * Retrieves restaurant details by ID.
     *
     * @param restaurantId the ID of the restaurant.
     * @return RestaurantResponseDto representing the restaurant details.
     * @throws ResourceNotFoundException if the restaurant is not found.
     */
    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurantDetails(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findWithUserById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        List<Category> categories = categoryRepository.findByRestaurantId(restaurantId);
        List<Food> foods = foodRepository.findByRestaurantId(restaurantId);

        RestaurantResponseDto response = restaurantMapper.toResponseDto(restaurant);
        response.setCategories(categoryMapper.toResponseDtoList(categories));
        response.setFoods(foodMapper.toResponseDtoList(foods));

        return response;
    }

    /**
     * Retrieves restaurant details by user ID.
     *
     * @param userId the ID of the user.
     * @return RestaurantResponseDto representing the restaurant details.
     * @throws ResourceNotFoundException if the restaurant is not found.
     */
    public RestaurantResponseDto getRestaurantByUserId(Integer userId) {
        return restaurantRepository.findByUserId(userId)
                .map(restaurantMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    /**
     * Updates an existing restaurant.
     *
     * @param restaurantId the ID of the restaurant to update.
     * @param requestDto the DTO containing updated restaurant data.
     * @return RestaurantResponseDto representing the updated restaurant.
     * @throws ResourceNotFoundException if the restaurant is not found.
     * @throws BusinessException if the user already has a restaurant.
     */
    public RestaurantResponseDto updateRestaurant(Integer restaurantId, RestaurantRequestDto requestDto) {
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        if (!existingRestaurant.getUser().getId().equals(requestDto.getUserId())) {
            if (restaurantRepository.existsByUserId(requestDto.getUserId())) {
                throw new BusinessException("User already has a restaurant");
            }
        }

        existingRestaurant.setBusinessOwner(requestDto.getBusinessOwner());
        existingRestaurant.setOwnerMail(requestDto.getOwnerMail());
        existingRestaurant.setPhone(requestDto.getPhone());
        existingRestaurant.setHours(requestDto.getHours());
        existingRestaurant.setCuisine(requestDto.getCuisine());
        existingRestaurant.setDeliveryRange(requestDto.getDeliveryRange());
        existingRestaurant.setAddress(requestDto.getAddress());
        existingRestaurant.setLogo(requestDto.getLogo());

        if (!existingRestaurant.getUser().getId().equals(requestDto.getUserId())) {
            User newUser = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            existingRestaurant.setUser(newUser);
        }

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        log.info("Updated restaurant with ID: {}", restaurantId);
        return restaurantMapper.toResponseDto(updatedRestaurant);
    }

    /**
     * Deletes a restaurant by ID.
     *
     * @param restaurantId the ID of the restaurant to delete.
     * @throws ResourceNotFoundException if the restaurant is not found.
     */
    public void deleteRestaurant(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        foodRepository.deleteAll(restaurant.getFoods());
        categoryRepository.deleteAll(restaurant.getCategories());

        restaurantRepository.delete(restaurant);

        log.info("Deleted restaurant with ID: {} along with its {} foods and {} categories",
                restaurantId, restaurant.getFoods().size(), restaurant.getCategories().size());
    }
}