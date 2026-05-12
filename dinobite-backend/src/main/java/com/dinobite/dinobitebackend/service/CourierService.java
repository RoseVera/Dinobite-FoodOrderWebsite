package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.CourierRequestDto;
import com.dinobite.dinobitebackend.dto.CourierResponseDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.CourierMapper;
import com.dinobite.dinobitebackend.model.Courier;
import com.dinobite.dinobitebackend.model.User;
import com.dinobite.dinobitebackend.repository.CourierRepository;
import com.dinobite.dinobitebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Service class for managing Courier entities.
 * Handles creation, retrieval, updating, and deletion of couriers.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourierService {

    // Repository for accessing Courier data.
    private final CourierRepository courierRepository;

    // Repository for accessing User data, used to validate the existence of a user.
    private final UserRepository userRepository;

    // Mapper for converting between Courier entities and DTOs.
    private final CourierMapper courierMapper;

    /**
     * Retrieves courier by user ID.
     *
     * @param userId the ID of the user associated with the courier.
     * @return CourierResponseDto representing the courier.
     */
    @Transactional(readOnly = true)
    public CourierResponseDto getCourierByUserId(Integer userId) {
        return courierRepository.findByUserId(userId)
                .map(courierMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found for user ID: " + userId));
    }

     @Transactional(readOnly = true)
    public CourierResponseDto getCourierById(Integer id) {
        return courierRepository.findById(id)
                .map(courierMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found for ID: " + id));
    }


    /**
     * Retrieves a list of available couriers.
     *
     * @return a list of CourierResponseDto representing available couriers.
     * @throws ResourceNotFoundException if no available couriers are found.
     */
    @Transactional(readOnly = true)
    public List<CourierResponseDto> getAvailableCouriers() {
        List<Courier> availableCouriers = courierRepository.findAvailableActiveCouriers();

        if (availableCouriers.isEmpty()) {
            log.info("No available couriers at {}", LocalDateTime.now());
            return Collections.emptyList();
        }

        return courierMapper.toResponseDtoList(availableCouriers);
    }

    /**
     * Creates a new courier profile.
     *
     * @param requestDto the DTO containing courier details including the user ID.
     * @return created Courier as a response DTO.
     * @throws BusinessException if a courier already exists for the user.
     * @throws ResourceNotFoundException if the user does not exist.
     */
    public CourierResponseDto createCourier(CourierRequestDto requestDto) {
        if (courierRepository.existsByUserId(requestDto.getUserId())) {
            throw new BusinessException("Courier already exists for this user");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Courier courier = courierMapper.toEntity(requestDto);
        courier.setUser(user);
        Courier savedCourier = courierRepository.save(courier);

        log.info("Created courier with ID: {}", savedCourier.getId());
        return courierMapper.toResponseDto(savedCourier);
    }

    /**
     * Updates the availability status of a courier.
     *
     * @param courierId the ID of the courier to update.
     * @param availability the new availability status.
     * @return updated Courier as a response DTO.
     * @throws ResourceNotFoundException if the courier does not exist.
     */
    public CourierResponseDto updateAvailability(Integer courierId, Boolean availability) {
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        courier.setAvailability(availability);
        Courier updatedCourier = courierRepository.save(courier);

        log.info("Updated availability for courier ID: {} to {}", courierId, availability);
        return courierMapper.toResponseDto(updatedCourier);
    }

    /**
     * Updates the details of an existing courier.
     *
     * @param courierId the ID of the courier to update.
     * @param requestDto the DTO containing updated courier details.
     * @return updated Courier as a response DTO.
     * @throws ResourceNotFoundException if the courier or user does not exist.
     * @throws BusinessException if a user already has a courier profile.
     */
    public CourierResponseDto updateCourier(Integer courierId, CourierRequestDto requestDto) {
        Courier existingCourier = courierRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found with ID: " + courierId));

        if (!existingCourier.getUser().getId().equals(requestDto.getUserId())) {
            if (courierRepository.existsByUserId(requestDto.getUserId())) {
                throw new BusinessException("User already has a courier profile");
            }
        }

        existingCourier.setAvailability(requestDto.getAvailability());
        existingCourier.setPhoto(requestDto.getPhoto());
        existingCourier.setBirthDate(requestDto.getBirthDate());
        existingCourier.setStatus(requestDto.getStatus());


        if (!existingCourier.getUser().getId().equals(requestDto.getUserId())) {
            User newUser = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            existingCourier.setUser(newUser);
        }

        Courier updatedCourier = courierRepository.save(existingCourier);
        log.info("Updated courier with ID: {}", courierId);
        return courierMapper.toResponseDto(updatedCourier);
    }

    /**
     * Deletes a courier by ID.
     *
     * @param courierId the ID of the courier to delete.
     * @throws ResourceNotFoundException if the courier does not exist.
     */
    public void deleteCourier(Integer courierId) {
        if (!courierRepository.existsById(courierId)) {
            throw new ResourceNotFoundException("Courier not found with ID: " + courierId);
        }

        courierRepository.deleteById(courierId);

        log.info("Deleted (deactivated) courier with ID: {}", courierId);
    }
}