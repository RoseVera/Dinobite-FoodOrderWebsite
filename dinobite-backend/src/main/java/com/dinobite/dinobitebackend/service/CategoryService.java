package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.CategoryRequestDto;
import com.dinobite.dinobitebackend.dto.CategoryResponseDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.CategoryMapper;
import com.dinobite.dinobitebackend.model.Category;
import com.dinobite.dinobitebackend.model.Restaurant;
import com.dinobite.dinobitebackend.repository.CategoryRepository;
import com.dinobite.dinobitebackend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Category entities.
 * Handles creation, retrieval, update, and deletion operations for categories,
 * with appropriate checks and exception handling.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    // Repository for accessing Category data.
    private final CategoryRepository categoryRepository;

    // Repository for accessing Restaurant data.
    private final RestaurantRepository restaurantRepository;

    // Mapper for converting between Category entities and DTOs.
    private final CategoryMapper categoryMapper;

    /**
     * Creates a new category for a restaurant.
     * Ensures that the category name is unique within the same restaurant.
     *
     * @param requestDto the DTO containing category creation details.
     * @return the created Category as a response DTO.
     * @throws BusinessException if a category with the same name already exists for the restaurant.
     * @throws ResourceNotFoundException if the restaurant does not exist.
     */
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        if (categoryRepository.existsByNameAndRestaurantId(requestDto.getName(), requestDto.getRestaurantId())) {
            throw new BusinessException("Categoryyyyy name already exists for this restaurant");
        }

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Category category = categoryMapper.toEntity(requestDto);
        category.setRestaurant(restaurant);
        Category savedCategory = categoryRepository.save(category);

        log.info("Created new category with id: {}", savedCategory.getId());
        return categoryMapper.toResponseDto(savedCategory);
    }

    /**
     * Retrieves a specific category by its ID and restaurant ID.
     *
     * @param id the ID of the category.
     * @param restaurantId the ID of the associated restaurant.
     * @return the found Category as a response DTO.
     * @throws ResourceNotFoundException if the category does not exist for the given restaurant.
     */
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Integer id, Integer restaurantId) {
        Category category = categoryRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id + " for restaurant id: " + restaurantId));
        return categoryMapper.toResponseDto(category);
    }

    /**
     * Retrieves all categories associated with a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant.
     * @return a list of Category response DTOs.
     * @throws ResourceNotFoundException if the restaurant does not exist.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategoriesByRestaurant(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found");
        }

        List<Category> categories = categoryRepository.findByRestaurantId(restaurantId);
        return categoryMapper.toResponseDtoList(categories);
    }

    /**
     * Updates an existing category's information.
     *
     * @param id the ID of the category to update.
     * @param requestDto the DTO containing the updated category details.
     * @return the updated Category as a response DTO.
     * @throws ResourceNotFoundException if the category does not exist for the given restaurant.
     */
    public CategoryResponseDto updateCategory(Integer id, CategoryRequestDto requestDto) {
        Category existingCategory = categoryRepository.findByIdAndRestaurantId(id, requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        categoryMapper.updateEntityFromDto(requestDto, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);

        log.info("Updated category with id: {}", id);
        return categoryMapper.toResponseDto(updatedCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete.
     * @throws ResourceNotFoundException if the category does not exist.
     */
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
        log.info("Deleted category with id: {}", id);
    }
}