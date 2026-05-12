import { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";

type DeliveryRequest = {
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

const CourierRequests = () => {
  const { id: courierId } = useParams();

  const [requests, setRequests] = useState<DeliveryRequest[]>([]);
  const [modalContent, setModalContent] = useState<string | null>(null);

  const fetchRequests = async () => {
    try {
      const response = await axios.get(`http://localhost:9090/api/v1/orders/couriers/${courierId}?status=READY_FOR_PICKUP`);
      console.log("response data", response.data);

      const mappedData = response.data.map((order: any) => ({
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

      setRequests(mappedData);
    } catch (err) {
      console.error("Del Requests error", err);
    }
  };
  useEffect(() => {
    fetchRequests();
  }, []);

  const handleAccept = async (orderId: number) => {
    try {
      const order = requests.find((req) => req.orderId === orderId);
      if (!order) return;

      await axios.put(`http://localhost:9090/api/v1/orders/${orderId}`, {
        customerId: order.customerId,
        restaurantId: order.restaurantId,
        courierId: courierId, 
        totalPrice: order.totalPrice,
        status: "ON_THE_WAY",
      });

      fetchRequests();
    } catch (err) {
      console.error("Kabul edilirken hata", err);
    }
  };

  const handleReject = async (orderId: number) => {
    try {
      const order = requests.find((req) => req.orderId === orderId);
      if (!order) return;

      await axios.put(`http://localhost:9090/api/v1/orders/${orderId}`, {
        customerId: order.customerId,
        restaurantId: order.restaurantId,
        courierId: null, 
        totalPrice: order.totalPrice,
        status: "PREPARING", 
      });

      fetchRequests();
    } catch (err) {
      console.error("Reject error", err);
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
          {requests.length === 0 ? (
            <tr>
              <td colSpan={4} className="text-center p-4">No delivery request.</td>
            </tr>
          ) : (
            requests.map((req) => (
              <tr key={req.orderId}>
                <td className="border p-2">{req.orderId}</td>
                <td className="border p-2">{req.restaurantName}</td>
                <td className="border p-2">
                  <button
                    className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                    onClick={() => setModalContent(req.restaurantAddress)}
                  >
                    View
                  </button>
                </td>                
                <td className="border p-2">{req.customerName}</td>
                <td className="border p-2">{req.customerPhone}</td>
                <td className="border p-2">
                  <button
                    className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                    onClick={() => setModalContent(req.customerAddress)}
                  >
                    View
                  </button>
                </td>
                <td className="border p-2 space-x-2">
                  <button
                    className="bg-[#00b2a9] text-white px-3 py-1 rounded hover:bg-[#646cffaa] hover:shadow-md transition"
                    onClick={() => handleAccept(req.orderId)}
                  >
                    Approve
                  </button>
                  <button
                    className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 hover:shadow-md transition"
                    onClick={() => handleReject(req.orderId)}
                  >
                    Reject
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {/* Mobil  */}
      <div className="md:hidden space-y-4">
        {requests.length === 0 ? (
          <p className="text-center">No delivery request.</p>
        ) : (
          requests.map((req) => (
            <div key={req.orderId} className="border rounded-xl p-4 shadow bg-white">
              <p><span className="font-semibold">Order ID:</span> {req.orderId}</p>
              <p><span className="font-semibold">Restaurant:</span> {req.restaurantName}</p>
              <p>
                <span className="font-semibold">Restaurant Address:</span>
                <button
                  className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                  onClick={() => setModalContent(req.restaurantAddress)}
                >
                  View
                </button>
              </p>
              <p><span className="font-semibold">Customer:</span> {req.customerName}</p>
              <p><span className="font-semibold">Phone:</span> {req.customerPhone}</p>
              <p>
                <span className="font-semibold">Customer Address:</span>
                <button
                  className="bg-[#00b2a9] text-white px-3 py-1 rounded-lg hover:bg-[#646cffaa] hover:shadow-md transition"
                  onClick={() => setModalContent(req.customerAddress)}
                >
                  View
                </button>
              </p>
              <div className="mt-4 space-x-2">
                <button
                  className="bg-[#00b2a9] text-white px-3 py-1 rounded hover:bg-[#646cffaa] hover:shadow-md transition"
                  onClick={() => handleAccept(req.orderId)}
                >
                  Approve
                </button>
                <button
                  className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 hover:shadow-md transition"
                  onClick={() => handleReject(req.orderId)}
                >
                  Reject
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal */}
      {modalContent && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="absolute inset-0 bg-black opacity-40"></div>

          {/* Modal  */}
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

export default CourierRequests;
