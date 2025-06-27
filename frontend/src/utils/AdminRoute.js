import { Navigate } from "react-router-dom";
import useAuthStore from "../stores/useAuthStore";

const AdminRoute = ({ children }) => {
  const user = useAuthStore((state) => state.user);
  const isUserAdmin = useAuthStore((state) => state.isUserAdmin());

  // Se não há user logado, redireciona para login
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Se user não é admin, redireciona para profile (ou outra página)
  if (!isUserAdmin) {
    return <Navigate to="/profile" replace />;
  }

  // Se é admin, renderiza o componente
  return children;
};

export default AdminRoute;