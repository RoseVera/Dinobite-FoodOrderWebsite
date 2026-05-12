import { create } from "zustand";

interface Order {
    id: number;
    restaurant_id: number;
    courier_id: number;
    status: string;
    items: { foodId: string; quantity: number }[];
    total_price: number;
    place_at: Date;
    delivered_at: Date;
    courier_rate: number;
    restaurant_rate: number;
}

interface Comment {
    id: number;
    restaurant_id: number;
    order_id: string;
    comment_text: string;
    createdAt: Date;
}

interface Favorite {
    id: number;
    restaurant_id: number;
}

interface Customer {
    id: number;
    name: string;
    address: string;
    phone: string;
    orders: Order[];
    comments: Comment[];
    favorites: Favorite[];
}

interface CustomerState {
    customer: Customer | null;
    loading: boolean;
    error: string | null;
    fetchUserData: (userId: string) => void;
}

export const useUserStore = create<CustomerState>((set) => ({
    customer: null,
    loading: false,
    error: null,

    fetchUserData: async (customerId: string) => {
        set({ loading: true });
        try {
            // Fetch user data
            const userResponse = await fetch(`/customers/${customerId}`);
            if (!userResponse.ok) {
                throw new Error("Failed to fetch user data");
            }
            const customerData = await userResponse.json();

            // Fetch orders of the user
            const ordersResponse = await fetch(`/customers/${customerId}/orders`);
            if (!ordersResponse.ok) {
                throw new Error("Failed to fetch orders");
            }
            const orders = await ordersResponse.json();

            // Fetch comments of the user
            const commentsResponse = await fetch(`/customers/${customerId}/comments`);
            if (!commentsResponse.ok) {
                throw new Error("Failed to fetch comments");
            }
            const comments = await commentsResponse.json();

            // Fetch favorites of the user
            const favoritesResponse = await fetch(`/customers/${customerId}/favorites`);
            if (!favoritesResponse.ok) {
                throw new Error("Failed to fetch favorites");
            }
            const favorites = await favoritesResponse.json();

            // Merge user data
            set({
                customer: {
                    ...customerData,
                    orders,
                    comments,
                    favorites,
                },
                loading: false,
            });
        } catch (error: unknown) {
            if (error instanceof Error) {
                set({ error: error.message, loading: false });
            }
        }
    },
}));
