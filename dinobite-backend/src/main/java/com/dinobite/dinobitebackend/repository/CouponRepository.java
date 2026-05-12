package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Coupon entities in the database.
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    /**
     * Retrieves a list of coupons associated with a specific customer ID.
     *
     * @param customerId The ID of the customer to search for
     * @return A list of coupons belonging to the specified customer
     */
    List<Coupon> findByCustomerId(Integer customerId);

    /**
     * Retrieves a coupon by its unique code.
     *
     * @param code The unique code of the coupon to find
     * @return An Optional containing the coupon if found, or empty if not found
     */
    Optional<Coupon> findByCode(String code);

    /**
     * Checks if an entity with the given code exists.
     *
     * @param code The code to check for existence
     * @return true if an entity with the code exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Retrieves a list of active coupons for a specific customer based on their ID.
     *
     * @param customerId The ID of the customer to retrieve active coupons for
     * @return A list of active coupons for the specified customer
     */
    @Query("SELECT c FROM Coupon c WHERE c.customer.id = :customerId AND c.status = true AND c.expirationDate > CURRENT_TIMESTAMP")
    List<Coupon> findActiveCouponsByCustomer(@Param("customerId") Integer customerId);
}