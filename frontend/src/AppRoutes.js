import { useEffect, useState } from "react";
import { useLocation, useNavigate, Routes, Route } from "react-router-dom";
import useAuthStore from "./stores/useAuthStore";
import useLocaleStore from "./stores/useLocaleStore";
import Menu from "./components/menu/Menu";
import Header from "./components/header/Header";
import Spinner from "./components/spinner/spinner";
import Login from "./pages/login/Login";
import Register from "./pages/register/Register";
import Profile from "./pages/profile/Profile";
import Users from "./pages/users/Users";
import AccountActivation from "./pages/landing/AccountActivation";
import ActivatedAccount from "./pages/landing/ActivatedAccount";
import PasswordReset from "./pages/passwordReset/PasswordReset";
import NotFound404 from "./pages/404/404NotFound";
import ProtectedRoute from "./utils/ProtectedRoute";

function AppRoutes() {
  const [hydrating, setHydrating] = useState(true);
  const location = useLocation();
  const locale = useLocaleStore((state) => state.locale);
  const setLocale = useLocaleStore((state) => state.setLocale);
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

  // Lista de todas as rotas conhecidas (as que tens no <Routes>)
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
    "/users",
  ];

  // Se não for uma rota conhecida, é 404
  const is404 = !knownRoutes.includes(location.pathname);

  const showHeader = !hideHeaderRoutes.includes(location.pathname) && !is404;

  // Dados de exemplo para o Header
  const user = {
    name: "Teresa Matos",
    email: "teresa.matos@gmail.com",
    avatarUrl: "",
    unreadMessages: 2,
    unreadNotifications: 1,
  };

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
      // Now, after trying to fetch, check if the user is set
      if (useAuthStore.getState().user) {
        if (useAuthStore.getState().user.accountState === "INCOMPLETE") {
          // If the user is logged in but their account is incomplete, redirect to profile edit
          navigate("/profile?id=" + useAuthStore.getState().user.id);
        }
      }
      setHydrating(false);
    };
    hydrate();
    // eslint-disable-next-line
  }, []);

  if (hydrating) return <Spinner />;

  return (
    <>
      {/* Mostra o Header em todas as páginas exceto as do array acima, mas permite testar em /header */}
      {(showHeader || location.pathname === "/header") && (
        <>
          <Menu language={locale} setLanguage={setLocale} />
          <Header
            userName={user.name}
            userEmail={user.email}
            avatarUrl={user.avatarUrl}
            unreadMessages={user.unreadMessages}
            unreadNotifications={user.unreadNotifications}
          />
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
          path="/offcanvas-forgot-password"
          element={<div>OffcanvasForgotPassword</div>}
        />
        <Route path="/account-activation" element={<AccountActivation />} />
        <Route path="/activate" element={<ActivatedAccount />} />
        <Route path="/password-reset" element={<PasswordReset />} />
        <Route
          path="/menu"
          element={<Menu language={locale} setLanguage={setLocale} />}
        />{" "}
        {/* Só mostra o Menu */}
        <Route path="/header" element={<div />} /> {/* Só mostra o Header */}
        <Route path="*" element={<NotFound404 />} />
      </Routes>
    </>
  );
}

export default AppRoutes;
