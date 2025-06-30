import { Navigate } from "react-router-dom";
import handleNotification from "../handles/handleNotification";
import useAuthStore from "../stores/useAuthStore";



//Proteção de acesso sem autenticação
const ProtectedRoute = ({ children }) => {
  const { user } = useAuthStore((state) => state);

  if (!user) {
    handleNotification("info", "protectedRoute");
    return <Navigate to="/" />;
  }

  return children;
};

export default ProtectedRoute;