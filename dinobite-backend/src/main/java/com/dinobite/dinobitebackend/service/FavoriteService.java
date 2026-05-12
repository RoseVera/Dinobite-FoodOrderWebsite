package com.dinobite.dinobitebackend.service;

import com.dinobite.dinobitebackend.dto.FavoriteRequestDto;
import com.dinobite.dinobitebackend.dto.FavoriteResponseDto;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.exception.ResourceNotFoundException;
import com.dinobite.dinobitebackend.mapper.FavoriteMapper;
import com.dinobite.dinobitebackend.model.Customer;
import com.dinobite.dinobitebackend.model.Favorite;
import com.dinobite.dinobitebackend.model.Restaurant;
import com.dinobite.dinobitebackend.repository.CustomerRepository;
import com.dinobite.dinobitebackend.repository.FavoriteRepository;
import com.dinobite.dinobitebackend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Favorite entities.
 * Handles adding, removing, and retrieving favorites for customers.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FavoriteService {

    // Repository for accessing Favorite data.
    private final FavoriteRepository favoriteRepository;

    // Repository for accessing Customer data.
    private final CustomerRepository customerRepository;

    // Repository for accessing Restaurant data.
    private final RestaurantRepository restaurantRepository;

    // Mapper for converting between Favorite entities and DTOs.
    private final FavoriteMapper favoriteMapper;

    /**
     * Retrieves a list of favorites for a given customer.
     *
     * @param customerId the ID of the customer.
     * @return a list of FavoriteResponseDto representing the customer's favorites.
     */
    @Transactional(readOnly = true)
    public List<FavoriteResponseDto> getCustomerFavorites(Integer customerId) {
        List<Favorite> favorites = favoriteRepository.findByCustomerIdWithRestaurant(customerId);
        return favoriteMapper.toResponseDtoList(favorites);
    }

    /**
     * Adds a restaurant to the customer's favorites.
     *
     * @param requestDto the DTO containing customer and restaurant IDs.
     * @return FavoriteResponseDto representing the added favorite.
     * @throws BusinessException if the restaurant is already in favorites.
     * @throws ResourceNotFoundException if the customer or restaurant is not found.
     */
    public FavoriteResponseDto addFavorite(FavoriteRequestDto requestDto) {
        if (favoriteRepository.existsByCustomerIdAndRestaurantId(
                requestDto.getCustomerId(), requestDto.getRestaurantId())) {
            throw new BusinessException("Restaurant is already in favorites");
        }

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Favorite favorite = favoriteMapper.toEntity(requestDto);
        favorite.setCustomer(customer);
        favorite.setRestaurant(restaurant);
        Favorite savedFavorite = favoriteRepository.save(favorite);

        log.info("Added restaurant {} to favorites for customer {}",
                restaurant.getId(), customer.getId());
        return favoriteMapper.toResponseDto(savedFavorite);
    }

    /**
     * Removes a restaurant from the customer's favorites.
     *
     * @param customerId the ID of the customer.
     * @param restaurantId the ID of the restaurant to remove.
     * @throws ResourceNotFoundException if the favorite is not found.
     */
    public void removeFavorite(Integer customerId, Integer restaurantId) {
        if (!favoriteRepository.existsByCustomerIdAndRestaurantId(customerId, restaurantId)) {
            throw new ResourceNotFoundException("Favorite not found");
        }

        favoriteRepository.deleteByCustomerAndRestaurant(customerId, restaurantId);
        log.info("Removed restaurant {} from favorites for customer {}",
                restaurantId, customerId);
    }
}