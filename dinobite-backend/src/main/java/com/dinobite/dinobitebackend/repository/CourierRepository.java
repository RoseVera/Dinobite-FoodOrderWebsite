package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Courier entities in the database.
 */
@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer> {

    /**
     * Retrieves a Courier entity by the given user ID.
     *
     * @param userId The ID of the user to search for
     * @return An Optional containing the Courier entity if found, otherwise empty
     */
    Optional<Courier> findByUserId(Integer userId);

    
    Optional<Courier> findById(Integer id);

    /**
     * Retrieves a list of Courier entities where availability is set to true.
     *
     * @return A list of Courier entities with availability set to true
     */
    List<Courier> findByAvailabilityTrue();

    /**
     * Retrieves a list of active couriers that are currently available.
     *
     * @return A list of Courier entities that have 'ACTIVE' status and are available
     */
    @Query("SELECT c FROM Courier c WHERE c.status = 'ACTIVE' AND c.availability = true")
    List<Courier> findAvailableActiveCouriers();

    /**
     * Checks if a user exists based on their user ID.
     *
     * @param userId The ID of the user to check for existence
     * @return true if a user with the given ID exists, false otherwise
     */
    boolean existsByUserId(Integer userId);
}