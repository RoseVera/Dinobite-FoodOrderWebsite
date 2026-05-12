package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Category entities in the database.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Retrieves a list of categories associated with a specific restaurant ID.
     *
     * @param restaurantId The ID of the restaurant to search for categories.
     * @return A list of Category objects associated with the given restaurant ID.
     */
    List<Category> findByRestaurantId(Integer restaurantId);

    /**
     * Checks if an entity with the given name and restaurant ID exists.
     *
     * @param name The name to check for
     * @param restaurantId The ID of the restaurant to check against
     * @return true if an entity with the given name and restaurant ID exists, false otherwise
     */
    boolean existsByNameAndRestaurantId(String name, Integer restaurantId);

    /**
     * Retrieves a category by its ID and the ID of the associated restaurant.
     *
     * @param id The ID of the category to find
     * @param restaurantId The ID of the restaurant to which the category belongs
     * @return An Optional containing the category if found, otherwise empty
     */
    Optional<Category> findByIdAndRestaurantId(Integer id, Integer restaurantId);
}