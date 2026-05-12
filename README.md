# 🦕 DinoBite - Food Delivery Platform

**DinoBite** is a comprehensive food delivery platform that connects customers, restaurants, and couriers in one seamless application. Built with modern technologies, it provides a complete ecosystem for online food ordering with unique features like a spin-the-wheel rewards system and AI-powered chatbot.

## 🌟 Features

### For Customers
- 🔍 **Restaurant Discovery**: Search and filter restaurants by cuisine, price, rating, and location
- 🍕 **Food Browsing**: Browse restaurant menus with detailed food categories
- 🛒 **Shopping Cart**: Add items to cart with quantity management
- 💖 **Favorites**: Save favorite restaurants for quick access
- 🎰 **Spin the Wheel**: Daily rewards system with discount coupons
- 💬 **AI Chatbot**: Get assistance with orders and restaurant recommendations
- 📦 **Order Tracking**: Real-time order status updates
- ⭐ **Reviews & Ratings**: Rate restaurants and leave comments
- 👤 **Profile Management**: Update personal information and preferences

### For Restaurants
- 🏪 **Restaurant Profile**: Manage restaurant information, hours, and details
- 📋 **Menu Management**: Create and organize food categories and items
- 📊 **Order Management**: Accept/reject incoming orders
- 💰 **Pricing Control**: Set food prices and manage availability
- 📈 **Analytics**: Track orders and customer feedback

### For Couriers
- 🚚 **Delivery Management**: Accept and manage delivery requests
- 🗺️ **Route Optimization**: Efficient delivery tracking
- 📱 **Mobile-Friendly**: Responsive design for mobile courier apps
- 💼 **Profile Management**: Manage availability and delivery history

### For Administrators
- 👥 **User Management**: Manage all platform users
- 📊 **Platform Analytics**: Monitor platform performance
- 🔧 **System Administration**: Platform configuration and maintenance

## 🛠️ Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.4.4** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **MySQL 8.0** - Database
- **JWT** - Token-based authentication
- **MapStruct** - Object mapping
- **Docker** - Containerization
- **Gradle** - Build tool

### Frontend
- **React 19** - UI library
- **TypeScript** - Type-safe JavaScript
- **Vite** - Build tool and dev server
- **React Router Dom** - Client-side routing
- **Zustand** - State management
- **Axios** - HTTP client
- **TailwindCSS** - Utility-first CSS framework
- **Radix UI** - UI components
- **Lucide React** - Icons

## 🏗️ Project Structure

```
DinoBite/
├── dinobite-backend/          # Spring Boot backend application
│   ├── src/main/java/         # Java source code
│   │   └── com/dinobite/
│   │       ├── controller/    # REST API controllers
│   │       ├── service/       # Business logic services
│   │       ├── repository/    # Data access layer
│   │       ├── model/         # Entity models
│   │       ├── dto/           # Data transfer objects
│   │       ├── mapper/        # MapStruct mappers
│   │       └── config/        # Configuration classes
│   ├── src/main/resources/    # Application resources
│   ├── build.gradle.kts       # Build configuration
│   ├── compose.yaml           # Docker compose configuration
│   └── Dockerfile             # Docker container configuration
│
└── dinobite-frontend/         # React frontend application
    ├── src/
    │   ├── components/        # Reusable UI components
    │   ├── pages/            # Application pages
    │   ├── store/            # Zustand state stores
    │   ├── assets/           # Static assets
    │   └── styles/           # CSS stylesheets
    ├── public/               # Public static files
    ├── package.json          # Node.js dependencies
    └── vite.config.ts        # Vite configuration
```

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Node.js 18** or higher
- **Docker** and **Docker Compose**
- **MySQL 8.0** (if running without Docker)

### Installation & Setup

#### 1. Clone the Repository
```bash
git clone https://gitlab.com/bbm384-25/dinobite.git
cd dinobite
```

#### 2. Backend Setup

**Using Docker (Recommended):**
```bash
cd dinobite-backend
docker-compose up -d
```

**Manual Setup:**
```bash
cd dinobite-backend

# Configure database in src/main/resources/application.properties
# Update the following properties:
# spring.datasource.url=jdbc:mysql://localhost:3306/food_order_app
# spring.datasource.username=your_username
# spring.datasource.password=your_password

# Build and run
./gradlew bootRun
```

#### 3. Frontend Setup
```bash
cd dinobite-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

### 🐳 Docker Deployment

The project includes Docker configuration for easy deployment:

```bash
# Start all services
cd dinobite-backend
docker-compose up -d

# This will start:
# - MySQL database on port 3306
# - Spring Boot backend on port 9090
```

### 🌐 API Endpoints

The backend provides RESTful APIs with the following main endpoints:

- **Authentication**: `/api/auth/*` - User registration, login, password reset
- **Users**: `/api/v1/users/*` - User management
- **Restaurants**: `/api/v1/restaurants/*` - Restaurant operations
- **Categories**: `/api/v1/restaurants/{restaurantId}/categories/*` - Menu categories
- **Food**: `/api/v1/restaurants/{restaurantId}/foods/*` - Food items
- **Orders**: `/api/v1/orders/*` - Order management
- **Customers**: `/api/v1/customers/*` - Customer-specific operations
- **Couriers**: `/api/v1/couriers/*` - Courier management
- **Favorites**: `/api/v1/customers/{customerId}/favorites/*` - Customer favorites

## 📱 Usage Examples

### Customer Flow
1. **Register/Login** - Create account or sign in
2. **Browse Restaurants** - Search and filter restaurants
3. **Add to Cart** - Select food items and add to cart
4. **Place Order** - Complete checkout and payment
5. **Track Order** - Monitor order status in real-time
6. **Rate & Review** - Provide feedback after delivery

### Restaurant Flow
1. **Setup Profile** - Complete restaurant information
2. **Create Menu** - Add categories and food items
3. **Manage Orders** - Accept/reject incoming orders
4. **Update Status** - Keep customers informed

### Courier Flow
1. **Register as Courier** - Complete courier profile
2. **Accept Deliveries** - Choose delivery requests
3. **Update Status** - Provide delivery updates
4. **Complete Delivery** - Confirm successful delivery

## 🔧 Configuration

### Backend Configuration
Key configuration files:
- `application.properties` - Database, email, and server settings
- `compose.yaml` - Docker services configuration
- `SecurityConfig.java` - Security and CORS settings

### Frontend Configuration
- `vite.config.ts` - Build and development server settings
- `tailwind.config.js` - TailwindCSS customization
- Store files in `/src/store/` - State management configuration

## 🧪 Testing

The project includes test cases for various functionalities:

### Backend Testing
```bash
cd dinobite-backend
./gradlew test
```

### Frontend Testing
Test cases are documented in `/dinobite-frontend/TestCases/` directory covering:
- User authentication flows
- Order management
- Cart functionality
- Profile management
- Admin operations

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is part of an academic assignment. Please refer to your institution's guidelines for usage and distribution.

## 👥 Team & Support

DinoBite is developed as part of the BBM384 Software Engineering course. For support or questions:

- 📧 Email: dinobitedinodash@gmail.com
- 🐛 Issues: Use the project's issue tracker
- 📚 Documentation: Refer to inline code documentation

## 🚀 Deployment

The application can be deployed using:

- **Development**: Local development servers
- **Production**: Docker containers with production databases
- **Cloud**: Compatible with major cloud providers (AWS, GCP, Azure)

## 📊 Database Schema

The application uses the following main entities:
- **User** - Base user information
- **Customer** - Customer-specific data
- **Restaurant** - Restaurant profiles
- **Courier** - Courier information
- **Food** - Menu items
- **Category** - Food categories
- **Order** - Order transactions
- **OrderItem** - Individual order items
- **Favorite** - Customer favorites
- **Coupon** - Discount coupons
- **Comment** - Reviews and ratings

---

**Built with ❤️ by the DinoBite Team** 🦕
