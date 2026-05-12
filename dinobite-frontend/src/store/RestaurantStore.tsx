import { create } from "zustand";
import axios from 'axios';
import { useOrderStore } from "./OrderStore.ts";
import { useEffect } from "react";

interface Food {
    id: string;
    name: string;
    price: number;
    description: string;
    availability: boolean;
    image: string;
    protein: number;
    carbs: number;
    fats: number;
    sugar: number;
}

interface Category {
    id: string;
    name: string;
    foods: Food[];
}

interface Restaurant {
    id: string;
    name: string;
    phone: string;
    hours: string;
    address?: string;
    business_owner: string;
    min: number;
    cuisine: string;
    deliveryRange: string;
    logo: string;
    categories: Category[];
    foods?: Food[];
    rating?: number;
}

interface RestaurantState {
    restaurants: Restaurant[];
    loading: boolean;
    error: string | null;
    fetchRestaurants: () => Promise<void>;
    fetchCategoriesAndFoods: (restaurantId: string) => Promise<void>;
}

export const useRestaurantStore = create<RestaurantState>((set) => ({
    restaurants: [],
    loading: false,
    error: null,

    fetchRestaurants: async () => {
        set({ loading: true });
        try {
            const response = await axios.get(`http://localhost:9090/api/v1/restaurants`);
            const restaurants = response.data;
            

            const updatedRestaurants = await Promise.all(
                restaurants.map(async (restaurant: any) => {
                  try {
                    const foodRes = await axios.get(`http://localhost:9090/api/v1/restaurants/${restaurant.id}/foods`);
                    const foods = foodRes.data;
              
                    const { fetchOrdersByRestaurant } = useOrderStore.getState();
                    await fetchOrdersByRestaurant(restaurant.id);
              
                    const orders = useOrderStore.getState().orders;
              
                    const minItem = foods.reduce(
                      (min: any, item: any) => (item.price < min.price ? item : min),
                      foods[0]
                    );
              
                    const allRatings = orders
                      .map((o) => o.restaurantRate)
                      .filter((rate): rate is number => typeof rate === "number");
              
                    const avgRating =
                      allRatings.length > 0
                        ? allRatings.reduce((sum, r) => sum + r, 0) / allRatings.length
                        : 0;
              
                    return {
                      ...restaurant,
                      foods,
                      min: minItem?.price ?? 0,
                      rating: parseFloat(avgRating.toFixed(1)),
                    };
                  } catch (err) {
                    console.error(`🚨 Cannot get food info for ${restaurant.name}`, err);
                    return {
                      ...restaurant,
                      foods: [],
                      min: 0,
                      rating: 0,
                    };
                  }
                })
              );
              

            set({ restaurants: updatedRestaurants, loading: false });
        } catch (error: unknown) {
            if (error instanceof Error) {
                set({ error: error.message, loading: false });
            } else {
                set({ error: "An unknown error occurred", loading: false });
            }
        }
    },

    fetchCategoriesAndFoods: async (restaurantId: string) => {
        try {
            const categoriesResponse = await axios.get(`http://localhost:9090/api/v1/restaurants/${restaurantId}/categories`);
            const categories = categoriesResponse.data;

            const updatedCategories = await Promise.all(
                categories.map(async (category: Category) => {
                    const foodsResponse = await axios.get(`http://localhost:9090/api/v1/categories/${category.id}/foods`);
                    const foods = foodsResponse.data;
                    return { ...category, foods };
                })
            );

            // Update state
            set((state) => {
                const updatedRestaurants = state.restaurants.map((restaurant) => {
                    if (restaurant.id === restaurantId) {
                        return { ...restaurant, categories: updatedCategories };
                    }
                    return restaurant;
                });
                return { restaurants: updatedRestaurants };
            });
        } catch (error: unknown) {
            console.error("Kategori ve yemek verisi çekilemedi 🚨", error);
            if (error instanceof Error) {
                set({ error: error.message });
            } else {
                set({ error: "An unknown error occurred" });
            }
        }
    },
}));
