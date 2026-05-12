// src/pages/ChatbotPage.tsx
import React, { useState, useEffect, useRef } from 'react';
import dinoBotIcon from '../assets/dinobot.png';
import userIcon from '../assets/user-avatar.png';
import axios from 'axios'; // Import axios
import { useUserStore as useAuthUserStore } from '@/store/UserStore';

// --- Interfaces from your DTOs (simplified for frontend use) ---
interface FoodDto {
  id: number;
  name: string;
  // Add other fields if needed for display or linking
}

interface RestaurantDto {
  id: number;
  name: string;
  foods: FoodDto[]; // Assuming foods are included here
  // Add other fields if needed
}

interface OrderDto {
  id: number;
  restaurantId: number;
  restaurantName: string; // Useful for quick display
  // Add other fields if needed
}

interface CustomerDto {
    id: number;
    userId: number;
    // Add other fields
}

// --- Chatbot specific interfaces ---
interface Message {
  id: string;
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
  avatar?: string;
}

interface Suggestion {
  food: string;
  restaurant: string;
  restaurantId: number;
  foodId: number;
}

interface ChatNode {
  id: string;
  botMessage: string | (() => Promise<string>) | (() => string);
  userOptions?: {
    text: string;
    nextId: string;
    action?: () => Promise<void> | void;
  }[];
}

const API_BASE_URL = 'http://localhost:9090/api/v1'; // ADJUST AS NEEDED

const ChatbotPage: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [currentNodeId, setCurrentNodeId] = useState<string>('start');
  const [isLoadingBotResponse, setIsLoadingBotResponse] = useState<boolean>(false);
  const messagesEndRef = useRef<null | HTMLDivElement>(null);

  const [triedSuggestions, setTriedSuggestions] = useState<Suggestion[]>([]);
  const [untriedSuggestions, setUntriedSuggestions] = useState<Suggestion[]>([]);
  const [currentTriedIndex, setCurrentTriedIndex] = useState<number>(0);
  const [currentUntriedIndex, setCurrentUntriedIndex] = useState<number>(0);
  const [isLoadingSuggestions, setIsLoadingSuggestions] = useState<boolean>(true);
  const [suggestionsError, setSuggestionsError] = useState<string | null>(null);

  const { user } = useAuthUserStore();

  useEffect(() => {
    const fetchUserSuggestions = async () => {
      setIsLoadingSuggestions(true);
      setSuggestionsError(null);
      setTriedSuggestions([]);
      setUntriedSuggestions([]);
      setCurrentTriedIndex(0);
      setCurrentUntriedIndex(0);

      if (!user || !user.id) {
        // For guests, we might fetch all restaurants and suggest popular items as "untried"
        // Or simply state that personalized suggestions need login.
        // For now, let's assume guests can see "untried" from all restaurants.
        try {
            console.log("Fetching all restaurants for guest or user with no orders yet...");
            const allRestaurantsResponse = await axios.get<RestaurantDto[]>(`${API_BASE_URL}/restaurants`);
            const allRestaurants = allRestaurantsResponse.data;
            
            const guestUntried: Suggestion[] = [];
            allRestaurants.forEach(restaurant => {
                restaurant.foods.forEach(food => {
                    guestUntried.push({
                        food: food.name,
                        restaurant: restaurant.name,
                        restaurantId: restaurant.id,
                        foodId: food.id,
                    });
                });
            });
            setUntriedSuggestions(guestUntried);
            if (guestUntried.length === 0) {
                setSuggestionsError("No restaurants or food items found to suggest.");
            }

        } catch (error) {
            console.error("Error fetching restaurants for guest:", error);
            setSuggestionsError("Could not load suggestions at this time.");
        }
        setIsLoadingSuggestions(false);
        return;
      }

      try {
        // 1. Get Customer ID from User ID
        let customerId: number | null = null;
        try {
            const customerResponse = await axios.get<CustomerDto>(`${API_BASE_URL}/customers/users/${user.id}`);
            customerId = customerResponse.data.id;
        } catch (err: any) {
            if (err.response && err.response.status === 404) {
                console.log("No customer profile found for this user. Treating as new.");
                // Proceed to fetch all restaurants for "untried" suggestions
            } else {
                throw err; // Rethrow other errors
            }
        }

        // 2. Get User's Past Orders (if customerId exists)
        const orderedRestaurantIds = new Set<number>();
        if (customerId) {
            try {
                const ordersResponse = await axios.get<OrderDto[]>(`${API_BASE_URL}/orders/customers/${customerId}`);
                ordersResponse.data.forEach(order => orderedRestaurantIds.add(order.restaurantId));
            } catch (err: any) {
                 if (err.response && err.response.status === 404) {
                    console.log("No orders found for this customer.");
                 } else {
                    throw err; // Rethrow other errors
                 }
            }
        }
        

        // 3. Get All Restaurants (these include their food items as per assumption)
        const allRestaurantsResponse = await axios.get<RestaurantDto[]>(`${API_BASE_URL}/restaurants`);
        const allRestaurants = allRestaurantsResponse.data;

        if (!allRestaurants || allRestaurants.length === 0) {
            setSuggestionsError("No restaurants found in the system.");
            setIsLoadingSuggestions(false);
            return;
        }

        const fetchedTried: Suggestion[] = [];
        const fetchedUntried: Suggestion[] = [];

        allRestaurants.forEach(restaurant => {
          if (restaurant.foods && restaurant.foods.length > 0) {
            const isTriedRestaurant = orderedRestaurantIds.has(restaurant.id);
            restaurant.foods.forEach(food => {
              const suggestion: Suggestion = {
                food: food.name,
                restaurant: restaurant.name,
                restaurantId: restaurant.id,
                foodId: food.id,
              };
              if (isTriedRestaurant) {
                fetchedTried.push(suggestion);
              } else {
                fetchedUntried.push(suggestion);
              }
            });
          }
        });
        
        // If user has ordered, but all those restaurants are gone, or they had no food items
        // then fetchedTried might be empty.
        // If user has never ordered, fetchedTried will be empty. orderedRestaurantIds.size will be 0.
        
        setTriedSuggestions(fetchedTried);
        setUntriedSuggestions(fetchedUntried);

        if (fetchedTried.length === 0 && fetchedUntried.length === 0) {
            setSuggestionsError("No food items available to suggest from any restaurant.");
        }

      } catch (error) {
        console.error("Error fetching dynamic suggestions:", error);
        setSuggestionsError("Could not load personalized suggestions. Please try again later.");
      } finally {
        setIsLoadingSuggestions(false);
      }
    };

    fetchUserSuggestions();
  }, [user?.id]); // Re-fetch if user.id changes

  useEffect(() => {
  const addBotResponseForCurrentNode = async () => {
    const lastMessage = messages[messages.length - 1];
    // Sadece son mesaj kullanıcıdansa ve zaten yüklenmiyorsa bot mesajı ekle
    if (!lastMessage || lastMessage.sender !== 'user') {
      return;
    }

    setIsLoadingBotResponse(true);

    const node = chatFlow[currentNodeId];
    if (!node) { /* Hata yönetimi... */ return; }

    let botResponseText = "";
    if (typeof node.botMessage === 'function') {
      // BURADA currentTriedIndex gibi state'ler GÜNCEL DEĞERLERİNİ TAŞIR
      botResponseText = await (node.botMessage as () => Promise<string>)();
    } else {
      botResponseText = node.botMessage;
    }

    const botResponse: Message = {
      id: `bot-${Date.now() + 1}`, // Ensure unique ID
      text: botResponseText,
      sender: 'bot',
      timestamp: new Date(),
      avatar: dinoBotIcon,
    };
    setMessages(prev => [...prev, botResponse]); // Bot mesajı eklendi
    setIsLoadingBotResponse(false);
  };

  if (messages.length > 0) {
      addBotResponseForCurrentNode();
  }
// Bu useEffect, currentNodeId veya messages değiştiğinde (ve diğer ilgili state'ler) çalışır
}, [currentNodeId, messages, currentTriedIndex, currentUntriedIndex, isLoadingSuggestions, suggestionsError]);

  // --- Chat Flow Definition (moved inside component to access state/methods) ---
  const chatFlow: Record<string, ChatNode> = {
    start: {
      id: 'start',
      botMessage: "Hello! I'm Dino, the helpful assistant at DinoDash. How can I help you?🦖",
      userOptions: [
        { text: "I don't know what to eat, suggest me something.😋", nextId: "ask_recommendation_type" },
        { text: "I want to try something new, what do you recommend?🧐", nextId: "suggest_something_new_init" },
        { text: "I have an issue with my order.", nextId: "report_order_issue_confirm" },
      ],
    },
    ask_recommendation_type: {
      id: 'ask_recommendation_type',
      botMessage: () => {
        if (isLoadingSuggestions) return "Hold on, I'm looking up your past favorites... 🤔";
        if (suggestionsError) return `Sorry, I encountered an issue: ${suggestionsError} How about going back to the main menu?`;
        if (triedSuggestions.length === 0) {
          return "It seems I couldn't find any past orders with available items, or you haven't ordered with us before. How about trying something new?";
        }
        if (currentTriedIndex >= triedSuggestions.length) {
          return "I've run out of suggestions from your past orders. Would you like to try something completely new, or shall we go back to the main menu?";
        }
        const suggestion = triedSuggestions[currentTriedIndex];
        return `Would you like to try ${suggestion.food} from ${suggestion.restaurant} again? You've enjoyed that restaurant before!`;
      },
      userOptions: [
        { text: "Yes, sounds good! 😊", nextId: "finish_positive" },
        {
          text: "No, suggest something else from my past orders.",
          nextId: "ask_recommendation_type",
          action: () => {
            setCurrentTriedIndex(prev => prev + 1);
          }
        },
        { text: "Suggest something completely new instead.", nextId: "suggest_something_new_init", action: () => setCurrentUntriedIndex(0) },
        { text: "Back to main menu.", nextId: "start", action: () => setCurrentTriedIndex(0) },
      ],
    },
    suggest_something_new_init: {
      id: 'suggest_something_new_init',
      botMessage: () => {
        if (isLoadingSuggestions) return "Let me find some exciting new options for you... 🧐";
        if (suggestionsError) return `Sorry, I encountered an issue: ${suggestionsError} How about going back to the main menu?`;
        if (untriedSuggestions.length === 0) {
          return "I'm fresh out of brand new suggestions for you at the moment! Maybe check your past favorites (if any)?";
        }
        if (currentUntriedIndex >= untriedSuggestions.length) {
          return "I've shared all my new ideas for now! Would you like to revisit past orders or go back to the main menu?";
        }
        const suggestion = untriedSuggestions[currentUntriedIndex];
        return `I noticed you haven't tried ${suggestion.restaurant} yet. How about their ${suggestion.food}?`;
      },
      userOptions: [
        { text: "Yes, I'll try that! 👍", nextId: "finish_positive" },
        {
          text: "No, suggest another new thing.",
          nextId: "suggest_something_new_init",
          action: () => {
            setCurrentUntriedIndex(prev => prev + 1);
          }
        },
        { text: "Let me see my past orders instead.", nextId: "ask_recommendation_type", action: () => setCurrentTriedIndex(0) },
        { text: "Back to main menu.", nextId: "start", action: () => setCurrentUntriedIndex(0) },
      ]
    },
    report_order_issue_confirm: {
      id: 'report_order_issue_confirm',
      botMessage: "I'm sorry to hear you're having an issue with an order. Are you sure you want to report an issue? I can connect you to support.",
      userOptions: [
        { text: "Yes, please help.", nextId: "report_order_issue" },
        { text: "No, take me back.", nextId: "start" }
      ]
    },
    report_order_issue: {
      id: 'report_order_issue',
      botMessage: "I understand. Please contact our customer service at 📞 555-1234 or via email at support@dinodash.com. They will assist you with your order issue.",
      userOptions: [{ text: "I got it, thanks!", nextId: "finish" }]
    },
    finish_positive: {
      id: 'finish_positive',
      botMessage: "Great choice! I hope you enjoy it. Let me know if there's anything else. Goodbye! 👋",
      // Here you could potentially add an action to navigate to the restaurant/food page
      // e.g., action: () => navigateToFood(suggestion.restaurantId, suggestion.foodId)
    },
    finish: {
      id: 'finish',
      botMessage: "I hope my help was useful. Have a great day! Goodbye 👋👋 ",
    },
  };

  useEffect(() => {
    const loadInitialMessage = async () => {
      const initialNode = chatFlow[currentNodeId];
      if (initialNode) {
        let botMsgText = "";
        if (typeof initialNode.botMessage === 'function') {
          setIsLoadingBotResponse(true);
          if (initialNode.id !== 'start' && isLoadingSuggestions && !suggestionsError) {
             botMsgText = "Loading initial options...";
          } else {
             botMsgText = await initialNode.botMessage();
          }
          setIsLoadingBotResponse(false);
        } else {
          botMsgText = initialNode.botMessage;
        }
        const initialBotMsg: Message = {
          id: `bot-${Date.now()}`,
          text: botMsgText,
          sender: 'bot',
          timestamp: new Date(),
          avatar: dinoBotIcon,
        };
        if (messages.length === 0) {
            setMessages([initialBotMsg]);
        }
      }
    };
    if (messages.length === 0 && !isLoadingSuggestions) { // Wait for suggestions to load/fail before initial message if not 'start'
        loadInitialMessage();
    } else if (messages.length === 0 && currentNodeId === 'start') { // 'start' node is static, can load immediately
        loadInitialMessage();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentNodeId, isLoadingSuggestions, suggestionsError, messages.length]); // chatFlow removed to avoid infinite loop due to its definition inside component

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleOptionClick = async (optionText: string, nextNodeId: string, action?: () => Promise<void> | void) => {
  const userMessage: Message = {
      id: `user-${Date.now()}`,
      text: optionText,
      sender: 'user',
      timestamp: new Date(),
      avatar: userIcon,
    };
  setMessages(prev => [...prev, userMessage]); // Kullanıcı mesajı eklendi

  setIsLoadingBotResponse(true); // Yükleniyor...

  if (action) {
    await Promise.resolve(action()); // Index güncelleme gibi eylemler burada
  }

  setCurrentNodeId(nextNodeId); // Bir sonraki adıma geç

  // ESKİDEN BURADA BOT MESAJI OLUŞTURULUP EKLENİYORDU, ŞİMDİ YOK!
};

  const currentNode = chatFlow[currentNodeId];
  const currentOptions = currentNode?.userOptions?.filter(opt => {
    if (isLoadingSuggestions && currentNode.id !== 'start') return opt.text.toLowerCase().includes("back"); // Only show back if still loading suggestions
    if (suggestionsError && currentNode.id !== 'start') return opt.text.toLowerCase().includes("back") || opt.nextId === 'start';

    if (currentNode.id === 'ask_recommendation_type') {
        if (triedSuggestions.length === 0 && !opt.text.toLowerCase().includes("new") && !opt.text.toLowerCase().includes("back")) return false;
        if (currentTriedIndex >= triedSuggestions.length && opt.text.toLowerCase().includes("past orders")) return false;
    }
    if (currentNode.id === 'suggest_something_new_init') {
        if (untriedSuggestions.length === 0 && !opt.text.toLowerCase().includes("past") && !opt.text.toLowerCase().includes("back")) return false;
        if (currentUntriedIndex >= untriedSuggestions.length && opt.text.toLowerCase().includes("another new")) return false;
    }
    return true;
  });

  return (
    <div className="flex flex-col h-[calc(100vh-100px)] max-w-2xl mx-auto bg-gray-50 shadow-2xl rounded-lg overflow-hidden my-6 border border-gray-200">
      <header className="bg-gradient-to-r from-orange-500 to-red-600 text-white p-4 text-center shadow-md">
        <h1 className="text-xl font-semibold flex items-center justify-center">
          <img src={dinoBotIcon} alt="DinoAI" className="w-8 h-8 mr-2 rounded-full" />
          DinoAI Chat
        </h1>
      </header>

      <div className="flex-grow p-4 space-y-4 overflow-y-auto custom-scrollbar">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex items-end space-x-2 ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}
          >
            {msg.sender === 'bot' && (
              <img src={msg.avatar || dinoBotIcon} alt="Bot Avatar" className="w-8 h-8 rounded-full shadow" />
            )}
            <div
              className={`max-w-[70%] px-4 py-2.5 rounded-xl shadow-md ${
                msg.sender === 'user'
                  ? 'bg-orange-500 text-white rounded-br-none animate-fade-in-right'
                  : 'bg-white text-gray-800 border border-gray-200 rounded-bl-none animate-fade-in-left'
              }`}
            >
              <p className="text-sm whitespace-pre-wrap">{msg.text}</p>
              <p className={`text-xs mt-1.5 ${msg.sender === 'user' ? 'text-orange-100 text-right' : 'text-gray-400 text-left'}`}>
                {msg.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </p>
            </div>
            {msg.sender === 'user' && (
              <img src={msg.avatar || userIcon} alt="User Avatar" className="w-8 h-8 rounded-full shadow" />
            )}
          </div>
        ))}
        {isLoadingBotResponse && (
            <div className="flex justify-start items-end space-x-2">
                <img src={dinoBotIcon} alt="Bot Avatar" className="w-8 h-8 rounded-full shadow" />
                <div className="px-4 py-3 rounded-xl shadow-md bg-white text-gray-800 border border-gray-200 rounded-bl-none">
                    <div className="flex space-x-1 items-center">
                        <span className="block w-2 h-2 bg-gray-400 rounded-full animate-bounce-1"></span>
                        <span className="block w-2 h-2 bg-gray-400 rounded-full animate-bounce-2"></span>
                        <span className="block w-2 h-2 bg-gray-400 rounded-full animate-bounce-3"></span>
                    </div>
                </div>
            </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <footer className="bg-gray-50 border-t border-gray-200 p-4 shadow-inner">
        {currentOptions && currentOptions.length > 0 && !isLoadingBotResponse && (
          <div className="grid grid-cols-1 gap-2">
            {currentOptions.map((option) => (
              <button
                key={option.nextId + option.text.substring(0,10)}
                onClick={() => handleOptionClick(option.text, option.nextId, option.action)}
                disabled={isLoadingBotResponse}
                className="w-full p-3 text-sm bg-white border border-orange-400 text-orange-600 rounded-lg hover:bg-orange-500 hover:text-white hover:border-orange-500 transition-all duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-orange-300 shadow hover:shadow-md"
              >
                {option.text}
              </button>
            ))}
          </div>
        )}
        {(!currentOptions || currentOptions.length === 0) && !isLoadingBotResponse && currentNodeId !== 'start' && !chatFlow[currentNodeId]?.userOptions && (
             <p className="text-center text-sm text-gray-500 italic">
                Sohbeti yeniden başlatmak için
                <button onClick={async () => {
                    const startNode = chatFlow['start'];
                    const startText = typeof startNode.botMessage === 'function' 
                                        ? await (startNode.botMessage as () => Promise<string>)() 
                                        : startNode.botMessage;
                    setMessages([{ 
                        id: `bot-reset-${Date.now()}`, 
                        text: startText, 
                        sender: 'bot', 
                        timestamp: new Date(), 
                        avatar: dinoBotIcon 
                    }]);
                    setCurrentNodeId('start');
                    setCurrentTriedIndex(0);
                    setCurrentUntriedIndex(0);
                    // Optionally, re-trigger suggestion fetching if necessary, though it's tied to user.id
                    // fetchUserSuggestions(); // if you want to force refresh on manual reset
                }} className="text-orange-600 hover:underline font-semibold mx-1">buraya tıklayın</button>.
            </p>
        )}
      </footer>
    </div>
  );
};

export default ChatbotPage;
