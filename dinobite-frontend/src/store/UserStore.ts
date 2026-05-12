import { create } from 'zustand';
import { persist } from 'zustand/middleware';

type User = {
  id: number;
  name: string;
  userType: string;
  mail: string;
  customerId?: number;
  courierId?: number;
  restaurantId?: number;
};

type UserStore = {
  user: User | null;
  setUser: (user: User | null) => void;
  clearUser: () => void;
};

export const useUserStore = create<UserStore>()(
  persist(
    (set) => ({
      user: null,
      setUser: (user) => set({ user }),
      clearUser: () => set({ user: null }),
    }),
    {
      name: 'user-store', 
      partialize: (state) => ({ user: state.user }), 
    }
  )
);
