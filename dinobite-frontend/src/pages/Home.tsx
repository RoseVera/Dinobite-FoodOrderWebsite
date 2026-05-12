import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { BotIcon, X } from "lucide-react";
import wheel from "../assets/wheel.png";
import { useRestaurantStore } from "../store/RestaurantStore";
import { LoadingPage } from '../components/Loading';

const MAX_PRICE = 500;

const Home: React.FC = () => {
  const { restaurants, loading, error, fetchRestaurants } = useRestaurantStore();

  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [maxPrice, setMaxPrice] = useState(MAX_PRICE);
  const [open, setOpen] = useState(false);
  const [minRating, setMinRating] = useState(0);
  const [showOnlyOpen, setShowOnlyOpen] = useState(false);
  const [sortBy, setSortBy] = useState("rating");
  const [highProteinOnly, setHighProteinOnly] = useState(false);



  const cuisines = Array.from(new Set(restaurants.map(r => r.cuisine).filter(Boolean)));

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const handleCategoryChange = (category: string) => {
    setSelectedCategories(prev =>
      prev.includes(category)
        ? prev.filter(c => c !== category)
        : [...prev, category]
    );
  };

  const filteredRestaurants = restaurants.filter(({ hours,rating, foods, cuisine, name, address, min }) => {
    const matchesCategory =
      selectedCategories.length === 0 || selectedCategories.includes(cuisine ?? "");

      const search = searchTerm.toLowerCase();

      const matchesSearch =
        (name?.toLowerCase().includes(search) || false) ||
        (address?.toLowerCase().includes(search) || false) ||
        (foods?.some(food =>
          food.name.toLowerCase().includes(search)
        ) || false);

    const matchesPrice = min <= maxPrice;
    const matchesRating = (rating ?? 0) >= minRating;
    const matchesOpen = !showOnlyOpen || isOpenNow(hours);
    const matchesProtein =
    !highProteinOnly || (foods && foods.some((food) => food.protein >= 25));

    return matchesCategory && matchesSearch && matchesPrice && matchesRating && matchesOpen && matchesProtein;
  });
  const isOpenNow = (hours: any) => {
    const [start, end] = hours.split("-").map((h: string) => parseInt(h));
    const currentHour = new Date().getHours();
    console.log(currentHour, start, end)
    return currentHour >= start && currentHour < end;
  };
  

  filteredRestaurants.sort((a, b) => {
    if (sortBy === 'rating') return (b.rating ?? 0) - (a.rating ?? 0);
    if (sortBy === 'price') return a.min - b.min;
    return 0;
  });

  return (
    <div className="p-4 md:p-10 grid grid-cols-1 bg-gray-200">
      {open && (
        <div className="fixed bottom-34 right-6 z-50 bg-white shadow-lg rounded-xl p-4 w-80 max-w-full">
          <div className="flex justify-between items-center mb-2">
            <h2 className="text-lg font-bold">Dino AI</h2>
            <button onClick={() => setOpen(false)} className="text-gray-500 hover:text-black">
              <X size={20} />
            </button>
          </div>
          <div className="text-gray-600 text-sm">
            Hi! I am ready to talk with you. You can ask me anything 🦖
          </div>
        </div>
      )}

      {/* Dino AI Button */}
      <Link
        to="/chatbot" // << YÖNLENDİRİLECEK SAYFA
        className="fixed bottom-10 right-10 z-40 bg-orange-600 hover:bg-orange-700 text-white rounded-full p-4 shadow-lg transition transform hover:scale-110" // z-index'i bilgi baloncuğundan düşük olabilir
        title="Chat with DinoAI"
        onClick={() => setOpen(false)} // Chat sayfasına giderken bilgi baloncuğunu kapat
                                      // Veya bu onClick'i tamamen kaldırabilirsin,
                                      // bilgi baloncuğunu başka bir şekilde tetikleyebilirsin (örn: fareyle üzerine gelince)
      >
        <BotIcon size={58} />
      </Link>

      {/* Main Content */}
      <div className="col-span-4 mt-6 p-6 bg-white rounded-lg">
          <Link
                to="/spin-wheel"
                className="absolute top-36 right-16 z-20 bg-transparent rounded-full p-1 hover:scale-110 active:scale-100 transition-transform duration-200 block"
                style={{ width: '150px', height: '150px' }}
                title="Spin the Wheel for Prizes!"
            >
                <img
                    src={wheel} 
                    alt="Spin the Wheel"
                    className="w-full h-full object-contain drop-shadow-md"
                />
          </Link>

       

        <h2 className="text-6xl font-bold mb-4 text-center p-10">DinoDash Restaurants</h2>

        <div className="flex flex-col lg:flex-row space-y-6 lg:space-y-0 lg:space-x-4">
          {/* Filters */}
          <div className="w-full lg:w-1/4 space-y-6 text-white">
            <select onChange={(e) => setSortBy(e.target.value)} className="bg-orange-600 p-6 rounded-lg shadow-md w-full font-bold text-xl pl-2">
              <option value="rating" className="hover:bg-orange-100 font-bold">Rating</option>
              <option value="price">Minimum Price</option>
            </select>
            {/* Search */}
            <div className="bg-orange-600 p-6 rounded-lg shadow-md ">
              <h3 className="text-3xl font-bold mb-4 text-center">Find Your Need!</h3>
              <div className="mb-4">
                <input
                  type="text"
                  className="w-full p-3 border border-teal-700 rounded-md shadow-sm focus:ring-2 focus:ring-orange-500 bg-gray-200 text-gray-800"
                  placeholder="Search..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>

            {/* Cuisine */}
            <div className="bg-orange-600 p-6 rounded-lg shadow-md">
              <h4 className="text-3xl font-bold mb-4 text-center">Cuisines</h4>
              <ul className="space-y-4">
                {cuisines.map((cuisine) => (
                  <li key={cuisine} className="flex items-center text-xl">
                    <input
                      type="checkbox"
                      className="w-6 h-6 mr-3"
                      onChange={() => handleCategoryChange(cuisine)}
                    />
                    <span>{cuisine}</span>
                  </li>
                ))}
              </ul>
            </div>
            {/* Rating Filter */}
            <div className="bg-orange-600 p-6 rounded-lg shadow-md text-white">
              <h4 className="text-3xl font-bold mb-4 text-center">Minimum Rating</h4>
              <div className="flex flex-col items-center">
                <input
                  type="range"
                  min="0"
                  max="5"
                  step="0.1"
                  value={minRating}
                  onChange={(e) => setMinRating(Number(e.target.value))}
                  className="w-full mb-2 bg-teal-500"
                />
                <span className="text-lg">{minRating.toFixed(1)} ★ & above</span>
              </div>
            </div>
  
            {/* Price Range */}
            <div className="bg-orange-600 p-6 rounded-lg shadow-md text-white">
              <h4 className="text-3xl font-bold mb-4 text-center">Price Range</h4>
              <div className="flex flex-col items-center">
                <input
                  type="range"
                  min="0"
                  max={MAX_PRICE}
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(Number(e.target.value))}
                  className="w-full mb-2 bg-teal-500"
                />
                <span className="text-lg">Up to ₺{maxPrice}</span>
              </div>
            </div>

            {/* Protein-Rich Option */}
            <div className="bg-orange-600 p-6 rounded-lg shadow-md text-white">
              <h4 className="text-3xl font-bold mb-4 text-center">Nutrition</h4>
              <label className="flex items-center text-xl">
                <input
                  type="checkbox"
                  className="w-6 h-6 mr-3"
                  checked={highProteinOnly}
                  onChange={() => setHighProteinOnly(!highProteinOnly)}
                />
                High Protein Options
              </label>
            </div>

          </div>

          {/* Restaurants */}
          <div className="w-3/4">
            {loading ? (
              <LoadingPage />
            ) : error ? (
              <div className="text-center text-2xl font-bold text-red-500 py-20">Hata: {error}</div>
            ) : filteredRestaurants.length === 0 ? (
              <div className="text-center text-xl font-semibold text-gray-600 py-20">
                No matching restaurants found. Check filters or search term.
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                {filteredRestaurants.map(({ id, logo, name, rating, min, address }) => (
                  <div
                    key={id}
                    className="bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-xl transition"
                  >
                    <Link
                      key={id}
                      to={`/restaurants/${id}`}
                      className="bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-xl transition"
                    >
                      <img src={logo} alt={name} className="w-full h-60 object-cover" />
                      <div className="p-4">
                          <h3 className="text-xl font-bold">{name}</h3>
                          <p className="text-yellow-500">⭐ {rating}</p>
                          <p className="text-gray-500 text-sm">Min. Order ₺{min}</p>
                          <p className="text-gray-500 text-sm">{address}</p>
                      </div>
                    </Link>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
export default Home;