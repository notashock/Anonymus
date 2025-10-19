import { Navigate } from "react-router-dom";

function ProtectedRoute({ children }) {
  const user = JSON.parse(localStorage.getItem("user"));

  // If no user, block access
  if (!user || !user.email) {
    return <Navigate to="/" replace />;
  }

  // Otherwise, allow access
  return children;
}

export default ProtectedRoute;
