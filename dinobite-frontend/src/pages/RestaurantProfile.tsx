import { useState, useEffect } from 'react';
import axios, { AxiosError } from 'axios';
import { useUserStore } from '@/store/UserStore';
import { useCourierStore } from '../store/CourierStore';
import { useOrderStore } from '../store/OrderStore';
import type { Order } from '../store/OrderStore'; // dosya yolunu doğru şekilde ayarla
import { LoadingPage } from '../components/Loading';
import { ErrorPage } from '../components/Failed';
import { useNavigate } from 'react-router-dom';

const RestaurantManagementPage = () => {
    const clearUser = useUserStore((state) => state.clearUser);
    const navigate = useNavigate();
    const user = useUserStore((state) => state.user);
    const { couriers, error, fetchCouriers } = useCourierStore();
    const { orders, addCommentToOrderRestaurant, updateOrderStatus, fetchOrdersByRestaurant, deleteOrderWithItemsAndComments } = useOrderStore();
    const statuses: ('ALL' | Order['status'])[] = ['ALL', 'PENDING', 'PREPARING', 'READY_FOR_PICKUP', 'ON_THE_WAY', 'CANCELLED', 'DELIVERED'];
    const [restaurant, setRestaurant] = useState<any>(null);
    const [categories, setCategories] = useState<any[]>([]);
    const [newCategory, setNewCategory] = useState<string>('');
    const [newFood, setNewFood] = useState({
        name: '',
        price: 0,
        description: '',
        image: '',
        protein: 0,
        carbs: 0,
        fats: 0,
        sugar: 0,
    });
    const [selectedCategory, setSelectedCategory] = useState<string>('');
    const [editingCategory, setEditingCategory] = useState<any | null>(null);
    const [editingFood, setEditingFood] = useState<any | null>(null);
    const [loading_, setLoading] = useState<boolean>(false);
    const [error_, setError] = useState<string | null>(null);
    const [errorUpdate, setErrorUpdate] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [errorFood, setErrorFood] = useState<string | null>(null);
    const [errorCategory, setErrorCategory] = useState<string | null>(null);
    const [errorAddCategory, setErrorAddCategory] = useState<string | null>(null);
    const [errorAddFood, setErrorAddFood] = useState<string | null>(null);
    const [activeTab, setActiveTab] = useState('menu');
    const [selectedStatus, setSelectedStatus] = useState<'ALL' | Order['status']>('ALL');
    const [selectedCouriers, setSelectedCouriers] = useState<Record<number, number>>({});
    const [newComments, setNewComments] = useState<{ [orderId: number]: string }>({});
    const [openOrderId, setOpenOrderId] = useState<number | null>(null);
    const [expandedOrderId, setExpandedOrderId] = useState<number | null>(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [orderToDelete, setOrderToDelete] = useState<Order | null>(null);
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    useEffect(() => {
        if (errorCategory) {
          const timer = setTimeout(() => setErrorCategory(null), 3000); 
          return () => clearTimeout(timer); 
        }
        if (errorFood) {
            const timer = setTimeout(() => setErrorFood(null), 3000); 
            return () => clearTimeout(timer); 
          }
        if (errorAddFood) {
            const timer = setTimeout(() => setErrorAddFood(null), 3000); 
            return () => clearTimeout(timer); 
        }
        if (errorAddCategory) {
            const timer = setTimeout(() => setErrorAddCategory(null), 3000); 
            return () => clearTimeout(timer); 
        }
        if (errorUpdate) {
            const timer = setTimeout(() => setErrorUpdate(null), 3000); 
            return () => clearTimeout(timer); 
        }
        if (success) {
            const timer = setTimeout(() => setSuccess(null), 3000); 
            return () => clearTimeout(timer); 
        }
      }, [errorCategory, errorFood, errorAddFood, errorAddCategory, errorUpdate, success]);

    useEffect(() => {
        if (!user || !user.restaurantId) return;
        const fetchRestaurantData = async () => {
            setLoading(true);
            try {
                const response = await axios.get(`http://localhost:9090/api/v1/restaurants/${user?.restaurantId}`);
                const restaurant = response.data;    
                if (!restaurant) {
                    setError('No restaurant found for this user');
                    setLoading(false);
                    return;
                }
                    const categoriesWithFoods = restaurant.categories.map((category: any) => {
                    const foodsInCategory = restaurant.foods.filter((food: any) => food.categoryId === category.id);
                    return {
                        ...category,
                        foods: foodsInCategory,
                    };
                });
                setRestaurant(restaurant);
                setCategories(categoriesWithFoods);
                setLoading(false);
            
            } catch (err) {
                setError('Failed to fetch restaurant data');
                setLoading(false);
            }
        };
        if (user?.restaurantId) {
            fetchOrdersByRestaurant(user.restaurantId);
          }
        fetchCouriers(); 
        fetchRestaurantData();
    }, [user?.restaurantId]);

    const handleUpdate = async () => {
        try {
          await axios.put(`http://localhost:9090/api/v1/restaurants/${restaurant.id}`, restaurant);
          setSuccess('Restaurant updated successfully!');
        } catch (err) {
          setErrorUpdate('Failed to update restaurant. Format is incorrect.');
        }
    };

    const handleAddCategory = async () => {
        if (!newCategory) return;

        try {
            const response = await axios.post(`http://localhost:9090/api/v1/restaurants/${restaurant.id}/categories`, {
                name: newCategory,
                restaurantId: restaurant.id,
            });
            setCategories([...categories, response.data]);
            setNewCategory('');
        } catch (error) {
        const axiosError = error as AxiosError<any>; // 👈 type casting burada
        console.log("errror" ,axiosError)
        if (axiosError.response && axiosError.response.data && axiosError.response.data.message) {
            setErrorAddCategory(axiosError.response.data.message);
        } else {
            setErrorAddCategory('An unexpected error occurred while adding category.');
        }
    }
};

    const handleAddFoodItem = async () => {
        // Check if the required fields are filled
        if (!newFood.name || newFood.price <= 0 || !selectedCategory) return;

        try {
            const response = await axios.post(
                `http://localhost:9090/api/v1/restaurants/${restaurant.id}/foods`,
                {
                    restaurantId: restaurant.id,
                    categoryId: selectedCategory,
                    name: newFood.name,
                    price: newFood.price,
                    description: newFood.description,
                    image: newFood.image,
                    protein: newFood.protein,
                    carbs: newFood.carbs,
                    fats: newFood.fats,
                    sugar: newFood.sugar,
                    availability: true,  // Assuming it's available by default
                }
            );

            setCategories(categories.map((category) =>
                category.id === selectedCategory
                    ? { ...category, foods: [...category.foods, response.data] }
                    : category
            ));

            setNewFood({
                name: '',
                price: 0,
                description: '',
                image: '',
                protein: 0,
                carbs: 0,
                fats: 0,
                sugar: 0,
            });
        } catch (error) {
            setErrorAddFood('Failed to add food item. The format is incorrect or the food item already exists.');
        }
    };

    const handleRemoveFoodItem = async (categoryId: string, foodId: string) => {
        try {
            await axios.delete(`http://localhost:9090/api/v1/restaurants/${restaurant.id}/foods/${foodId}`);
            setCategories(categories.map((category) =>
                category.id === categoryId ? { ...category, foods: category.foods.filter((food: any) => food.id !== foodId) } : category
            ));
        } catch (error) {
            setErrorFood('You have orders under this food item. You cannot delete it. Try updating it instead.');
        }
    };

    const handleRemoveCategory = async (categoryId: string) => {
        try {
            await axios.delete(`http://localhost:9090/api/v1/restaurants/${restaurant.id}/categories/${categoryId}`);
            // Remove the deleted category from the state
            setCategories(categories.filter((category) => category.id !== categoryId));
        } catch (error) {
            setErrorCategory('You have orders under this category. You cannot delete it. Try updating it instead.');
        }
    };

    const handleUpdateCategory = async () => {
        if (!editingCategory?.name) return;

        try {
            const response = await axios.put(
                `http://localhost:9090/api/v1/restaurants/${restaurant.id}/categories/${editingCategory.id}`,
                {
                    name: editingCategory.name, restaurantId: restaurant.id
                }
            );
            setCategories(categories.map((category) =>
                category.id === editingCategory.id
                    ? { ...category, name: response.data.name }
                    : category
            ));
            setEditingCategory(null);
        } catch (error) {
            setError('Failed to update category');
        }
    };

    const handleUpdateFoodItem = async () => {
        if (!editingFood?.name) return;

        try {
            const response = await axios.put(
                `http://localhost:9090/api/v1/restaurants/${restaurant.id}/foods/${editingFood.id}`,
                {
                    ...editingFood,
                }
            );
            setCategories(categories.map((category) =>
                category.id === editingFood.categoryId
                    ? {
                        ...category,
                        foods: category.foods.map((food: { id: any; }) =>
                            food.id === editingFood.id ? response.data : food
                        ),
                    }
                    : category
            ));
            setEditingFood(null);
        } catch (error) {
            setError('Failed to update food item');
        }
    };

    const handleAddComment = async (orderId: number) => {
        const content = newComments[orderId];
        if (!content) return;
        try {
          await addCommentToOrderRestaurant(orderId, content);
          setNewComments((prev) => ({ ...prev, [orderId]: "" }));
        } catch (err) {
          console.error("Yorum eklenemedi:", err);
        }
      };

    if (loading_ || !user || !user.restaurantId) {
        return <LoadingPage />;
    }
    if (!loading_ && !restaurant && error_) {
        return <ErrorPage error={error_} />;
    }
    console.log(orders)
    const filteredOrders = selectedStatus === 'ALL'
  ? orders
  : orders.filter(order => order.status === selectedStatus);
      

    return (
        <div className="p-20 space-y-8 bg-gray-50 min-h-screen">
            {restaurant && (
                <>
                    <div className="flex items-center justify-start space-x-6 py-8 bg-white rounded-md p-10">
                        <div className="flex flex-col space-y-4 w-1/4">
                            <input
                            type="text"
                            value={restaurant.name}
                            onChange={(e) => setRestaurant({ ...restaurant, name: e.target.value })}
                            className="text-3xl font-bold text-orange-600"
                            placeholder="Restaurant Name"
                            />

                            <input
                            type="text"
                            value={restaurant.businessOwner}
                            onChange={(e) => setRestaurant({ ...restaurant, businessOwner: e.target.value })}
                            className='text-lg text-gray-700 border-2 p-2 border-gray-300 rounded-md'
                            placeholder="Business Owner"
                            />

                            <input
                            type="phone"
                            value={restaurant.phone}
                            onChange={(e) => setRestaurant({ ...restaurant, phone: e.target.value })}
                            className='text-lg text-gray-700 border-2 p-2 border-gray-300 rounded-md'
                            placeholder="Phone"
                            />

                            <input
                            type="email"
                            value={restaurant.ownerMail}
                            onChange={(e) => setRestaurant({ ...restaurant, ownerMail: e.target.value })}
                            className='text-lg text-gray-700 border-2 p-2 border-gray-300 rounded-md'
                            placeholder="Email"
                            />

                            <input
                            type="text"
                            value={restaurant.hours}
                            onChange={(e) => setRestaurant({ ...restaurant, hours: e.target.value })}
                            className='text-lg text-gray-700 border-2 p-2 border-gray-300 rounded-md'
                            placeholder="Working Hours"
                            />

                            <input
                            type="text"
                            value={restaurant.cuisine}
                            onChange={(e) => setRestaurant({ ...restaurant, cuisine: e.target.value })}
                            className='text-lg text-gray-700 border-2 p-2 border-gray-300 rounded-md'
                            placeholder="Cuisine"
                            />
                            <input 
                            type="integer"
                            value={restaurant.deliveryRange}
                            onChange={(e) => setRestaurant({ ...restaurant, deliveryRange: e.target.value })}
                            className='text-lg text-gray-700 border-2 p-2 border-gray-300 rounded-md'
                            placeholder='Delivery Range'
                            ></input>

                            <input
                            type="text"
                            value={restaurant.adress}
                            onChange={(e) => setRestaurant({ ...restaurant, adress: e.target.value })}
                            className='text-lg text-gray-700 border-2 p-2 border-gray-300 rounded-md'
                            placeholder="Adress"
                            />
                        </div>
                        <div className='flex flex-col'>
                            <img
                            src={restaurant.logo}
                            alt={`${restaurant.name} Logo`}
                            className="w-80 h-80 object-cover border-2 border-orange-600 rounded-md"
                            />
                            <input
                            type="text"
                            value={restaurant.logo}
                            onChange={(e) => setRestaurant({ ...restaurant, logo: e.target.value })}
                            className="mt-2 w-80 border-2 rounded-md p-2 border-orange-600"
                            placeholder="Logo URL"
                            />
                            <button
                            onClick={handleUpdate}
                            className="mt-6 px-6 py-2 bg-teal-700 text-white rounded hover:bg-teal-600 transition"
                            >
                            Update
                            </button>
                        </div>
                        <button
                        onClick={() => setShowDeleteModal(true)}
                        className="mt-4 px-6 py-2 bg-red-600 text-white rounded hover:bg-red-500 transition"
                        >
                        Sil
                        </button>
                        {showDeleteModal && (
                        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                            <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                            <h3 className="text-lg font-semibold text-gray-800 mb-4">Are you sure?</h3>
                            <p className="text-sm text-gray-600 mb-6">
                                This action will permanently delete the restaurant and its associated user account.
                            </p>
                            <div className="flex justify-end space-x-4">
                                <button
                                onClick={() => setShowDeleteModal(false)}
                                className="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400"
                                >
                                Cancel
                                </button>
                                <button
                                onClick={async () => {
                                    try {
                                    console.log("restoran id", restaurant.restaurantId);
                                    console.log("user id", restaurant.id);
                                    
                                    await axios.delete(`http://localhost:9090/api/v1/restaurants/${restaurant.restaurantId}`);
                                    await axios.delete(`http://localhost:9090/api/v1/users/${restaurant.id}`);
                                    
                                    setShowDeleteModal(false);
                                    setSuccessMessage("Restaurant deleted successfully.");
                                    setErrorMessage('');
                                    
                                    
                                    setTimeout(() => {
                                        clearUser();
                                        navigate('/register');
                                    }, 1500);
                                    } catch (error) {
                                    console.error("Delete error:", error);
                                    setErrorMessage("An error occurred while deleting. Restaurant may have orders or categories in place.");
                                    setSuccessMessage('');
                                    setShowDeleteModal(false);
                                    }
                                }}
                                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                                >
                                Confirm Delete
                                </button>
                            </div>
                            </div>
                        </div>
                        )}
                        {errorMessage && (
                        <p className="mt-4 text-red-600 font-semibold">{errorMessage}</p>
                        )}
                        {successMessage && (
                        <p className="mt-4 text-green-600 font-semibold">{successMessage}</p>
                        )}
                        {errorUpdate && (
                            <p className="text-red-500">{errorUpdate}</p>
                        )}
                        {success && (
                            <p className="text-green-500">{success}</p>
                        )}
                    </div>
                    <div className="p-6">
                    {/* Tab Butonları */}
                    <div className="flex space-x-4 border-b border-gray-300 mb-6">
                        <button
                        onClick={() => setActiveTab('menu')}
                        className={`pb-2 ${activeTab === 'menu' ? 'border-b-4 border-orange-500 font-bold text-orange-600' : 'text-gray-500'}`}
                        >
                        Menu Operations
                        </button>
                        <button
                        onClick={() => setActiveTab('orders')}
                        className={`pb-2 ${activeTab === 'orders' ? 'border-b-4 border-orange-500 font-bold text-orange-600' : 'text-gray-500'}`}
                        >
                        Deliveries & Couriers
                        </button>
                        <button
                        onClick={() => setActiveTab('reviews')}
                        className={`pb-2 ${activeTab === 'reviews' ? 'border-b-4 border-orange-500 font-bold text-orange-600' : 'text-gray-500'}`}
                        >
                        Reviews
                        </button>
                    </div>

                    {/* Tab Content */}
                    <div>
                        {activeTab === 'menu' && (
                        <div>
                            {/* Menü İşlemleri */}
                            <div className="flex space-x-8 py-8 bg-white p-10 rounded-md">
                                <div className="flex-1 space-y-8">
                                    <div className="space-y-4">
                                        <h2 className="text-2xl font-semibold text-gray-800">Add Category</h2>
                                        <div className="flex space-x-4">
                                            <input
                                                type="text"
                                                value={newCategory}
                                                onChange={(e) => setNewCategory(e.target.value)}
                                                placeholder="Enter new category name"
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <button onClick={handleAddCategory} className="px-4 py-2 bg-teal-700 text-white rounded-md hover:bg-teal-600">
                                                Add Category
                                            </button>
                                        </div>
                                    </div>

                                    <div className="space-y-4">
                                        <h2 className="text-2xl font-semibold text-gray-800">Add Food Item</h2>
                                        <div className="flex flex-col space-y-4">
                                            <select
                                                onChange={(e) => setSelectedCategory(e.target.value)}
                                                value={selectedCategory}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            >
                                                <option value="">Select Category</option>
                                                {categories.map((category) => (
                                                    <option key={category.id} value={category.id}>
                                                        {category.name}
                                                    </option>
                                                ))}
                                            </select>
                                            <input
                                                type="text"
                                                placeholder="Food name"
                                                value={newFood.name}
                                                onChange={(e) => setNewFood({ ...newFood, name: e.target.value })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <span className='text-l'>Price</span>
                                            <input
                                                type="integer"
                                                placeholder="Price"
                                                value={newFood.price}
                                                onChange={(e) => setNewFood({ ...newFood, price: Number(e.target.value) })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <input
                                                type="text"
                                                placeholder="Description"
                                                value={newFood.description}
                                                onChange={(e) => setNewFood({ ...newFood, description: e.target.value })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <input
                                                type="text"
                                                placeholder="Image URL"
                                                value={newFood.image}
                                                onChange={(e) => setNewFood({ ...newFood, image: e.target.value })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <div className='grid grid-cols-2 gap-4 pl-30 pr-30'>
                                            <span className='text-l font-bold text-teal-600'>Protein <span className='text-teal-700'>(g)</span></span>
                                            <input
                                                type="integer"
                                                placeholder="Protein (g)"
                                                value={newFood.protein}
                                                onChange={(e) => setNewFood({ ...newFood, protein: Number(e.target.value) })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <span className='text-l font-bold text-yellow-600'>Carbs <span className='text-yellow-700'>(g)</span></span>
                                            <input
                                                type="integer"
                                                placeholder="Carbs (g)"
                                                value={newFood.carbs}
                                                onChange={(e) => setNewFood({ ...newFood, carbs: Number(e.target.value) })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <span className='text-l font-bold text-red-600'>Fats <span className='text-red-700'>(g)</span></span>
                                            <input
                                                type="integer"
                                                placeholder="Fats (g)"
                                                value={newFood.fats}
                                                onChange={(e) => setNewFood({ ...newFood, fats: Number(e.target.value) })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            <span className='text-l font-bold text-pink-500'>Sugar <span className='text-pink-600'>(g)</span></span>
                                            <input
                                                type="integer"
                                                placeholder="Sugar (g)"
                                                value={newFood.sugar}
                                                onChange={(e) => setNewFood({ ...newFood, sugar: Number(e.target.value) })}
                                                className="w-full p-2 border border-gray-300 rounded-md"
                                            />
                                            </div>
                                            <button
                                                onClick={handleAddFoodItem}
                                                className="px-4 py-2 bg-teal-700 text-white rounded-md hover:bg-teal-600"
                                            >
                                                Add Food Item
                                            </button>
                                        </div>
                                    </div>
                                    {errorAddCategory && (
                                        <p className="text-red-500">{errorAddCategory}</p>
                                    )}
                                    {errorAddFood && (
                                        <p className="text-red-500">{errorAddFood}</p>
                                    )}
                                </div>
                                <div className="flex-1 space-y-6">
                                    <h2 className="text-2xl font-semibold text-gray-800">Menu</h2>
                                    {categories.length > 0 ? (
                                        categories.map((category) => (
                                            <div key={category.id} className="border p-4 rounded-md bg-white shadow-md">
                                                <h3 className="text-xl font-semibold text-gray-700">
                                                    {category.name}
                                                    <button
                                                        onClick={() => setEditingCategory(category)}
                                                        className="px-4 py-1.5 bg-teal-700 text-white rounded-md hover:bg-teal-600 ml-5"
                                                    >
                                                        Edit
                                                    </button>
                                                    <button
                                                        onClick={() => handleRemoveCategory(category.id)} 
                                                        className="px-4 py-1.5 bg-teal-700 text-white rounded-md hover:bg-teal-600 ml-5"
                                                    >
                                                        Delete
                                                    </button>
                                                </h3>
                                                {editingCategory && editingCategory.id === category.id && (
                                                    <div className="mt-4">
                                                        <input
                                                            type="text"
                                                            value={editingCategory.name}
                                                            onChange={(e) => setEditingCategory({ ...editingCategory, name: e.target.value })}
                                                            className="w-full p-2 border border-gray-300 rounded-md"
                                                        />
                                                        <button
                                                            onClick={handleUpdateCategory}
                                                            className="mt-2 rounded-md px-4 py-1.5 bg-teal-700 text-white rounded-md hover:bg-teal-600"
                                                        >
                                                            Update Category
                                                        </button>
                                                    </div>
                                                )}

                                                <ul className="space-y-2 mt-5">
                                                    {category.foods && category.foods.length > 0 ? (
                                                        category.foods.map((food: any) => (
                                                            <li key={food.id} className="flex justify-between items-center">
                                                                <img src={food.image} className='w-20 h-20 rounded'></img>
                                                                <span className='ml-3 text-gray-700'><span className='font-bold'>{food.name}</span> - ${food.price}</span>
                                                                <div className="flex space-x-4 ml-auto"> {/* Butonlar sağda, aralarındaki mesafe arttı */}
                                                                    <button
                                                                        onClick={() => handleRemoveFoodItem(category.id, food.id)}
                                                                        className="px-2 py-1 bg-red-500 text-white rounded-md hover:bg-red-600"
                                                                    >
                                                                        Remove
                                                                    </button>
                                                                    <button
                                                                        onClick={() => setEditingFood(food)}
                                                                        className="px-2 py-1 bg-yellow-500 text-white rounded-md hover:bg-red-600"
                                                                    >
                                                                        Edit
                                                                    </button>
                                                                </div>
                                                            </li>
                                                        ))
                                                    ) : (
                                                        <p>No food items available</p>
                                                    )}
                                                </ul>

                                                {editingFood && editingFood.categoryId === category.id && (
                                                    <div className="grid grid-cols-2 gap-4 pl-10 pr-10 mt-4">
                                                        <div>
                                                            <span className='font-bold'>Item Name</span>
                                                            <input
                                                            type="text"
                                                            value={editingFood.name}
                                                            onChange={(e) => setEditingFood({ ...editingFood, name: e.target.value })}
                                                            className="w-full p-2 border border-gray-300 rounded-md mb-3"
                                                        />
                                                        </div>
                                                
                                                        <div>
                                                            <span className='font-bold'>Price</span>
                                                            <input
                                                                type="integer"
                                                                placeholder="Price"
                                                                value={editingFood.price}
                                                                onChange={(e) => setEditingFood({ ...editingFood, price: Number(e.target.value) })}
                                                                className="w-full p-2 border border-gray-300 rounded-md"
                                                            />
                                                        </div>
                                                        <div>
                                                            <span className='font-bold'>Description</span>
                                                            <input
                                                            type="text"
                                                            placeholder="Description"
                                                            value={editingFood.description}
                                                            onChange={(e) => setEditingFood({ ...editingFood, description: e.target.value })}
                                                            className="w-full p-2 border border-gray-300 rounded-md"
                                                        />
                                                        </div>
                                                        
                                                        
                                                        <div>
                                                            <span className='font-bold'>Image URL</span>
                                                            <input
                                                            type="text"
                                                            placeholder="Image URL"
                                                            value={editingFood.image}
                                                            onChange={(e) => setEditingFood({ ...editingFood, image: e.target.value })}
                                                            className="w-full p-2 border border-gray-300 rounded-md"
                                                        />
                                                        </div>

                                                        
                                                        <div>
                                                        <span className='text-l font-bold text-teal-600'>Protein <span className='text-teal-700'>(g)</span></span>
                                                        <input
                                                            type="integer"
                                                            placeholder="Protein (g)"
                                                            value={editingFood.protein}
                                                            onChange={(e) => setEditingFood({ ...editingFood, protein: Number(e.target.value) })}
                                                            className="w-full p-2 border border-gray-300 rounded-md"
                                                        />
                                                        </div>

                                                        
                                                        <div>
                                                        <span className='text-l font-bold text-yellow-600'>Carbs <span className='text-yellow-700'>(g)</span></span>
                                                        <input
                                                            type="integer"
                                                            placeholder="Carbs (g)"
                                                            value={editingFood.carbs}
                                                            onChange={(e) => setEditingFood({ ...editingFood, carbs: Number(e.target.value) })}
                                                            className="w-full p-2 border border-gray-300 rounded-md"
                                                        />
                                                        </div>

                                                        
                                                        <div>
                                                        <span className='text-l font-bold text-red-600'>Fats <span className='text-red-700'>(g)</span></span>
                                                        <input
                                                            type="integer"
                                                            placeholder="Fats (g)"
                                                            value={editingFood.fats}
                                                            onChange={(e) => setEditingFood({ ...editingFood, fats: Number(e.target.value) })}
                                                            className="w-full p-2 border border-gray-300 rounded-md"
                                                        />
                                                        </div>

                                                        
                                                        <div>
                                                        <span className='text-l font-bold text-pink-500'>Sugar <span className='text-pink-600'>(g)</span></span>
                                                        <input
                                                                type="integer"
                                                                placeholder="Sugar (g)"
                                                                value={editingFood.sugar}
                                                                onChange={(e) => setEditingFood({ ...editingFood, sugar: Number(e.target.value) })}
                                                                className="w-full p-2 border border-gray-300 rounded-md"
                                                            />
                                                        </div>


                                                        <button
                                                            onClick={handleUpdateFoodItem}
                                                            className="px-4 py-2 mt-2 bg-teal-600 hover:bg-teal-700 text-white rounded-md"
                                                        >
                                                            Update Food Item
                                                        </button>
                                                    </div>
                                                )}
                                            </div>
                                        ))
                                    ) : (
                                        <p>No categories available</p>
                                    )}
                                    {errorCategory && (
                                        <p className="text-red-500">{errorCategory}</p>
                                    )}
                                    {errorFood && (
                                        <p className="text-red-500">{errorFood}</p>
                                    )}
                                </div>
                            </div>
                        </div>
                        )}

                        {activeTab === 'orders' && (
                        <div className='bg-white p-10 rounded-md'>
                            <h2 className="text-2xl font-semibold mb-4">Orders</h2>
                    
                            {/* Status filters */}
                            <div className="mb-4 flex flex-wrap gap-2">
                                {statuses.map((status) => (
                                    <button
                                    key={status}
                                    onClick={() => setSelectedStatus(status)}
                                    className={`px-4 py-1 rounded-md border ${
                                        selectedStatus === status ? 'bg-teal-700 text-white' : 'bg-white text-gray-700'
                                    }`}
                                    >
                                    {status}
                                    </button>
                                ))}
                            </div>
                        
                            {/* Order List */}
                            <div className="grid grid-cols-1 m:grid-cols-1 lg:grid-cols-3 gap-4">
                                {filteredOrders.map(order => (
                                    <div key={order.id} className="border rounded-lg p-4 shadow-sm space-y-2">
                                    <div className="flex md:flex-col lg:flex-row justify-between items-center">
                                        <div>
                                        <p><strong className='text-orange-700'>#{order.id}</strong> - {order.customerName}</p>
                                        <p>Status: <span className="font-semibold text-orange-600">{order.status}</span></p>
                                        <p>Total Price: ₺{order.totalPrice}</p>
                                        </div>
                                        
                                        {/* Change Status */}
                                        <select
                                        value={order.status}
                                        onChange={(e) => updateOrderStatus(order.id, e.target.value as Order['status'])}
                                        className="border px-2 py-1 rounded-md"
                                        style={{ display: order.status === 'ON_THE_WAY' || order.status === 'DELIVERED' ? 'none' : 'block' }}
                                        >
                                        <option value="PENDING">PENDING</option>
                                        <option value="PREPARING">PREPARING</option>
                                        </select>

                                        
                                    </div>
                                    <div className="mt-2 ">
                                        <button
                                            onClick={() =>
                                            setExpandedOrderId(expandedOrderId === order.id ? null : order.id)
                                            }
                                            className="text-sm text-white bg-teal-600 p-2 rounded-md"
                                        >
                                            {expandedOrderId === order.id ? 'Hide Details' : 'Show Details'}
                                        </button>
                                        <button
                                        onClick={() => {
                                            setOrderToDelete(order);
                                            setShowDeleteModal(true);
                                        }}
                                        className="text-sm text-white bg-red-500 ml-4 p-2 rounded-md"
                                        >
                                        🗑️ Delete
                                        </button>
                                        </div>

                                        {/* Order Items Detail */}
                                        {showDeleteModal && orderToDelete && (
                                        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
                                            <div className="bg-white p-6 rounded-lg shadow-md w-96">
                                            <h3 className="text-lg font-semibold mb-4">Delete Order</h3>
                                            <p>Are You Sure You Want To Delete This Order?</p>

                                            <div className="mt-6 flex justify-end gap-3">
                                                <button
                                                onClick={() => {
                                                    setShowDeleteModal(false);
                                                    setOrderToDelete(null);
                                                }}
                                                className="px-4 py-2 rounded-md bg-gray-300 hover:bg-gray-400"
                                                >
                                                No
                                                </button>

                                                <button
                                                onClick={() => {
                                                    deleteOrderWithItemsAndComments(orderToDelete);
                                                    setShowDeleteModal(false);
                                                    setOrderToDelete(null);
                                                }}
                                                className="px-4 py-2 rounded-md bg-red-600 text-white hover:bg-red-700"
                                                >
                                                Yes, Delete
                                                </button>
                                            </div>
                                            </div>
                                        </div>
                                        )}
                                        {expandedOrderId === order.id && (
                                        <div className="mt-4 border-t pt-4 space-y-2">
                                            <h4 className="font-semibold text-gray-800 mb-2">🧾 Order Details</h4>
                                            {order.orderItems && order.orderItems.length > 0 ? (
                                            <ul className="space-y-1 text-sm text-gray-700">
                                                {order.orderItems.map((item) => (
                                                <li key={item.id} className="flex justify-between">
                                                    <span>{item.foodName}</span>
                                                    <span className="font-medium">x{item.quantity}</span>
                                                </li>
                                                ))}
                                            </ul>
                                            ) : (
                                            <p className="text-gray-500 text-sm">Order Details Not Found.</p>
                                            )}
                                        </div>
                                        )}
                                    
                                    {/* Set Courier */}
                                    {order.status === 'PREPARING' && (
                                        <div className="mt-2 flex items-center gap-3">
                                            <select
                                            value={selectedCouriers[order.id] || ''}
                                            onChange={(e) =>
                                                setSelectedCouriers((prev) => ({
                                                ...prev,
                                                [order.id]: parseInt(e.target.value),
                                                }))
                                            }
                                            className="border px-3 py-2 rounded-md shadow-sm bg-white"
                                            >
                                            <option value="">Select Courier</option>
                                            {couriers.map((courier) => (
                                                <option key={courier.id} value={courier.id}>
                                                {courier.name} {courier.averageRating ? `⭐ ${courier.averageRating.toFixed(1)}` : '⭐ -'}
                                                </option>
                                            ))}
                                            </select>

                                            <button
                                            onClick={() => {
                                                const courierId = selectedCouriers[order.id];
                                                if (!courierId) {
                                                alert('Lütfen bir kurye seçin!');
                                                } else {
                                                updateOrderStatus(order.id, 'READY_FOR_PICKUP', courierId);
                                                }
                                            }}
                                            className="bg-teal-700 hover:bg-teal-800 text-white px-4 py-2 rounded-md transition"
                                            >
                                            Ready For Pickup
                                            </button>
                                        </div>
                                        )}


                                    {order.status === 'CANCELLED' && (
                                    <div className="mt-2 bg-red-50 border border-red-300 p-3 rounded-md space-y-2">
                                        <p className="text-red-700 font-semibold">
                                        Kurye bu siparişi reddetti. Lütfen yeni bir kurye seçin ve siparişi tekrar başlatın.
                                        </p>
                                        <div className="flex items-center gap-2">
                                        <select
                                            value={selectedCouriers[order.id] || ''}
                                            onChange={(e) =>
                                            setSelectedCouriers(prev => ({ ...prev, [order.id]: parseInt(e.target.value) }))
                                            }
                                            className="border px-2 py-1 rounded-md"
                                        >
                                            <option value="">Kurye Seç</option>
                                            {couriers.map(courier => (
                                            <option key={courier.id} value={courier.id}>
                                                {courier.name}
                                            </option>
                                            ))}
                                        </select>
                                        <button
                                            onClick={() => {
                                            const courierId = selectedCouriers[order.id];
                                            if (!courierId) {
                                                alert('Lütfen bir kurye seçin!');
                                            } else {
                                                updateOrderStatus(order.id, 'PREPARING', courierId);
                                            }
                                            }}
                                            className="bg-orange-600 text-white px-3 py-1 rounded-md"
                                        >
                                            Siparişi Yeniden Başlat
                                        </button>
                                        </div>
                                    </div>
                                    )}

                                    </div>
                                ))}
                                </div>

                        </div>
                        )}

                        {activeTab === 'reviews' && (
                        <div className="bg-white p-10 rounded-md">
                        <h2 className="text-2xl font-semibold mb-6 text-gray-800">Restaurant Reviews</h2>
                        
                      
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                          {orders.filter((order) => order.comments?.length !== 0).map((order) => (
                            <div
                              key={order.id}
                              className="border border-gray-200 p-6 rounded-2xl bg-gray-50 shadow-sm"
                            >
                              <h3 className="font-semibold text-lg mb-4 text-teal-800">🧾 Order No: #{order.id}</h3>
                              <div className="flex items-center gap-6 mb-4 text-sm text-gray-700">
                                <div className="flex items-center gap-1">
                                🍽️ Restaurant Rating:
                                <span className="font-semibold text-teal-700">
                                    {order.restaurantRate !== null ? order.restaurantRate.toFixed(1) : 'N/A'}
                                </span>
                                </div>
                                <div className="flex items-center gap-1">
                                🛵 Courier Rating:
                                <span className="font-semibold text-orange-600">
                                    {order.courierRate !== null ? order.courierRate.toFixed(1) : 'N/A'}
                                </span>
                                
                                </div>
                              </div>
                      
                              <div className="space-y-4">
                                {order.comments?.map((comment, index) => {
                                  const isCustomer = index % 2 === 0;
                                  const authorType = isCustomer ? '👤 Customer' : '🏪 Restaurant';
                                  const authorName = isCustomer ? order.customerName : order.restaurantName;
                      
                                  const formattedDate = new Date(
                                    comment.createdAt.replace(' ', 'T')
                                  ).toLocaleString('en-EN', {
                                    day: '2-digit',
                                    month: 'long',
                                    year: 'numeric',
                                    hour: '2-digit',
                                    minute: '2-digit',
                                  });
                      
                                  return (
                                    <div
                                      key={comment.id}
                                      className={`p-4 rounded-xl shadow-sm ${
                                        isCustomer ? 'bg-white border-l-4 border-teal-500' : 'bg-orange-50 border-l-4 border-orange-400'
                                      }`}
                                    >
                                      <div className="flex justify-between items-center mb-2">
                                        <p className="text-sm font-semibold text-gray-700">
                                          {authorType}: <span className="text-gray-900">{authorName}</span>
                                        </p>
                                        <p className="text-xs text-gray-500">{formattedDate}</p>
                                      </div>
                                      <p className="text-sm text-gray-800">{comment.commentText}</p>
                                    </div>
                                  );
                                })}
                              </div>
                      
                              {/* Adding Comment Part */}
                              <div className="mt-6 bg-white border border-gray-200 rounded-xl p-4">
                                {(() => {
                                  const comments = order.comments || [];
                                  const lastIndex = comments.length - 1;
                                  const lastByRestaurant = lastIndex >= 0 && lastIndex % 2 === 1;
                      
                                  return (
                                    <>
                                      <textarea
                                        rows={3}
                                        className="w-full resize-none p-3 border border-gray-300 rounded-lg text-sm shadow-sm focus:outline-none focus:ring-2 focus:ring-teal-500 transition"
                                        placeholder="Write Response..."
                                        value={newComments[order.id] || ''}
                                        onChange={(e) =>
                                          setNewComments((prev) => ({
                                            ...prev,
                                            [order.id]: e.target.value,
                                          }))
                                        }
                                        disabled={lastByRestaurant}
                                      />
                                      <div className="flex justify-between items-center mt-3">
                                        <button
                                          onClick={() => handleAddComment(order.id)}
                                          className="bg-teal-600 hover:bg-teal-700 text-white px-4 py-2 rounded-lg text-sm transition disabled:opacity-50 disabled:cursor-not-allowed"
                                          disabled={lastByRestaurant}
                                        >
                                          Add Comment
                                        </button>
                                        {lastByRestaurant && (
                                          <p className="text-red-600 text-sm ml-4">
                                            You cannot comment back to back.
                                          </p>
                                        )}
                                      </div>
                                    </>
                                  );
                                })()}
                              </div>
                              <button
                                onClick={() => setOpenOrderId(order.id)}
                                className="mt-4 text-sm text-white bg-teal-600 p-2 rounded-md"
                                >
                                📦 See Order Details
                                </button>
                                {openOrderId === order.id && (
                                    <div className="fixed inset-0 z-50 bg-white/10 backdrop-blur-sm flex items-center justify-center">
                                        <div className="bg-white w-full max-w-md p-6 rounded-lg shadow-lg relative">
                                        <h4 className="text-lg font-semibold mb-4 text-gray-800">📦 Order Details</h4>

                                        <ul className="space-y-2">
                                            {order.orderItems?.map((item) => (
                                            <li key={item.id} className="text-sm text-gray-700 flex justify-between">
                                                <span>{item.foodName}</span>
                                                <span className="font-medium">x{item.quantity}</span>
                                            </li>
                                            ))}

                                            {(!order.orderItems || order.orderItems.length === 0) && (
                                            <li className="text-sm text-gray-500">Order Content Is Not Found.</li>
                                            )}
                                        </ul>

                                        <button
                                            onClick={() => setOpenOrderId(null)}
                                            className="absolute top-2 right-2 text-gray-500 hover:text-gray-800 text-lg"
                                        >
                                            ✕
                                        </button>
                                        </div>
                                    </div>
                                    )}

                            </div>
                          ))}
                        </div>
                      </div>
                      
                        )}
                    </div>
                    </div>   
                </>
            )}

            {error && <div className="text-red-500">{error}</div>}
        </div>
    );
};

export default RestaurantManagementPage;
