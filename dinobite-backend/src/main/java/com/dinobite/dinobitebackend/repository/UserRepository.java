package com.dinobite.dinobitebackend.repository;

import com.dinobite.dinobitebackend.enums.UserType;
import com.dinobite.dinobitebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a User by their email address.
     *
     * @param mail the email address of the User
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByMail(String mail);

    /**
     * Checks if a User exists by their email address.
     *
     * @param mail the email address of the User
     * @return true if a User with the given email exists, false otherwise
     */
    boolean existsByMail(String mail);

    /**
     * Finds all Users by their type.
     *
     * @param type the type of the User
     * @return a List of Users with the given type
     */
    List<User> findByType(UserType type);
}