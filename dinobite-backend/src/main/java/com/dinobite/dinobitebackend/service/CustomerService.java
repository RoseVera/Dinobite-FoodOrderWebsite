package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.CustomerRequestDto;
import com.dinobite.dinobitebackend.dto.CustomerResponseDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.CustomerMapper;
import com.dinobite.dinobitebackend.model.Customer;
import com.dinobite.dinobitebackend.model.User;
import com.dinobite.dinobitebackend.repository.CustomerRepository;
import com.dinobite.dinobitebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Customer entities.
 * Handles creation, retrieval, updating, and deletion of customers.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService {

    // Repository for accessing Customer data.
    private final CustomerRepository customerRepository;

    // Repository for accessing User data, used to validate the existence of a user.
    private final UserRepository userRepository;

    // Mapper for converting between Customer entities and DTOs.
    private final CustomerMapper customerMapper;

    /**
     * Creates a new customer.
     *
     * @param requestDto the DTO containing customer data.
     * @return CustomerResponseDto representing the created customer.
     * @throws BusinessException if a customer already exists for the user ID.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public CustomerResponseDto createCustomer(CustomerRequestDto requestDto) {
        if (customerRepository.existsByUserId(requestDto.getUserId())) {
            throw new BusinessException("Customer already exists for this user");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = customerMapper.toEntity(requestDto);
        customer.setUser(user);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Created customer for user ID: {}", user.getId());
        return customerMapper.toResponseDto(savedCustomer);
    }

    /**
     * Updates an existing customer.
     *
     * @param id the ID of the customer to update.
     * @param requestDto the DTO containing updated customer data.
     * @return CustomerResponseDto representing the updated customer.
     * @throws ResourceNotFoundException if the customer is not found.
     */
    public CustomerResponseDto updateCustomer(Integer id, CustomerRequestDto requestDto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerMapper.updateEntityFromDto(requestDto, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        log.info("Updated customer with ID: {}", id);
        return customerMapper.toResponseDto(updatedCustomer);
    }

    /**
     * Retrieves a customer by user ID.
     *
     * @param userId the ID of the user associated with the customer.
     * @return CustomerResponseDto representing the customer.
     * @throws ResourceNotFoundException if the customer is not found.
     */
    public CustomerResponseDto getCustomerByUserId(Integer userId) {
        return customerRepository.findByUserId(userId)
                .map(customerMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    /**
     * Deletes a customer by ID.
     *
     * @param id the ID of the customer to delete.
     */
    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
        log.info("Deleted customer with ID: {}", id);
    }
}