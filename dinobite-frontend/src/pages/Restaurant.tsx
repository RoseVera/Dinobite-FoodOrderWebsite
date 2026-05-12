import { useParams } from "react-router-dom";
import useCartStore from "@/store/CartStore";
import { useEffect, useState } from 'react';
import axios from 'axios';
import cartImg from "../assets/cart.png";
import { useUserStore } from '@/store/UserStore';
import { LoadingPage } from '../components/Loading';
import { ComingSoon } from '../components/ComingSoon';
import { useOrderStore } from '../store/OrderStore';

// --- Types ---
type Category = {
  id: number;
  name: string;
  items: Item[];
};
type Coupon = {
  id: number;
  discountPercent: number;
  code: string;
  expirationDate: string;
};
type Item = {
  id: number;
  name: string;
  categoryId: number;
  price: number;
  description: string;
  carbs: number;
  fats: number;
  sugar: number;
  protein: number;
  availability: boolean;
  image: string;
};

interface Restaurant {
  id: number;
  name: string;
  hours: string;
  address: string;
  cuisine: string;
  phone: string;
  categories: Category[];
  logo: string;
}
export default function RestaurantDetail() {
  const { id } = useParams<{ id: string }>();
  const user = useUserStore((state) => state.user);
  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [activeTab, setActiveTab] = useState<"Menu" | "Reviews">("Menu");
  const { orders, fetchOrdersByRestaurant } = useOrderStore();
  const [isFavorite, setIsFavorite] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const { items: cart, clearCart, addToCart, removeFromCart } = useCartStore();
  const [expandedOrders, setExpandedOrders] = useState<{ [key: number]: boolean }>({});
  const [coupons, setCoupons] = useState<Coupon[]>([]);
  const [selectedCouponId, setSelectedCouponId] = useState<number | null>(null);
  const [discountPercentage, setDiscountPercentage] = useState(0);
   
  useEffect(() => {
    const fetchCoupons = async () => {
      try {
        const res = await axios.get(`http://localhost:9090/api/v1/customers/${user?.customerId}/coupons/active`);
        setCoupons(res.data);
      } catch (error) {
        console.error("Failed to fetch coupons", error);
      }
    };

    if (user?.customerId) fetchCoupons();
  }, [user?.customerId]);

  const toggleExpand = (orderId: number) => {
    setExpandedOrders((prev) => ({ ...prev, [orderId]: !prev[orderId] }));
  };

  useEffect(() => {
    const fetchRestaurant = async () => {
      try {
        const response = await axios.get(`http://localhost:9090/api/v1/restaurants/${id}`);
        const data = response.data;
        const categoriesWithItems = data.categories.map((category: any) => ({
          ...category,
          items: data.foods.filter((item: any) => item.categoryId === category.id),
        }));
        setRestaurant({
          ...data,
          categories: categoriesWithItems,
        });

        setLoading(false);
      } catch (error) {
        setError('Failed to fetch restaurant data');
        setLoading(false);
      }
    };

    if (id) {
      fetchRestaurant();
    }
    fetchOrdersByRestaurant(Number(id));
  }, [id]);
  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");  
      }, 3000); 
  
      return () => clearTimeout(timer); 
    }
  }, [successMessage]);
  useEffect(() => {
    if(!user || !user?.customerId) return;
    const checkIfFavorite = async () => {
      try {
        const res = await axios.get(`http://localhost:9090/api/v1/customers/${user?.customerId}/favorites`);
        const favoriteRestaurants = res.data;
        const isAlreadyFavorite = favoriteRestaurants.some(
          (fav: any) => fav.restaurantId === Number(id)
        );
        setIsFavorite(isAlreadyFavorite);
      } catch (error) {
        console.error("Failed to fetch favorites", error);
      }
    };
  
    
      checkIfFavorite();
    
  }, [user, id]);

  const handleCheckout = async () => {
    if (cart.length === 0) {
      setErrorMessage("Your cart is empty.");
      return;
    }
  
    const uniqueRestaurantIds = Array.from(new Set(cart.map((item) => item.restaurantId)));
    if (uniqueRestaurantIds.length > 1) {
      setErrorMessage("You can only order from one restaurant at a time.");
      return;
    }
  
    const hasInvalidQuantity = cart.some(item => item.quantity > 100);
    if (hasInvalidQuantity) {
      setErrorMessage("Item quantity cannot exceed 100.");
      return;
    }
    if (selectedCouponId) {
      await axios.patch(`http://localhost:9090/api/v1/customers/${user?.customerId}/coupons/${selectedCouponId}/deactivate`);
    }
  
    const totalPrice = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const discount = totalPrice * (discountPercentage / 100);
    const finalPrice = totalPrice - discount;
    const restaurantId = uniqueRestaurantIds[0];
  
    let orderId: number | null = null;
  
    try {
      const orderRes = await axios.post("http://localhost:9090/api/v1/orders", {
        customerId: user?.customerId,
        restaurantId,
        totalPrice: finalPrice,
        status: "PENDING",
      });
  
      orderId = orderRes.data.id;
  
      const orderItems = cart.map((item) => ({
        orderId,
        foodId: item.id,
        quantity: item.quantity,
        price: item.price,
        orderNote: "note",
      }));
  
      await Promise.all(
        orderItems.map((item) =>
          axios.post(`http://localhost:9090/api/v1/orders/${orderId}/items`, item)
        )
      );
  
      setErrorMessage("");
      clearCart();
      setSuccessMessage("Order placed successfully!");
    } catch (error) {
      console.error("Checkout error", error);
  
      if (orderId !== null) {
        try {
          await axios.delete(`http://localhost:9090/api/v1/orders/${orderId}`);
        } catch (deleteError) {
          console.error("Failed to delete order after error", deleteError);
        }
      }
  
      setErrorMessage("To order, you must be logged in.");
    }
  };
  const handleAddFavorite = async () => {
    try {
      await axios.post(`http://localhost:9090/api/v1/customers/${user?.customerId}/favorites`, {
        customerId: user?.customerId,
        restaurantId: id,
      });
      setIsFavorite(true);
    } catch (error) {
      console.error("Failed to add favorite:", error);
      setErrorMessage("This Restaurant Is Already In Your Favorites List.");
    }
  };
  
  

  if (loading) return <div className="text-center text-2xl">
    <LoadingPage />
  </div>
  if (!restaurant) return (
    <ComingSoon />
  );
  const ratings = orders.flatMap(order => [
    order.courierRate,
    order.restaurantRate,
  ]).filter(r => typeof r === 'number') as number[];
  const subtotal = cart.reduce((total, item) => total + item.price * item.quantity, 0);
  const discountAmount = subtotal * (discountPercentage / 100);
  const totalPrice = subtotal - discountAmount;
  
  const averageRating = ratings.length > 0
    ? (ratings.reduce((a, b) => a + b, 0) / ratings.length).toFixed(1)
    : "Yok";


  return (
    <div className="p-10 space-y-10 bg-gray-50">
      <div className="flex flex-col lg:flex-row justify-between bg-white p-6 rounded-2xl shadow-md space-y-6 lg:space-y-0 lg:space-x-8">
        {/* Sol taraf: Resim ve bilgiler */}
        <div className="flex flex-col md:flex-row gap-6">
          <img
            src={restaurant.logo}
            alt={restaurant.name}
            className="w-full md:w-64 h-64 object-cover rounded-2xl border"
          />
          <div className="space-y-3">
            <h1 className="text-4xl font-extrabold text-gray-800">{restaurant.name}</h1>
            <p className="text-gray-600"><span className="font-semibold">📍 Address:</span> {restaurant.address}</p>
            <p className="text-gray-600"><span className="font-semibold">🍽️ Cuisine:</span> {restaurant.cuisine}</p>
            <p className="text-gray-600"><span className="font-semibold">📞 Phone Number:</span> {restaurant.phone}</p>
            <p className="text-gray-600"><span className="font-semibold">🕒 Working Hours:</span> {restaurant.hours}</p>
          </div>
        </div>

        {/* Sağ taraf: Puan ve buton */}
        <div className="flex flex-col items-start lg:items-end space-y-3">
          <div className="bg-gray-50 border border-gray-200 rounded-xl px-6 py-4 shadow-sm text-center">
            <p className="text-sm text-gray-500">Ortalama Puan</p>
            <p className="text-3xl font-bold text-orange-600">{averageRating} ⭐</p>
          </div>
          <button
            onClick={handleAddFavorite}
            className="bg-orange-600 hover:bg-orange-700 text-white px-6 py-2 rounded-full shadow"
          >
            {isFavorite ? "❤️ Favorilerde" : "➕ Favorilere Ekle"}
          </button>
          {errorMessage && (
            <p className="text-red-500 text-sm">{errorMessage}</p>
          )}
        </div>
      </div>


      <div className="p-5 bg-white rounded-xl shadow-md">
        <div className="flex border-b border-gray-300 mb-10">
          <button
            onClick={() => setActiveTab("Menu")}
            className={`px-4 py-2 font-medium ${
              activeTab === "Menu"
                ? "border-b-2 border-teal-600 text-teal-600"
                : "text-gray-500"
            }`}
          >
            Menu
          </button>
          <button
            onClick={() => setActiveTab("Reviews")}
            className={`px-4 py-2 font-medium ml-4 ${
              activeTab === "Reviews"
                ? "border-b-2 border-teal-600 text-teal-600"
                : "text-gray-500"
            }`}
          >
            Reviews
          </button>
        </div>

        {activeTab === "Menu" && (
          <div className="flex flex-col lg:flex-row gap-6">
          <div className="lg:w-2/3 space-y-6">
            <div className="flex gap-4 overflow-x-auto pb-2">
              {restaurant.categories.map((cat) => (
                <a
                  key={cat.name}
                  href={`#${cat.name}`}
                  className="bg-orange-100 px-3 py-2 rounded-full text-orange-600 font-medium whitespace-nowrap"
                >
                  {cat.name}
                </a>
              ))}
            </div>
  
            {restaurant.categories.map((category) => (
              <div key={category.name} id={category.name} className="space-y-3">
                <h2 className="text-4xl font-bold text-orange-700">{category.name}</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {category.items.map((item) => (
                    <div
                      key={item.id}
                      className="border p-6 rounded-2xl flex flex-col justify-between items-center shadow-lg bg-white transition-transform transform hover:scale-105"
                    >
                      <div className="flex flex-col md:flex-row items-center md:items-start">
                        <img
                          src={item.image}
                          alt={item.name}
                          className="w-60 h-60 rounded-lg md:w-40 md:h-40 mr-6"
                        />
                        <div className="text-center md:text-left">
                          <h3 className="text-2xl font-semibold text-gray-800">{item.name}</h3>
                          <p className="text-lg text-gray-500">₺{item.price}</p>
                          <p className="text-gray-600 text-sm mt-2">{item.description}</p>
                          <div className="flex flex-wrap justify-start space-x-6 mt-4 text-sm">
                            <div className="flex items-center space-x-2">
                              <span className="text-teal-600 font-semibold">Protein:</span>
                              <span className="text-teal-700">{item.protein}g</span>
                            </div>
                            <div className="flex items-center space-x-2">
                              <span className="text-yellow-500 font-semibold">Carb:</span>
                              <span className="text-yellow-600">{item.carbs}g</span>
                            </div>
                            <div className="flex items-center space-x-2">
                              <span className="text-red-600 font-semibold">Fats:</span>
                              <span className="text-red-700">{item.fats}g</span>
                            </div>
                            <div className="flex items-center space-x-2">
                              <span className="text-pink-500 font-semibold">Sugar:</span>
                              <span className="text-pink-600">{item.sugar}g</span>
                            </div>
                          </div>
                        </div>
  
                      </div>
  
                      <div className="mt-5">
                        {item.availability ? (
                          <button
                            onClick={() => addToCart(item, restaurant.id)}
                            className="bg-teal-600 text-white px-4 py-1 rounded-full hover:bg-teal-700 transition-colors"
                          >
                            Add To Cart
                          </button>
                        ) : (
                          <button
                            disabled
                            className="bg-gray-400 text-white px-6 py-2 rounded-full cursor-not-allowed"
                          >
                            Unavailable
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
  
  
            ))}
          </div>
  
          <div className="lg:w-1/3 bg-white p-6 rounded-xl shadow-lg h-fit">
            <h2 className="text-2xl font-bold mb-4 text-teal-600">Your Cart</h2>
            {coupons.length > 0 && (
  <div className="mb-6">
    <label className="block mb-2 text-sm font-semibold text-gray-800">Apply a Coupon</label>
    <div className="relative">
      <select
        className="w-full appearance-none bg-white border border-teal-500 text-gray-800 font-medium rounded-lg p-3 shadow-sm focus:outline-none focus:ring-2 focus:ring-teal-500"
        value={selectedCouponId ?? ""}
        onChange={(e) => {
          const selectedId = parseInt(e.target.value);
          setSelectedCouponId(selectedId);
          const selected = coupons.find(c => c.id === selectedId);
          setDiscountPercentage(selected?.discountPercent || 0);
            }}
          >
            <option value="">No Coupon</option>
            {coupons.map((coupon) => (
              <option key={coupon.id} value={coupon.id}>
                {coupon.code} – %{coupon.discountPercent} Off
              </option>
            ))}
          </select>

          <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3 text-teal-600">
            <svg
              className="h-5 w-5"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
            </svg>
          </div>
        </div>
      </div>
    )}

  
            {cart.length === 0 ? (
              <div className="flex flex-col justify-center items-center text-gray-500">
                <img
                  src={cartImg}
                  alt="Empty Cart"
                  className="w-32 h-32 object-cover mb-4"
                />
                <p>Cart is empty</p>
                {successMessage && (
                    <p className="text-green-500 text-sm mt-2 text-center">{successMessage}</p>
                  )}
              </div>
              
            ) : (
              <div>
                <ul className="space-y-4">
                  {cart.map((item) => (
                    <li key={item.id} className="flex justify-between items-center border-b py-4">
                      <div className="flex items-center space-x-4">
                        <img
                          src={item.image}
                          alt={item.name}
                          className="w-16 h-16 object-cover rounded-lg"
                        />
                        <span className="text-lg font-semibold text-gray-800">{item.name}</span>
                        <span className="text-sm text-gray-500">x{item.quantity}</span>
                      </div>
                      <div className="flex items-center space-x-4">
                        <span className="text-lg font-semibold text-gray-700">
                          ₺{item.price * item.quantity}
                        </span>
                        <button
                          onClick={() => removeFromCart(item.id, item.restaurantId)}
                          className="text-red-500 hover:text-red-700"
                        >
                          Remove
                        </button>
                      </div>
                    </li>
                  ))}
                </ul>
                <div className="mt-6 space-y-2">
                <div className="flex justify-between items-center">
                  <p className="text-sm text-gray-600">Subtotal</p>
                  <p className="text-sm text-gray-600">₺{subtotal.toFixed(2)}</p>
                </div>
                {discountPercentage > 0 && (
                  <div className="flex justify-between items-center">
                    <p className="text-sm text-green-600">Discount ({discountPercentage}%)</p>
                    <p className="text-sm text-green-600">-₺{discountAmount.toFixed(2)}</p>
                  </div>
                )}
                <div className="flex justify-between items-center text-lg font-bold text-gray-800">
                  <p>Total</p>
                  <p className="text-teal-600">₺{totalPrice.toFixed(2)}</p>
                </div>
              </div>
                <div className="mt-4">
                  <button
                    onClick={handleCheckout}
                    className="w-full bg-teal-600 text-white py-2 rounded-lg hover:bg-teal-700 transition-colors"
                  >
                    Proceed to Checkout
                  </button>
                  {errorMessage && (
                    <p className="text-red-500 text-sm mt-2 text-center">{errorMessage}</p>
                  )}
                  
                </div>
              </div>
            )}
          </div>
  
  
        </div>
        )}

        {activeTab === "Reviews" && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {orders
            .filter((order) => (order.status === "DELIVERED" || order.status === "CANCELLED") && order.comments?.length !== 0)
            .map((order) => {
              const comments = order.comments || [];
              const showMore = expandedOrders[order.id];
              const firstComment = comments[0];
              const remainingComments = comments.slice(1);
              const products = order.orderItems || [];
        
              return (
                <div key={order.id} className="border rounded-xl p-5 bg-white shadow-md">
                  {/* Sipariş Başlığı */}
                  <div className="flex items-center justify-between mb-3">
                    <h3 className="text-lg font-semibold text-gray-800">
                      🧾 Order #{order.id}
                    </h3>
                    <span className="text-sm text-gray-500">Status: {order.status}</span>
                  </div>
        
                  {/* Ürün Listesi */}
                  {products.length > 0 && (
                    <div className="mb-4">
                      <p className="text-sm font-medium text-gray-700 mb-1">🛍️ Order Items:</p>
                      <ul className="list-disc list-inside text-sm text-gray-600 space-y-1">
                        {products.map((item: any) => (
                          <li key={item.id}>
                            {item.foodName} × {item.quantity}
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
        
                  {/* Puanlar */}
                  <div className="grid grid-cols-2 gap-4 text-sm text-gray-700 mb-4">
                    <div>
                      <p>⭐ Courier Rate:</p>
                      <p className="font-semibold text-orange-600">{order.courierRate ?? "Yok"}</p>
                    </div>
                    <div>
                      <p>🏪 Restaurant Rate:</p>
                      <p className="font-semibold text-orange-600">{order.restaurantRate ?? "Yok"}</p>
                    </div>
                  </div>
        
                  {/* İlk Yorum */}
                  {firstComment ? (
                    <div
                      className="cursor-pointer border-l-4 border-orange-400 pl-4 py-2 bg-orange-50 rounded-md hover:bg-orange-100"
                      onClick={() => toggleExpand(order.id)}
                    >
                      <p className="text-sm text-gray-800">{firstComment.commentText}</p>
                      <p className="text-xs text-gray-500 mt-1">
                        {new Date(firstComment.createdAt.replace(" ", "T")).toLocaleString("tr-TR", {
                          year: "numeric",
                          month: "long",
                          day: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })}{" "}
                        - {order.customerName}
                      </p>
                    </div>
                  ) : (
                    <p className="text-sm text-gray-500 italic">No comment For This Order.</p>
                  )}
        
                  {/* Ek Yorumlar */}
                  {showMore && remainingComments.length > 0 && (
                    <div className="mt-4 space-y-3">
                      {remainingComments.map((comment, index) => {
                        const globalIndex = index + 1;
                        const isCustomer = globalIndex % 2 === 0;
                        const label = isCustomer
                          ? `👤 Customer (${order.customerName})`
                          : `🏪 Restaurant (${order.restaurantName})`;
        
                        return (
                          <div
                            key={comment.id}
                            className="ml-2 border-l-4 border-gray-300 pl-4 py-2 bg-gray-50 rounded-md"
                          >
                            <p className="text-sm text-gray-800">
                              <span className="font-medium text-teal-600">{label}:</span>{" "}
                              {comment.commentText}
                            </p>
                            <p className="text-xs text-gray-500 mt-1">
                              {new Date(comment.createdAt.replace(" ", "T")).toLocaleString("en-EN", {
                                year: "numeric",
                                month: "long",
                                day: "numeric",
                                hour: "2-digit",
                                minute: "2-digit",
                              })}
                            </p>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            })}
        </div>
        
        
        )}
      </div>


      
    </div>
  );
}
