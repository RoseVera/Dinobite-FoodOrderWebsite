import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useUserStore } from "@/store/UserStore";

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedUserTypes: string[];
  idParamCheck?: boolean;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, allowedUserTypes, idParamCheck = false }) => {
  const user = useUserStore((state) => state.user);
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (!allowedUserTypes.includes(user.userType)) {
    return <Navigate to="/" replace />;
  }

  if (idParamCheck && location.pathname.includes("courier-profile")) {
    const urlParts = location.pathname.split("/");
    const routeId = Number(urlParts[urlParts.length - 1]);

    if (user.courierId !== routeId) {
      return <Navigate to="/" replace />;
    }
  }

  return <>{children}</>;
};

export default ProtectedRoute;
