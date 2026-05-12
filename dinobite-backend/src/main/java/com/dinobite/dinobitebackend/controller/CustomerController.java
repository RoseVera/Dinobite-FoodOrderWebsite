package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.dto.CustomerRequestDto;
import com.dinobite.dinobitebackend.dto.CustomerResponseDto;
import com.dinobite.dinobitebackend.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * CustomerController.java
 * This class handles HTTP requests related to customers.
 * It provides endpoints for creating, updating, deleting, and fetching customer details.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    // Constructor-based dependency injection
    private final CustomerService customerService;

    /**
     * Create a new customer
     * @param requestDto The request DTO containing customer details
     * @return ResponseEntity with the created customer and HTTP status 201 (Created)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @RequestBody @Valid CustomerRequestDto requestDto) {

        CustomerResponseDto response = customerService.createCustomer(requestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Updates customer information for a specific customer ID.
     *
     * @param id         the ID of the customer to update.
     * @param requestDto the updated customer data.
     * @return the updated customer details.
     */
    @PutMapping("/{id}")
    public CustomerResponseDto updateCustomer(
            @PathVariable Integer id,
            @RequestBody @Valid CustomerRequestDto requestDto) {

        return customerService.updateCustomer(id, requestDto);
    }

    /**
     * Retrieves a customer based on the associated user ID.
     *
     * @param userId the ID of the user.
     * @return the corresponding CustomerResponseDto.
     */
    @GetMapping("/users/{userId}")
    public CustomerResponseDto getCustomerByUser(
            @PathVariable Integer userId) {
        return customerService.getCustomerByUserId(userId);
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param id the ID of the customer to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(
            @PathVariable Integer id) {
        customerService.deleteCustomer(id);
    }


}