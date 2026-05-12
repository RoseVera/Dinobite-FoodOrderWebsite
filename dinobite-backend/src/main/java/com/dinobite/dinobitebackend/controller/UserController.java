package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.PasswordUpdateDto;
import com.dinobite.dinobitebackend.dto.UserCreateDto;
import com.dinobite.dinobitebackend.dto.UserRequestDto;
import com.dinobite.dinobitebackend.dto.UserResponseDto;
import com.dinobite.dinobitebackend.enums.UserType;
import com.dinobite.dinobitebackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * UserController.java
 * This class handles HTTP requests related to user management.
 * It provides endpoints for creating, updating, deleting, and fetching users.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    // Constructor-based dependency injection
    private final UserService userService;

    /**
     * Create a new user.
     *
     * @param requestDto the request DTO containing user details
     * @return ResponseEntity with the created user and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody @Valid UserCreateDto requestDto) {

        UserResponseDto response = userService.createUser(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Fetch all users or filter by user type.
     *
     * @param type the user types to filter by (optional)
     * @return List of UserResponseDto
     */
    @GetMapping
    public List<UserResponseDto> getAllUsers(
            @RequestParam(required = false) UserType type) {
        return userService.getAllUsers(type);
    }

    /**
     * Fetch a user by their ID.
     *
     * @param userId the ID of the user
     * @return UserResponseDto containing user details
     */
    @GetMapping("/{userId}")
    public UserResponseDto getUserById(
            @PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    /**
     * Updates user information for a specific user ID.
     *
     * @param userId    the ID of the user to update
     * @param requestDto the updated user data
     * @return the updated UserResponseDto
     */
    @PutMapping("/{userId}")
    public UserResponseDto updateUser(
            @PathVariable Integer userId,
            @RequestBody @Valid UserRequestDto requestDto) {
        return userService.updateUser(userId, requestDto);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Integer userId) {
        userService.deleteUser(userId);
    }

    /**
     * Updates the password for a specific user.
     *
     * @param userId         the ID of the user
     * @param passwordUpdate the new password details
     */
    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(
            @PathVariable Integer userId,
            @RequestBody @Valid PasswordUpdateDto passwordUpdate) {

        userService.updatePassword(userId, passwordUpdate);
    }
}