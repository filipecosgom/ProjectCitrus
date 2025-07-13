import { useEffect, useState } from "react";
import { useLocation, useNavigate, Routes, Route } from "react-router-dom";
import useAuthStore from "./stores/useAuthStore";
import Menu from "./components/menu/Menu";
import Header from "./components/header/Header";
import Spinner from "./components/spinner/Spinner";
import Login from "./pages/login/Login";
import Register from "./pages/register/Register";
import Profile from "./pages/profile/Profile";
import Users from "./pages/users/Users";
import Appraisals from "./pages/appraisals/Appraisals";
import Cycles from "./pages/cycles/Cycles";
import Settings from "./pages/settings/Settings";
import Chat from "./pages/messageCenter/MessageCenter";
import Courses from "./pages/courses/Courses";
import AccountActivation from "./pages/landing/AccountActivation";
import ActivatedAccount from "./pages/landing/ActivatedAccount";
import PasswordReset from "./pages/passwordReset/PasswordReset";
import Notifications from "./pages/notifications/Notifications";
import NotFound404 from "./pages/404/404NotFound";
import ProtectedRoute from "./utils/ProtectedRoute";
import AdminRoute from "./utils/AdminRoute";
import { ToastContainer } from "react-toastify";
import Dashboard from "./pages/dashboard/Dashboard";

function AppRoutes({ currentLocale, setLocale }) {
  const [hydrating, setHydrating] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();

  const hideHeaderRoutes = [
    "/",
    "/login",
    "/register",
    "/password-reset",
    "/offcanvas-forgot-password",
    "/account-activation",
    "/activated-account",
    "/header",
  ];

  // ✅ ATUALIZAR: Lista de todas as rotas conhecidas
  const knownRoutes = [
    "/",
    "/login",
    "/register",
    "/password-reset",
    "/offcanvas-forgot-password",
    "/account-activation",
    "/activated-account",
    "/header",
    "/profile",
    "/appraisals",
    "/users",
    "/cycles",
    "/settings",
    "/messages",
    "/courses",
    "/notifications",
    "/dashboard"
  ];

  const is404 = !knownRoutes.includes(location.pathname);
  const showHeader = !hideHeaderRoutes.includes(location.pathname) && !is404;

  useEffect(() => {
    const hydrate = async () => {
      if (!useAuthStore.getState().user) {
        const response = await useAuthStore
          .getState()
          .fetchAndSetUserInformation();
        if (!useAuthStore.getState().avatar && response.data) {
          if (response.data.user.hasAvatar) {
            await useAuthStore.getState().fetchAndSetUserAvatar();
          }
        }
      }
      if (useAuthStore.getState().user) {
        if (useAuthStore.getState().user.accountState === "INCOMPLETE") {
          navigate("/profile?id=" + useAuthStore.getState().user.id);
        }
      }
      setHydrating(false);
    };
    hydrate();
  }, []);

  if (hydrating) return <Spinner />;

  return (
    <>
      {(showHeader || location.pathname === "/header") && (
        <>
          <Menu language={currentLocale} setLanguage={setLocale} />
          <Header />
        </>
      )}
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users"
          element={
            <ProtectedRoute>
              <Users />
            </ProtectedRoute>
          }
        />
        <Route
          path="/appraisals"
          element={
            <ProtectedRoute>
              <Appraisals />
            </ProtectedRoute>
          }
        />
        <Route
          path="/cycles"
          element={
            <AdminRoute>
              <Cycles />
            </AdminRoute>
          }
        />
        <Route
          path="/settings"
          element={
            <AdminRoute>
              <Settings />
            </AdminRoute>
          }
        />
        <Route
          path="/messages"
          element={
            <ProtectedRoute>
              <Chat />
            </ProtectedRoute>
          }
        />
        <Route
          path="/offcanvas-forgot-password"
          element={<div>OffcanvasForgotPassword</div>}
        />
        <Route path="/account-activation" element={<AccountActivation />} />
        <Route path="/activate" element={<ActivatedAccount />} />
        <Route path="/password-reset" element={<PasswordReset />} />
        <Route
          path="/menu"
          element={<Menu language={currentLocale} setLanguage={setLocale} />}
        />
        <Route path="/header" element={<div />} />
        <Route
          path="/courses"
          element={
            <ProtectedRoute>
              <Courses />
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <AdminRoute>
              <Dashboard />
            </AdminRoute>
          }
        />
        <Route
          path="/notifications"
          element={
            <ProtectedRoute>
              <Notifications />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<NotFound404 />} />
      </Routes>
      <ToastContainer limit={3} />
    </>
  );
}

export default AppRoutes;
