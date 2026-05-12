package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Food;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Food entities in the database.
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    /**
     * Retrieves a list of food items based on the restaurant ID.
     *
     * @param restaurantId The ID of the restaurant to find food items for
     * @return A list of food items associated with the restaurant
     */
    @EntityGraph(attributePaths = {"restaurant", "category"})
    List<Food> findByRestaurantId(Integer restaurantId);

    /**
     * Retrieves a list of food items based on the category ID.
     *
     * @param categoryId The ID of the category to find food items for
     * @return A list of food items associated with the category
     */
    @EntityGraph(attributePaths = {"restaurant", "category"})
    List<Food> findByCategoryId(Integer categoryId);

    /**
     * Retrieves a list of food items based on the restaurant ID and category ID.
     *
     * @param restaurantId The ID of the restaurant to find food items for
     * @param categoryId   The ID of the category to find food items for
     * @return A list of food items associated with the restaurant and category
     */
    @EntityGraph(attributePaths = {"restaurant", "category"})
    List<Food> findByRestaurantIdAndCategoryId(
            @Param("restaurantId") Integer restaurantId,
            @Param("categoryId") Integer categoryId);

    /**
     * Retrieves a list of food items based on the restaurant ID and availability status.
     *
     * @param restaurantId The ID of the restaurant to find food items for
     * @return A list of food items associated with the restaurant that are available
     */
    @Query("SELECT f FROM Food f WHERE f.restaurant.id = :restaurantId AND f.availability = true")
    List<Food> findAvailableByRestaurantId(@Param("restaurantId") Integer restaurantId);

    /**
     * Checks if a food item exists with the given name and restaurant ID.
     *
     * @param name         The name of the food item
     * @param restaurantId The ID of the restaurant
     * @return true if a record exists with the given name and restaurant ID, false otherwise
     */
    boolean existsByNameAndRestaurantId(String name, Integer restaurantId);
}