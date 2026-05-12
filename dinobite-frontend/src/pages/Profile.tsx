import { useEffect, useState } from 'react';
import axios from 'axios';
import { useUserStore } from '../store/UserStore';
import { useOrderStore } from '@/store/OrderStore';
import { Link } from 'react-router-dom';
import { FaHeart } from 'react-icons/fa';
import { FaStar } from 'react-icons/fa';

// Add Popup component
const Popup = ({ message, type, onClose }: { message: string; type: 'success' | 'error'; onClose: () => void }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 3000);

    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={`fixed top-4 right-4 p-4 rounded-xl shadow-xl z-50 transform transition-all duration-300 ${
      type === 'success' ? 'bg-gradient-to-r from-green-50 to-green-100 border-green-400' : 'bg-gradient-to-r from-red-50 to-red-100 border-red-400'
    } border-2 backdrop-blur-sm`}>
      <div className="flex items-center gap-3">
        <span className="text-2xl">
          {type === 'success' ? '🎉' : '😢'}
        </span>
        <p className={`font-medium ${type === 'success' ? 'text-green-700' : 'text-red-700'}`}>
          {message}
        </p>
      </div>
    </div>
  );
};

type FavoriteRestaurant = {
  id: number;
  restaurantId: number;
  restaurantName: string;
  logo?: string;
  cuisine?: string;
  deliveryRange?: number;
  hours?: string;
};

export default function CustomerPage() {
  const { user } = useUserStore();
  const { orders, fetchOrdersByCustomer, addCommentToOrder, deleteAllCommentsForOrder } = useOrderStore();
  const [customer, setCustomer] = useState<any | null>(null);
  const [userData, setUserData] = useState<any | null>(null); 
  const [favoriteRestaurants, setFavoriteRestaurants] = useState<FavoriteRestaurant[]>([]);
  const [editingCustomer, setEditingCustomer] = useState<any | null>(null);
  const [editingUser, setEditingUser] = useState<any | null>(null);
  const [isSaving, setIsSaving] = useState(false); // Kaydetme durumu için state
  const [commentText, setCommentText] = useState<string>('');
  const [courierRating, setCourierRating] = useState<number>(0);
  const [restaurantRating, setRestaurantRating] = useState<number>(0);
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  // Add this helper function
  const renderStars = (rating: number, setRating: (rating: number) => void) => {
    return (
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <div
            key={star}
            className={`cursor-pointer ${
              star <= rating ? 'text-yellow-400' : 'text-gray-300'
            }`}
            onClick={() => setRating(star)}
          >
            <FaStar size={24} />
          </div>
        ))}
      </div>
    );
  };


  // Müşteri bilgilerini getir
  useEffect(() => {
    if (user?.customerId) { //şu an customer sınıf id si ile çağırdık
      axios.get(`http://localhost:9090/api/v1/customers/users/${user.id}`)
        .then(res => {
          const customerData = res.data;
          setCustomer(customerData);
          setEditingCustomer({
            ...customerData,
            name: user.name, 
            mail: user.mail
          });
        });
  
        // Fetch favorite restaurants
        axios.get(`http://localhost:9090/api/v1/customers/${user.customerId}/favorites`)
        .then(res => {
          // For each favorite restaurant, fetch its details
          const favoritePromises = res.data.map((fav: any) => 
            axios.get(`http://localhost:9090/api/v1/restaurants/${fav.restaurantId}`)
              .then(restaurantRes => ({
                ...fav,
                logo: restaurantRes.data.logo,
                cuisine: restaurantRes.data.cuisine,
                deliveryRange: restaurantRes.data.deliveryRange,
                hours: restaurantRes.data.hours
              }))
          );
          
          Promise.all(favoritePromises)
            .then(restaurantsWithDetails => setFavoriteRestaurants(restaurantsWithDetails));
        });
    }
  
    fetchOrdersByCustomer(user?.customerId!);
  }, [user?.customerId]);

// In your user update function:
const handleUserUpdate = async () => {
  if (editingUser) {
    setIsSaving(true);
    try {
      const response = await axios.put(
        `http://localhost:9090/api/v1/users/${user?.id}`,
        {
          name: editingUser.name,
          mail: editingUser.mail,
          userType: user?.userType,
        }
      );
      setUserData(response.data);
      setEditingUser(response.data);
      setPopup({ message: "Yay! Your info is updated! 🎉", type: 'success' });
    } catch (error: any) {
      console.error("Failed to update user:", error);
      setPopup({ 
        message: `Oops! ${error.response?.data?.message || error.message} 😢`, 
        type: 'error' 
      });
    } finally {
      setIsSaving(false);
    }
  }
};

// In your user data fetch useEffect:
useEffect(() => {
  if (user?.id) {
    axios.get(`http://localhost:9090/api/v1/users/${user.id}`)
      .then(res => {
        const userData = res.data;
        setUserData(userData);
        setEditingUser({
          // Make sure these field names match your API response
          name: userData.name || user.name,
          mail: userData.email || userData.mail || user.mail, // Handle different field names
          // Include other user fields
          // password: userData.password || '', // You might need to handle this differently
          userType: userData.userType || user.userType, // Changed from type to userType
        });
      })
      .catch(error => {
        console.error("Failed to fetch user data:", error);
      });
  }
}, [user?.id]);

  
  // Müşteri bilgilerini güncelle
  const handleCustomerUpdate = async () => {
    if (editingCustomer) {
      setIsSaving(true);
      try {
        await axios.put(`http://localhost:9090/api/v1/customers/${user?.customerId}`, editingCustomer);
        setEditingCustomer(editingCustomer);
        setPopup({ message: "Woohoo! Your info is updated! 🎉", type: 'success' });
      } catch (error) {
        setPopup({ message: "Oops! Something went wrong 😢", type: 'error' });
      } finally {
        setIsSaving(false);
      }
    }
  };

  const handleRemoveFavorite = async (restaurantId: number) => {
    try {
      await axios.delete(
        `http://localhost:9090/api/v1/customers/${user?.customerId}/favorites/restaurants/${restaurantId}`
      );
  
      // Favori listesinden çıkar
      setFavoriteRestaurants((prev) =>
        prev.filter((fav) => fav.id !== restaurantId)
      );
      setPopup({ message: "Restaurant removed from favorites! 👋", type: 'success' });
    } catch (error) {
      console.error("Failed to remove favorite", error);
      setPopup({ message: "Oops! Couldn't remove from favorites 😢", type: 'error' });
    }
  };
  
  return (
    <div className="max-w-4xl mx-auto p-8 bg-gradient-to-br from-white to-gray-50 shadow-2xl rounded-2xl">
      {popup && (
        <Popup
          message={popup.message}
          type={popup.type}
          onClose={() => setPopup(null)}
        />
      )}
      <h1 className="text-4xl font-bold text-gray-800 mb-8 text-center bg-gradient-to-r from-teal-600 to-orange-500 bg-clip-text text-transparent">Profile Page</h1>

      {editingCustomer && editingUser && (
        <div className="mb-8">
          <h2 className="text-2xl font-semibold text-gray-700 mb-4">Update Information</h2>
          <div className="space-y-4">
            <h3 className="text-1xl font-semibold text-gray-700 mb-4">Name: </h3>
            <input
              type="text"
              value={editingUser.name}
              onChange={(e) => setEditingUser({ ...editingUser, name: e.target.value })}
              placeholder="Your name pleasee"
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-teal-500"
            />
            <h3 className="text-1xl font-semibold text-gray-700 mb-4">Address: </h3>
            <input
              type="text"
              value={editingCustomer.address}
              onChange={(e) => setEditingCustomer({ ...editingCustomer, address: e.target.value })}
              placeholder="Your address pleasee"
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-teal-500"
            />
            <h3 className="text-1xl font-semibold text-gray-700 mb-4">Mail: </h3>
            <input
              type="email"
              value={editingUser.mail}
              onChange={(e) => setEditingUser({ ...editingUser, mail: e.target.value })}
              placeholder="Your mail pleasee"
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-teal-500"
            />
            <h3 className="text-1xl font-semibold text-gray-700 mb-4">Phone Number: </h3>
            <input
              type="text"
              value={editingCustomer.phone}
              onChange={(e) => setEditingCustomer({ ...editingCustomer, phone: e.target.value })}
              placeholder="Your phone number pleasee"
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-teal-500"
            />
            <h3 className="text-1xl font-semibold text-gray-700 mb-4">Birth Day! </h3>
            <input
              type="text"
              value={editingCustomer.birthDate}
              onChange={(e) => setEditingCustomer({ ...editingCustomer, birthDate: e.target.value })}
              placeholder="Your birth day pleasee"
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-teal-500"
            />
            <button
              onClick={async () => {
                await handleCustomerUpdate();
                await handleUserUpdate();
              }}
              className={`flex items-center justify-center gap-2 w-full py-4 rounded-xl text-white font-medium bg-gradient-to-r from-teal-500 to-teal-600 hover:from-teal-600 hover:to-teal-700 transform hover:scale-[1.02] transition-all duration-200 shadow-lg ${
                isSaving ? 'cursor-wait opacity-75' : 'cursor-pointer'
              }`}
              disabled={isSaving}
            >
              {isSaving ? (
                <>
                  <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Saving...
                </>
              ) : (
                <>
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                  </svg>
                  Save Changes
                </>
              )}
            </button>
          </div>
        </div>
      )}

      {/* 🧾 Siparişler */}
      <div className="mb-8">
  <h2 className="text-2xl font-semibold text-gray-700 mb-4">Orders</h2>
  {orders
    .filter((o) => o.customerId === user?.customerId)
    .map((order) => {
      const comments = order.comments || [];
      const lastCommentByCustomer = comments.length > 0 && comments.length % 2 === 1; // son yorum müşteriye aitse (tek sayıda yorum)

      return (
        <div
          key={order.id}
          className="bg-gray-50 border border-gray-200 p-4 rounded-lg shadow-sm mb-6"
        >
          {/* Order header with number, restaurant and total price */}
          <div className="flex justify-between items-center mb-4">
            <div className="flex items-center gap-4">
              <span className="font-bold">Order #{order.id}</span>
              <span className="text-gray-600">-</span>
              <span className="text-gray-700">Restaurant = {order.restaurantName}</span>
            </div>
            <span className="font-semibold text-gray-800">Total Price = ₺{order.totalPrice}</span>
          </div>
  
          {/* Order items grid */}
          {order.orderItems && order.orderItems.length > 0 && (
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 mb-4">
              {order.orderItems.map((item) => (
                <div key={item.id} className="flex flex-col items-center p-2 bg-white rounded-lg shadow-sm">
                  <img 
                    src={item.foodImage} 
                    alt={item.foodName}
                    className="w-16 h-16 object-cover rounded-full mb-2"
                  />
                  <span className="text-sm font-medium text-gray-700">{item.foodName}</span>
                  <span className="text-xs text-gray-500">x{item.quantity}</span>
                </div>
              ))}
            </div>
          )}
  
          {/* Order status */}
          <div className="mt-2">
            <p>
              Status: <span className="text-orange-600">{order.status}</span>
            </p>
          </div>
              {/* Comments Section */}
              <div className="mt-2 space-y-2">
                {comments.map((c, i) => {
                  const isCustomerComment = i % 2 === 0;
                  return (
                    <div
                      key={i}
                      className={`max-w-[75%] p-3 rounded-lg text-sm shadow-md ${
                        isCustomerComment
                          ? 'bg-gray-100 text-left self-start'
                          : 'bg-orange-100 text-right self-end ml-auto'
                      }`}
                    >
                      {/* Add ratings display for customer comments */}
                      {isCustomerComment && i === 0 && (
                        <div className="mb-2">
                          <div className="flex items-center gap-2 mb-1">
                            <span className="text-xs text-gray-600">Courier Rating:</span>
                            <div className="flex gap-1">
                              {[1, 2, 3, 4, 5].map((star) => (
                                <div key={star} className={star <= (order.courierRate ?? 0) ? 'text-yellow-400' : 'text-gray-300'}>
                                  <FaStar size={16} />
                                </div>
                              ))}
                            </div>
                          </div>
                          <div className="flex items-center gap-2">
                            <span className="text-xs text-gray-600">Restaurant Rating:</span>
                            <div className="flex gap-1">
                              {[1, 2, 3, 4, 5].map((star) => (
                                <div key={star} className={star <= (order.restaurantRate ?? 0) ? 'text-yellow-400' : 'text-gray-300'}>
                                  <FaStar size={16} />
                                </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      )}
                      <p className="text-gray-800">{c.commentText}</p>
                      <p className="text-xs text-gray-500 mt-1">
                        {isCustomerComment ? 'Customer' : 'Restaurant'}
                      </p>
                    </div>
                  );
                })}
              </div>
              {/* Add Delete Comments Button */}
              {comments.length > 0 && (
                <div className="mt-4">
                  <button
                    onClick={async () => {
                      if (window.confirm('Are you sure you want to delete all comments and ratings for this order?')) {
                        try {
                          await deleteAllCommentsForOrder(order.id);
                          setPopup({ message: "Comments and ratings deleted! 🧹", type: 'success' });
                        } catch (error) {
                          console.error('Failed to delete comments and ratings:', error);
                          setPopup({ message: "Couldn't delete comments 😢", type: 'error' });
                        }
                      }
                    }}
                    className="flex items-center gap-2 bg-teal-700 text-white px-4 py-2 rounded-lg shadow-md hover:bg-red-600 transform hover:scale-[1.02] transition-all duration-200 text-sm font-medium cursor-pointer"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clipRule="evenodd" />
                    </svg>
                    Delete All Comments and Ratings
                  </button>
                </div>
              )}


              {order.status === 'DELIVERED' ? (
                <>
                    {!lastCommentByCustomer ? (
                      <div className="mt-3 space-y-4">
                        <div className="space-y-2">
                          <label className="block text-sm font-medium text-gray-700">Courier Rating:</label>
                          {renderStars(courierRating, setCourierRating)}
                        </div>
                        
                        <div className="space-y-2">
                          <label className="block text-sm font-medium text-gray-700">Restaurant Rating:</label>
                          {renderStars(restaurantRating, setRestaurantRating)}
                        </div>

                        <textarea
                          value={commentText}
                          onChange={(e) => setCommentText(e.target.value)}
                          placeholder="Write your comment here..."
                          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                          rows={3}
                        />
                       <button
                          onClick={async () => {
                            if (courierRating > 0 && restaurantRating > 0) {
                              try {
                                await addCommentToOrder(order.id, commentText.trim() || ".", courierRating, restaurantRating);
                                setCommentText('');
                                setCourierRating(0);
                                setRestaurantRating(0);
                                await fetchOrdersByCustomer(user?.customerId!);
                                setPopup({ message: "Thanks for your feedback! 🌟", type: 'success' });
                              } catch (error: any) {
                                console.error('Failed to add comment:', error.response?.data || error);
                                setPopup({ 
                                  message: `Oops! ${error.response?.data?.message || error.message} 😢`, 
                                  type: 'error' 
                                });
                              }
                            } else {
                              setPopup({ message: "Please rate both courier and restaurant! ⭐", type: 'error' });
                            }
                          }}
                          className="flex items-center gap-2 bg-gradient-to-r from-orange-500 to-orange-600 text-white px-6 py-3 rounded-lg shadow-md hover:from-orange-600 hover:to-orange-700 transform hover:scale-[1.02] transition-all duration-200 text-sm font-medium cursor-pointer"
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                            <path fillRule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" clipRule="evenodd" />
                          </svg>
                          Add Comment and Rating
                        </button>
                      </div>
                    ) : (
                    <p className="text-sm text-gray-500 mt-2 italic">
                      Last comment was made by you. You can only add a comment once.
                    </p>
                  )}
                </>
              ) : (
                <p className="text-red-600 mt-2">
                  Your delivery is not completed yet. You can not add a comment.
                </p>
              )}
            </div>
          );
        })}
    </div>

      {/* ❤️ Favori Restoranlar */}
      <div>
        <h2 className="text-2xl font-semibold text-gray-700 mb-4">Favorite Restaurants</h2>
        {favoriteRestaurants.length > 0 ? (
          <ul className="space-y-4">
            {favoriteRestaurants.map((fav) => (
              <li
                key={fav.id}
                className="flex items-center justify-between bg-white p-4 rounded-lg shadow-md border border-gray-200 hover:shadow-xl transition-all"
              >
                <div className="flex items-center space-x-4">
                  {fav.logo && (
                    <img 
                      src={fav.logo} 
                      alt={fav.restaurantName}
                      className="w-16 h-16 object-cover rounded-full"
                    />
                  )}
                  <div className="flex flex-col">
                    <Link
                      to={`/restaurants/${fav.restaurantId}`}
                      className="text-teal-600 hover:underline font-medium"
                    >
                      {fav.restaurantName}
                    </Link>
                    {fav.cuisine && (
                      <span className="text-sm text-gray-600">Cuisine: {fav.cuisine}</span>
                    )}
                    {fav.deliveryRange && (
                      <span className="text-sm text-gray-600">Delivery Range: {fav.deliveryRange} km</span>
                    )}
                    {fav.hours && (
                      <span className="text-sm text-gray-600">Hours: {fav.hours}</span>
                    )}
                  </div>
                </div>
                <button
                  onClick={() => handleRemoveFavorite(fav.restaurantId)}
                  className="text-red-600 hover:text-red-800 transition duration-200"
                  title="Remove from favorites"
                >
                  <FaHeart size={24} />
                </button>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-sm text-gray-500">No favorite restaurants.</p>
        )}
      </div>

    </div>
  );
}
