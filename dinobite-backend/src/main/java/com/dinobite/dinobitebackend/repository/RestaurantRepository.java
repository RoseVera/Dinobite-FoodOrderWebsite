package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Restaurant entities in the database.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    /**
     * Finds all restaurants with their associated user entities.
     *
     * @return a list of all restaurants with their associated user entities.
    */
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT r FROM Restaurant r")
    List<Restaurant> findAllWithUser();

    /**
     * Finds all restaurants whose cuisine field contains the given string (case-insensitive),
     * with their associated user entities eagerly fetched.
     *
     * @param cuisine the partial or full cuisine name to search for.
     * @return a list of restaurants matching the cuisine search criteria.
     */
    @EntityGraph(attributePaths = {"user"})
    List<Restaurant> findByCuisineContainingIgnoreCase(String cuisine);

    /**
     * Finds a restaurant associated with a specific user by the user's ID.
     *
     * @param userId the ID of the user.
     * @return an Optional containing the matching restaurant if found, otherwise empty.
     */
    Optional<Restaurant> findByUserId(Integer userId);

    /**
     * Finds a restaurant by its ID, eagerly fetching the associated user entity.
     *
     * @param id the ID of the restaurant.
     * @return an Optional containing the restaurant with its user if found, otherwise empty.
     */
    @EntityGraph(attributePaths = {"user"})
    Optional<Restaurant> findWithUserById(Integer id);

    /**
     * Checks whether a restaurant associated with the given user ID exists.
     *
     * @param userId the ID of the user.
     * @return true if a restaurant for the user exists, false otherwise.
     */
    boolean existsByUserId(Integer userId);

    /**
     * Searches for restaurants based on optional filters: username, cuisine type,
     * minimum food price, and maximum food price.
     * If a filter is null, it will be ignored in the search.
     *
     * @param name the partial or full name of the user owning the restaurant (optional).
     * @param cuisine the partial or full cuisine type of the restaurant (optional).
     * @param minPrice the minimum price of any food item associated with the restaurant (optional).
     * @param maxPrice the maximum price of any food item associated with the restaurant (optional).
     * @return a list of restaurants matching the given search criteria.
     */
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.user u JOIN r.foods f " +
            "WHERE (:name IS NULL OR u.name LIKE %:name%) " +
            "AND (:cuisine IS NULL OR r.cuisine LIKE %:cuisine%) " +
            "AND (:minPrice IS NULL OR f.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR f.price <= :maxPrice)")
    List<Restaurant> searchRestaurants(
            @Param("name") String name,
            @Param("cuisine") String cuisine,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);
}