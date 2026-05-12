import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ShoppingCart, User } from "lucide-react";
import { useUserStore } from "@/store/UserStore";
import logo from "../assets/logo2.png";
import like from "../assets/like.png";
import { useNavigate } from 'react-router-dom';
import useCartStore from "@/store/CartStore";
import axios from "axios";
import cartImg from "../assets/cart.png";

type FavoriteRestaurant = {
  id: number;
  restaurantName: string;
};
export const Header: React.FC = () => {
  const navigate = useNavigate();
  const user = useUserStore((state) => state.user);
  const setUser = useUserStore((state) => state.setUser);
  const clearUser = useUserStore((state) => state.clearUser);
  const { items: cart, clearCart, addToCart, removeFromCart } = useCartStore();
  const [favoriteRestaurants, setFavoriteRestaurants] = useState<FavoriteRestaurant[]>([]);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    const roleToEndpointMap: Record<string, string> = {
      CUSTOMER: "customers",
      COURIER: "couriers",
      RESTAURANT: "restaurants",
    };
    
    const fetchUser = async () => {
      try {
        const response = await fetch("http://localhost:9090/api/auth/me", {
          credentials: "include",
        });
    
        if (!response.ok) {
          clearUser();
          return;
        }
    
        const data = await response.json();
        const basicUser = {
          id: data.userId,
          name: data.userName,
          userType: data.userType,
          mail: data.mail,
        };
    
        const roleKey = basicUser.userType;
        const endpoint = roleToEndpointMap[roleKey];
        if (endpoint) {
          const res = await axios.get(`http://localhost:9090/api/v1/${endpoint}/users/${basicUser.id}`);
          const roleIdKey = `${roleKey.toLowerCase()}Id`;
          setUser({
            ...basicUser,
            [roleIdKey]: res.data.id,
          });
        }
      } catch (error) {
        console.error("Fetch user failed:", error);
        clearUser();
      }
    };
    
    const fetchFavorites = async () => {
      try {
        if (user?.customerId) {
          const res = await axios.get(
            `http://localhost:9090/api/v1/customers/${user.customerId}/favorites`
          );
          setFavoriteRestaurants(res.data);
        }
      } catch (error) {
        console.error("Favori restoranlar alınırken hata:", error);
      }
    };

    fetchFavorites();

    if (!user) {
      fetchUser();
    }
  }, [user, setUser, clearUser]);

  const handleLogout = async () => {
    try {
      await fetch('http://localhost:9090/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });
      clearUser();
      clearCart();
      navigate('/login');
    } catch (error) {
      console.error("Logout failed", error);
    }
  };
  const handleLogoClick = () => {
    if (!user || user.userType === "CUSTOMER") {
      navigate("/");
    } else if (user.userType === "ADMIN") {
      navigate("/admin-dashboard");
    }
  };
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

    const totalPrice = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const restaurantId = uniqueRestaurantIds[0];

    let orderId: number | null = null;

    try {
      const orderRes = await axios.post("http://localhost:9090/api/v1/orders", {
        customerId: user?.customerId,
        restaurantId,
        totalPrice,
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

      setErrorMessage("To order, you must be logged in as a customer.");
    }
  };

  return (
    <header className="flex flex-col md:flex-row justify-between items-center px-20 py-3 bg-orange-600 shadow-md">
      <div className="flex items-center space-x-2 cursor-pointer" onClick={handleLogoClick}>
        <img src={logo} alt="Logo" className="w-70 h-AUTO object-cover" />
      </div>

      <div className="flex items-center space-x-6 text-gray-800">
        {user ? (
          <>
            {user.userType === "ADMIN" && (
              <Link to="/admin-dashboard">
                <span className="text-sm font-semibold text-red-600 bg-red-100 px-3 py-1 rounded-full hover:bg-red-200 transition">
                  Admin Page
                </span>
              </Link>
            )}

            {user?.userType === "CUSTOMER" && (
              <>
                <div className="relative group">
                  <div className="flex items-center text-white text-sm cursor-pointer">
                    <img src={like} className="w-5 h-5 mr-1" alt="like" /> Favorites
                  </div>
                  <div className="absolute right-0 w-56 bg-white shadow-lg rounded-md z-50 opacity-0 group-hover:opacity-100 pointer-events-none group-hover:pointer-events-auto transition-opacity duration-200">
                    {favoriteRestaurants.length > 0 ? (
                      favoriteRestaurants.map((restaurant) => (
                        <Link
                          key={restaurant.id}
                          to={`/restaurants/${restaurant.id}`}
                          className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                        >
                          {restaurant.restaurantName}
                        </Link>
                      ))
                    ) : (
                      <div className="px-4 py-2 text-sm text-gray-500">No favorites</div>
                    )}
                  </div>
                </div>


                <div className="relative group ml-4">
                  <div className="flex items-center text-white text-sm cursor-pointer">
                    <ShoppingCart className="w-6 h-6 text-white" />
                  </div>
                  <div className="absolute right-0  w-72 bg-white shadow-lg rounded-md z-50 hidden group-hover:block">
                    <div className="p-4 max-h-64 overflow-y-auto">
                      {cart.length > 0 ? (
                        cart.map((item) => (
                          <div key={item.id} className="flex justify-between items-center mb-2">
                            <span className="text-sm text-gray-800">{item.name}</span>
                            <span className="text-sm font-medium text-gray-600">x{item.quantity}</span>
                          </div>
                        ))
                      ) : (
                        <div className="text-sm text-gray-500 text-center"><img src={cartImg} className="w-50 h-50"></img> Cart Is Empty</div>
                      )}
                    </div>
                    {cart.length > 0 && (
                      <div className="border-t px-4 py-2">
                        <button
                          onClick={handleCheckout}
                          className="w-full bg-teal-600 text-white py-2 rounded-md hover:bg-teal-700 transition text-sm"
                        >
                          Proceed to Checkout
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              </>
            )}

            {user?.userType === "COURIER" && (
              <>
                <Link to={`/courier-profile/${user.courierId}`} className="flex items-center text-sm text-white hover:text-gray-100 transition">
                  <User className="w-6 h-6 mr-1 text-white" />
                  {user.name}
                </Link>

              </>
            )}

            {user?.userType === "RESTAURANT" && (
              <>
                <Link to="/restaurant-dashboard" className="flex items-center text-sm text-white hover:text-gray-100 transition">
                  <User className="w-6 h-6 mr-1 text-white" />
                  {user.name}
                </Link>

              </>
            )}
            {user?.userType === "CUSTOMER" && (
              <>
                <Link to="/profile" className="flex items-center text-sm text-white hover:text-gray-100 transition">
                  <User className="w-6 h-6 mr-1 text-white" />
                  {user.name}
                </Link>

              </>
            )}
            
            <button
              onClick={handleLogout}
              className="text-sm hover:text-white-100 text-white transition"
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="text-sm text-white hover:text-gray-100 transition">
              Login
            </Link>
            <Link to="/register" className="text-sm text-white hover:text-gray-100 transition">
              Register
            </Link>
          </>
        )}
      </div>

    </header>
  );
};

