import { create } from 'zustand';
import { persist } from 'zustand/middleware';

type CartItem = {
    id: number;
    name: string;
    price: number;
    image: string;
    quantity: number;
    restaurantId: number;
    carbs: number;
}
type MenuItem = {
    id: number;
    name: string;
    description: string;
    price: number;
    image: string;
    protein: number;
    fats: number;
    sugar: number;
    carbs: number;
    availability: boolean;
};
type CartState = {
    items: CartItem[];
    addToCart: (item: MenuItem, restaurantId: number) => void;
    removeFromCart: (id: number, restaurantId: number) => void;
    clearCart: () => void;
};

const useCartStore = create<CartState>()(
    persist(
        (set, get) => ({
            items: [],
            addToCart: (item: MenuItem, restaurantId: number) => {
                const currentItems = get().items;
                const existingItem = currentItems.find(
                    (i) => i.id === item.id && i.restaurantId === restaurantId
                );

                if (existingItem) {
                    set({
                        items: currentItems.map((i) =>
                            i.id === item.id && i.restaurantId === restaurantId
                                ? { ...i, quantity: i.quantity + 1 }
                                : i
                        ),
                    });
                } else {
                    const newCartItem: CartItem = {
                        id: item.id,
                        name: item.name,
                        price: item.price,
                        image: item.image,
                        restaurantId,
                        quantity: 1,
                        carbs: item.carbs, 
                    };

                    set({
                        items: [...currentItems, newCartItem],
                    });
                }
            },
            removeFromCart: (id, restaurantId) => {
                const updated = get().items
                    .map((item) =>
                        item.id === id && item.restaurantId === restaurantId
                            ? { ...item, quantity: item.quantity - 1 }
                            : item
                    )
                    .filter((item) => item.quantity > 0);
                set({ items: updated });
            },
            clearCart: () => {
                set({ items: [] });
              }
        }),
        {
            name: 'cart-storage', // localStorage key
        }
    )
);

export default useCartStore;
