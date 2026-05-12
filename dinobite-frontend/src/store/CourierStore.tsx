import { create } from "zustand";
import axios from 'axios';

interface Courier {
  id: number;
  userId: number;
  name: string;
  photo: string;
  birthDate: string;
  averageRating: number | null;
}

interface CourierState {
  couriers: Courier[];
  loading: boolean;
  error: string | null;
  fetchCouriers: () => void;
}

export const useCourierStore = create<CourierState>((set) => ({
  couriers: [],
  loading: false,
  error: null,

  fetchCouriers: async () => {
    set({ loading: true });
    try {
      const response = await axios.get('http://localhost:9090/api/v1/couriers/available');
      const couriersData = response.data;
  
      const couriersWithDetails = await Promise.all(
        couriersData.map(async (courier: any) => {
          // Kullanıcı bilgilerini al
          const userResponse = await axios.get(`http://localhost:9090/api/v1/users/${courier.userId}`);
          const user = userResponse.data;
  
          // Bu kurye için yapılan siparişleri al
          const ordersResponse = await axios.get(`http://localhost:9090/api/v1/orders/couriers/${courier.id}`);
          const orders = ordersResponse.data;
  
          // Rating hesapla
          const ratedOrders = orders.filter((order: any) => typeof order.courierRate === 'number');
          const totalRating = ratedOrders.reduce((sum: number, order: any) => sum + order.courierRate, 0);
          const averageRating =
            ratedOrders.length > 0 ? parseFloat((totalRating / ratedOrders.length).toFixed(2)) : null;
  
          return {
            id: courier.id,
            userId: courier.userId,
            name: user.name,
            photo: user.photo,
            birthDate: user.birthDate,
            averageRating,
          };
        })
      );
  
      set({ couriers: couriersWithDetails, loading: false });
    } catch (error: unknown) {
      if (error instanceof Error) {
        set({ error: error.message, loading: false });
      } else {
        set({ error: 'Bilinmeyen bir hata oluştu', loading: false });
      }
    }
  }
  
}));
