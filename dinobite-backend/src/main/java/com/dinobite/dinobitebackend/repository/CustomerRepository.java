package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Customer;
import com.dinobite.dinobitebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Customer entities in the database.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Retrieves a customer by their user ID.
     *
     * @param userId The user ID of the customer to find
     * @return An Optional containing the customer if found, otherwise empty
     */
    Optional<Customer> findByUserId(Integer userId);

    /**
     * Checks if a user exists based on their user ID.
     *
     * @param userId The ID of the user to check for existence
     * @return true if a user with the given ID exists, false otherwise
     */
    boolean existsByUserId(Integer userId);
    
    /**
     * Retrieves a customer by the associated user.
     *
     * @param user The user to find the customer for
     * @return An Optional containing the customer if found, otherwise empty
     */
    Optional<Customer> findByUser(User user);
}