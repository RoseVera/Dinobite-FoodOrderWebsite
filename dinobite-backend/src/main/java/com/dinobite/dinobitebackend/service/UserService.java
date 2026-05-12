package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.PasswordUpdateDto;
import com.dinobite.dinobitebackend.dto.UserCreateDto;
import com.dinobite.dinobitebackend.dto.UserRequestDto;
import com.dinobite.dinobitebackend.dto.UserResponseDto;
import com.dinobite.dinobitebackend.enums.UserType;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.UserMapper;
import com.dinobite.dinobitebackend.model.User;
import com.dinobite.dinobitebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing User entities.
 * Handles creation, retrieval, updating, and deletion of users.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    // Repository for accessing User data.
    private final UserRepository userRepository;

    // Mapper for converting between User entities and DTOs.
    private final UserMapper userMapper;

    // Password encoder for hashing passwords.
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user.
     *
     * @param requestDto the DTO containing user data.
     * @return UserResponseDto representing the created user.
     * @throws BusinessException if the email is already in use.
     */
    public UserResponseDto createUser(UserCreateDto requestDto) {
        if (userRepository.existsByMail(requestDto.getMail())) {
            throw new BusinessException("Email already in use");
        }

        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User savedUser = userRepository.save(user);

        log.info("Created user with ID: {}", savedUser.getId());
        return userMapper.toResponseDto(savedUser);
    }

    /**
     * Updates an existing user.
     *
     * @param id        the ID of the user to update.
     * @param requestDto the DTO containing updated user data.
     * @return UserResponseDto representing the updated user.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws BusinessException if the email is already in use.
     */
    public UserResponseDto updateUser(Integer id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getMail().equals(requestDto.getMail()) &&
                userRepository.existsByMail(requestDto.getMail())) {
            throw new BusinessException("Email already in use");
        }

        userMapper.updateEntityFromDto(requestDto, user);

        User updatedUser = userRepository.save(user);

        log.info("Updated user with ID: {}", id);
        return userMapper.toResponseDto(updatedUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
    }

    /**
     * Retrieves all users, optionally filtered by type.
     *
     * @param type the type of users to retrieve (optional).
     * @return a list of UserResponseDto representing the users.
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(UserType type) {
        List<User> users;

        if (type != null) {
            users = userRepository.findByType(type);
        } else {
            users = userRepository.findAll();
        }

        return userMapper.toResponseDtoList(users);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user to retrieve.
     * @return UserResponseDto representing the user.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    /**
     * Updates the password for a user.
     *
     * @param userId the ID of the user.
     * @param passwordUpdate the DTO containing the current and new passwords.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws BusinessException if the current password is incorrect.
     */
    public void updatePassword(Integer userId, PasswordUpdateDto passwordUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        userRepository.save(user);
        log.info("Updated password for user ID: {}", userId);
    }
}