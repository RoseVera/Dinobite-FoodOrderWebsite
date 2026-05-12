import { create } from 'zustand';
import axios from 'axios';

export type Order = {
  id: number;
  customerId: number;
  customerName: string;
  restaurantId: number;
  restaurantName: string;
  courierId?: number | null;
  courierName?: string | null;
  orderNote: string;
  status: 'PENDING' | 'PREPARING' | 'READY_FOR_PICKUP' | 'ON_THE_WAY' | 'CANCELLED' | 'DELIVERED';
  totalPrice: number;
  placeAt: string;
  deliveredAt: string;
  courierRate: number | null;
  restaurantRate: number | null;
  comments?: Comment[];
  orderItems?: OrderItem[]; 
};

type Comment = {
  id: number;
  commentText: string;
  createdAt: string;
};

type OrderItem = {
  id: number;
  foodId: number;
  foodName: string;
  quantity: number;
  price: number;
  foodImage: string
};

type OrderStore = {
  orders: Order[];
  fetchOrdersByCustomer: (customerId: number) => Promise<void>;
  fetchOrdersByRestaurant: (restaurantId: number) => Promise<void>;
  updateOrderStatus: (
    orderId: number,
    status: 'PENDING' | 'PREPARING' | 'READY_FOR_PICKUP' | 'ON_THE_WAY' | 'DELIVERED' | 'CANCELLED',
    courierId?: number
  ) => Promise<void>;
  fetchCommentsForOrder: (orderId: number) => Promise<Comment[]>;
  addCommentToOrder: (orderId: number, text: string, courierRating: number, restaurantRating: number) => Promise<void>;
  addCommentToOrderRestaurant: (orderId: number, text: string) => Promise<void>;
  fetchOrderItemsForOrder: (orderId: number) => Promise<OrderItem[]>;
  deleteOrderWithItemsAndComments: (order: Order) => Promise<void>;
  deleteAllCommentsForOrder: (orderId: number) => Promise<void>;
};

export const useOrderStore = create<OrderStore>((set, get) => ({
  orders: [],

  fetchOrdersByCustomer: async (customerId: number) => {
    try {
      const res = await axios.get<Order[]>(`http://localhost:9090/api/v1/orders/customers/${customerId}`);
      const ordersWithDetails = await Promise.all(
        res.data.map(async (order) => {
          const [comments, orderItems] = await Promise.all([
            get().fetchCommentsForOrder(order.id),
            get().fetchOrderItemsForOrder(order.id), // Yeni fonksiyon
          ]);
          return { ...order, comments, orderItems };
        })
      );
  
      const sortedOrders = ordersWithDetails.sort(
        (a, b) =>
          new Date(a.placeAt.replace(" ", "T")).getTime() -
          new Date(b.placeAt.replace(" ", "T")).getTime()
      );
  
      set({ orders: sortedOrders });
    } catch (err) {
      console.error("Failed to fetch customer orders", err);
    }
  },

  fetchOrdersByRestaurant: async (restaurantId: number) => {
    try {
      const res = await axios.get<Order[]>(`http://localhost:9090/api/v1/orders/restaurants/${restaurantId}`);
  
      const ordersWithDetails = await Promise.all(
        res.data.map(async (order) => {
          const [comments, orderItems] = await Promise.all([
            get().fetchCommentsForOrder(order.id),
            get().fetchOrderItemsForOrder(order.id), // Yeni fonksiyon
          ]);
          return { ...order, comments, orderItems };
        })
      );
  
      const sortedOrders = ordersWithDetails.sort(
        (a, b) =>
          new Date(a.placeAt.replace(" ", "T")).getTime() -
          new Date(b.placeAt.replace(" ", "T")).getTime()
      );
  
      set({ orders: sortedOrders });
    } catch (err) {
      console.error("Failed to fetch restaurant orders", err);
    }
  },

  updateOrderStatus: async (
    orderId: number,
    status: 'PENDING' | 'PREPARING' | 'READY_FOR_PICKUP' | 'ON_THE_WAY' | 'DELIVERED' | 'CANCELLED',
    courierId?: number
  ) => {
    try {
      const order = get().orders.find(order => order.id === orderId);
      if (!order) throw new Error('Order not found');
  
      const updatedOrder = {
        id: order.id,
        customerId: order.customerId,
        restaurantId: order.restaurantId,
        courierId: courierId ?? order.courierId, // ya mevcut courierId, ya da yeni
        totalPrice: order.totalPrice,
        status: status,
      };
  
      const response = await axios.put(
        `http://localhost:9090/api/v1/orders/${orderId}`,
        updatedOrder // PUT body
      );
  
      const currentOrders = get().orders.map(o =>
        o.id === orderId ? { ...o, status, courierId: updatedOrder.courierId } : o
      );
      set({ orders: currentOrders });
  
      return response.data;
    } catch (error) {
      console.error('Failed to update order status with PUT', error);
      throw error;
    }
  },

  fetchCommentsForOrder: async (orderId: number) => {  // Move fetchCommentsForOrder inside the store
    try {
      const res = await axios.get<Comment[]>(`http://localhost:9090/api/v1/orders/${orderId}/comments`);
      return res.data;
    } catch (err) {
      console.error(`Failed to fetch comments for order ${orderId}`, err);
      return []; // Return an empty array on error
    }
  },

  addCommentToOrder: async (orderId: number, text: string, courierRating: number, restaurantRating: number) => {
    try {
      // Önce mevcut order'ı bul
      const currentOrder = get().orders.find(order => order.id === orderId);
      if (!currentOrder) {
        throw new Error('Order not found');
      }
  
      // Önce yeni yorumu oluştur
      const res = await axios.post<Comment>(
        `http://localhost:9090/api/v1/orders/${orderId}/comments`,
        { 
          commentText: text, 
          orderId: orderId,
        }
      );
  
      // Sonra order'ı güncelle - tüm gerekli alanları gönder
      const orderUpdateRes = await axios.put(
        `http://localhost:9090/api/v1/orders/${orderId}`,
        {
          id: currentOrder.id,
          customerId: currentOrder.customerId,
          restaurantId: currentOrder.restaurantId,
          courierId: currentOrder.courierId,
          totalPrice: currentOrder.totalPrice,
          status: currentOrder.status,
          courierRate: courierRating,
          restaurantRate: restaurantRating,
        }
      );
  
      // State'i güncelle
      set((state) => {
        const updatedOrders = state.orders.map(order => {
          if (order.id === orderId) {
            const updatedComments = [...(order.comments || []), res.data];
            return {
              ...order,
              comments: updatedComments,
              courierRate: courierRating,
              restaurantRate: restaurantRating
            };
          }
          return order;
        });
  
        return { orders: updatedOrders };
      });
  
    } catch (err: any) {
      console.error('Error in addCommentToOrder:', {
        error: err,
        response: err.response?.data,
        status: err.response?.status
      });
      throw err;
    }
  },
  addCommentToOrderRestaurant: async (orderId: number, text: string) => {
    try {
      const res = await axios.post<Comment>(
        `http://localhost:9090/api/v1/orders/${orderId}/comments`,
        { commentText: text, orderId: orderId}
      );
  
      set((state) => {
        const updatedOrders = state.orders.map(order => {
          if (order.id === orderId) {
            return {
              ...order,
              comments: [...(order.comments || []), res.data],
            };
          }
          return order;
        });
  
        return { orders: updatedOrders };
      });
    } catch (err) {
      console.error(`Failed to add comment to order ${orderId}`, err);
      throw err;
    }
  },

  fetchOrderItemsForOrder: async (orderId: number) => {
    try {
      const res = await axios.get<OrderItem[]>(`http://localhost:9090/api/v1/orders/${orderId}/items`);
      return res.data;
    } catch (err) {
      console.error(`Failed to fetch order items for order ${orderId}`, err);
      return [];
    }
  },
  
  deleteOrderWithItemsAndComments: async (order: Order) => {
    try {
      const orderId = order.id;

      await axios.delete(`http://localhost:9090/api/v1/orders/${orderId}`);
  
      set((state) => ({
        orders: state.orders.filter((o) => o.id !== orderId),
      }));
    } catch (error) {
      console.error("Sipariş silinirken hata:", error);
      alert("Sipariş silinemedi.");
    }
  },

  deleteAllCommentsForOrder: async (orderId: number) => {
    try {
      const order = get().orders.find(order => order.id === orderId);
      if (!order) throw new Error('Order not found');
  
      // Delete all comments for this order
      if (order.comments && order.comments.length > 0) {
        await Promise.all(
          order.comments.map((comment) =>
            axios.delete(`http://localhost:9090/api/v1/orders/${orderId}/comments/${comment.id}`)
          )
        );
      }
  
      // Reset ratings by updating the order
      await axios.put(`http://localhost:9090/api/v1/orders/${orderId}`, {
        id: order.id,
        customerId: order.customerId,
        restaurantId: order.restaurantId,
        courierId: order.courierId,
        totalPrice: order.totalPrice,
        status: order.status,
        courierRate: null,
        restaurantRate: null
      });
  
      // Update the local state to remove comments and reset ratings
      set((state) => ({
        orders: state.orders.map((o) =>
          o.id === orderId ? { ...o, comments: [], courierRate: null, restaurantRate: null } : o
        ),
      }));
    } catch (error) {
      console.error("Failed to delete comments and ratings:", error);
      throw error;
    }
  },
  

}));
