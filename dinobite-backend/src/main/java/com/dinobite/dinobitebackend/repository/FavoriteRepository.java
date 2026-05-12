package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Favorite entities in the database.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    /**
     * Retrieves a list of favorite items based on the customer ID.
     *
     * @param customerId The ID of the customer to find favorites for
     * @return A list of favorite items associated with the customer
     */
    List<Favorite> findByCustomerId(Integer customerId);

    /**
     * Checks if a favorite item exists with the given customer ID and restaurant ID.
     *
     * @param customerId   The ID of the customer
     * @param restaurantId The ID of the restaurant
     * @return true if a record exists with the given customer ID and restaurant ID, false otherwise
     */
    boolean existsByCustomerIdAndRestaurantId(Integer customerId, Integer restaurantId);

    /**
     * Retrieves a list of favorite entities with their associated restaurant entity eagerly fetched
     * based on the provided customer ID.
     *
     * @param customerId The ID of the customer whose favorites are being retrieved
     * @return A list of Favorite entities with the associated Restaurant entity eagerly fetched
     */
    @Query("SELECT f FROM Favorite f JOIN FETCH f.restaurant WHERE f.customer.id = :customerId")
    List<Favorite> findByCustomerIdWithRestaurant(@Param("customerId") Integer customerId);

    /**
     * Deletes a favorite item based on the customer ID and restaurant ID.
     *
     * @param customerId   The ID of the customer
     * @param restaurantId The ID of the restaurant
     */
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.customer.id = :customerId AND f.restaurant.id = :restaurantId")
    void deleteByCustomerAndRestaurant(@Param("customerId") Integer customerId,
                                       @Param("restaurantId") Integer restaurantId);
}