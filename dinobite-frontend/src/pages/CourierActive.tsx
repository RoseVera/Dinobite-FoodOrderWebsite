import { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";

type ActiveOrder = {
  orderId: number;
  customerId: number;
  customerName: string;
  restaurantId: number;
  restaurantName: string;
  courierId: number | null;
  courierName: string | null;
  deliveredAt: string | null;
  placeAt: string;
  status: string;
  totalPrice: number;
  restaurantAddress: string | null,
  customerAddress: string | null,
  customerPhone: string | null
};

const CourierActive = () => {
  const { id: courierId } = useParams();
  const [orders, setOrders] = useState<ActiveOrder[]>([]);
  const [modalContent, setModalContent] = useState<string | null>(null);

  const fetchActiveOrders = async () => {
    try {
      const response = await axios.get(
        `http://localhost:9090/api/v1/orders/couriers/${courierId}?status=ON_THE_WAY`
      );

      const mapped = response.data.map((order: any) => ({
        orderId: order.id,
        customerId: order.customerId,
        customerName: order.customerName,
        restaurantId: order.restaurantId,
        restaurantName: order.restaurantName,
        courierId: order.courierId,
        courierName: order.courierName,
        totalPrice: order.totalPrice,
        status: order.status,
        restaurantAddress: order.restaurantAddress,
        customerAddress: order.customerAddress,
        customerPhone: order.customerPhone
      }));

      setOrders(mapped);
    } catch (err) {
      console.error("Active deliveries error", err);
    }
  };

  useEffect(() => {
    fetchActiveOrders();
  }, []);

  const handleStatusChange = async (orderId: number, newStatus: string) => {
    try {
      const order = orders.find((req) => req.orderId === orderId);
      if (!order) return;

      await axios.put(`http://localhost:9090/api/v1/orders/${orderId}`, {
        customerId: order.customerId,
        restaurantId: order.restaurantId,
        courierId: courierId, // şu anki kurye
        totalPrice: order.totalPrice,
        status: newStatus,
      });

      fetchActiveOrders();
    } catch (err) {
      console.error("Durum güncellenemedi", err);
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-semibold mb-4"></h2>
      <table className="block hidden md:table w-full border border-gray-300">
        <thead>
          <tr>
            <th className="border p-2">Order ID</th>
            <th className="border p-2">Restaurant</th>
            <th className="border p-2">Restaurant Address</th>
            <th className="border p-2">Customer</th>
            <th className="border p-2">Customer Phone</th>
            <th className="border p-2">Customer Address</th>
            <th className="border p-2">Action</th>
          </tr>
        </thead>
        <tbody>
          {orders.length === 0 ? (
            <tr>
              <td colSpan={7} className="text-center p-4">
                No active deliveries.
              </td>
            </tr>
          ) : (
            orders.map((order) => (
              <tr key={order.orderId}>
                <td className="border p-2">{order.orderId}</td>
                <td className="border p-2">{order.restaurantName}</td>
                <td className="border p-2">
                  <button
                    className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                    onClick={() => setModalContent(order.restaurantAddress)}
                  >
                    View
                  </button>
                </td>
                <td className="border p-2">{order.customerName}</td>
                <td className="border p-2">{order.customerPhone}</td>
                <td className="border p-2">
                  <button
                    className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                    onClick={() => setModalContent(order.customerAddress)}
                  >
                    View
                  </button>
                </td>
                <td className="border p-2">
                  <div className="flex items-center space-x-2">

                    <select
                      className="border rounded px-2 py-1"
                      onChange={(e) =>
                        handleStatusChange(order.orderId, e.target.value)
                      }
                      value={order.status}
                    >
                      <option value="ON_THE_WAY">On the Way</option>
                      <option value="DELIVERED">Delivered</option>
                      <option value="CANCELLED">Cancelled</option>
                    </select>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>


      {/* Mobil*/}
      <div className="md:hidden space-y-4">
        {orders.length === 0 ? (
          <p className="text-center">No active deliveries.</p>
        ) : (
          orders.map((order) => (
            <div key={order.orderId} className="border rounded-xl p-4 shadow bg-white">
              <p><strong>Order ID:</strong> {order.orderId}</p>
              <p><strong>Restaurant:</strong> {order.restaurantName}</p>
              <p>
                <strong>Restaurant Address:</strong>{" "}
                <button
                  className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                  onClick={() => setModalContent(order.restaurantAddress)}
                >
                  View
                </button>
              </p>
              <p><strong>Customer:</strong> {order.customerName}</p>
              <p><strong>Phone:</strong> {order.customerPhone}</p>
              <p>
                <strong>Customer Address:</strong>{" "}
                <button
                  className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                  onClick={() => setModalContent(order.customerAddress)}
                >
                  View
                </button>
              </p>
              <div className="mt-2">
                <select
                  className="border rounded px-2 py-1 w-full"
                  onChange={(e) => handleStatusChange(order.orderId, e.target.value)}
                  value={order.status}
                >
                  <option value="ON_THE_WAY">On the Way</option>
                  <option value="DELIVERED">Delivered</option>
                  <option value="CANCELLED">Cancelled</option>
                </select>
              </div>
            </div>
          ))
        )}
      </div>


      {modalContent && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="absolute inset-0 bg-black opacity-40"></div>
          {/* Modal */}
          <div className="relative bg-white p-6 rounded-2xl shadow-2xl w-full max-w-md border border-gray-300 z-10">
            <h3 className="text-lg font-bold mb-4">Address</h3>
            <p className="mb-4">{modalContent}</p>
            <div className="flex justify-end">
              <button
                onClick={() => setModalContent(null)}
                className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default CourierActive;
