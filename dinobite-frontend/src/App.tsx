import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from './pages/Home';
import Restaurant from './pages/Restaurant';
import { Header } from './components/Header';
import Login2 from "./pages/Login2";
import Register from "./pages/Register";
import Profile from "./pages/Profile";
import RestaurantProfile from "./pages/RestaurantProfile";
import CourierProfile from "./pages/CourierProfile";
import ProtectedRoute from "./components/ProtectedRoute";
import SpinWheelCss from './pages/SpinWheel';
import AdminUserPanel from "./pages/AdminUserPanel";
import ChatbotPage from "./pages/ChatbotPage";

const App: React.FC = () => {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/restaurants/:id" element={<Restaurant />} />
        <Route path="/login" element={<Login2 />} />
        <Route path="/register" element={<Register />} />
        <Route path="/spin-wheel" element={<SpinWheelCss />} />
        <Route path="/chatbot" element={<ChatbotPage />} />

        <Route
          path="/profile"
          element={
            <ProtectedRoute allowedUserTypes={["CUSTOMER"]}>
              <Profile />
            </ProtectedRoute>
          }
        />

        <Route
          path="/restaurant-dashboard"
          element={
            <ProtectedRoute allowedUserTypes={["RESTAURANT"]}>
              <RestaurantProfile />
            </ProtectedRoute>
          }
        />

        <Route
          path="/courier-profile/:id"
          element={
            <ProtectedRoute allowedUserTypes={["COURIER"]} idParamCheck={true}>
              <CourierProfile />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin-dashboard"
          element={
            <ProtectedRoute allowedUserTypes={["ADMIN"]}>
              <AdminUserPanel />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
};

export default App;
