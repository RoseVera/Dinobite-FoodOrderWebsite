package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.CourierRequestDto;
import com.dinobite.dinobitebackend.dto.CourierResponseDto;
import com.dinobite.dinobitebackend.service.CourierService;
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
 * CourierController.java
 * This class handles HTTP requests related to couriers.
 * It provides endpoints for registering, updating, and deleting couriers,
 * as well as fetching available couriers and courier details by user ID.
 */
@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
@Validated
public class CourierController {

    // Constructor-based dependency injection
    private final CourierService courierService;

    /**
     * Register a new courier
     * @param requestDto The request DTO containing courier details
     * @return ResponseEntity with the created courier and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CourierResponseDto> registerCourier(
            @RequestBody @Valid CourierRequestDto requestDto) {

        CourierResponseDto response = courierService.createCourier(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Fetch a courier by its User ID
     * @param userId The ID of the user
     * @return ResponseEntity with the courier details
     */
    @GetMapping("/users/{userId}")
    public CourierResponseDto getCourierByUser(
            @PathVariable Integer userId) {

        return courierService.getCourierByUserId(userId);
    }
     
    /**
     * Fetch a courier by its  ID
     * @param id The ID of the courier
     * @return ResponseEntity with the courier details
     */
    @GetMapping("/byId/{id}")
    public CourierResponseDto getCourierById(
            @PathVariable Integer id) {

        return courierService.getCourierById(id);
    }

    /**
     * Updates the availability status of a courier.
     *
     * @param courierId    the ID of the courier.
     * @param availability the new availability status.
     * @return the updated courier information.
     */
    @PatchMapping("/{courierId}/availability")
    public CourierResponseDto updateAvailability(
            @PathVariable Integer courierId,
            @RequestParam Boolean availability) {

        return courierService.updateAvailability(courierId, availability);
    }

    /**
     * Fetch all available couriers
     * @return List of available couriers
     */
    @GetMapping("/available")
    public List<CourierResponseDto> getAvailableCouriers() {
        return courierService.getAvailableCouriers();
    }

    /**
     * Updates the details of a courier.
     *
     * @param courierId  the ID of the courier to update.
     * @param requestDto the updated courier information.
     * @return the updated courier details.
     */
    @PutMapping("/{courierId}")
    public ResponseEntity<CourierResponseDto> updateCourier(
            @PathVariable Integer courierId,
            @RequestBody @Valid CourierRequestDto requestDto) {

        return ResponseEntity.ok(courierService.updateCourier(courierId, requestDto));
    }

    /**
     * Deletes a courier by its ID.
     *
     * @param courierId the ID of the courier to delete.
     */
    @DeleteMapping("/{courierId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourier(@PathVariable Integer courierId) {
        courierService.deleteCourier(courierId);
    }
}