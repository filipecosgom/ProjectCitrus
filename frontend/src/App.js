import React from "react";
import "./App.css";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  useLocation,
} from "react-router-dom";
import Login from "./pages/login/Login";
import Register from "./pages/register/Register";
import { IntlProvider } from "react-intl";
import languages from "./utils/translations";
import useLocaleStore from "./stores/useLocaleStore";
import ForgotPassword from "./pages/forgotpassword/ForgotPassword";
import { ToastContainer } from "react-toastify";
import AccountActivation from "./pages/landing/AccountActivation";
import ActivatedAccount from "./pages/landing/ActivatedAccount";
import Header from "./components/header/Header";
import NotFound404 from "./pages/404/404NotFound";

// Componente para gerir o layout e mostrar/esconder o Header
function AppRoutes() {
  const location = useLocation();
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

  return (
    <>
      {/* Mostra o Header em todas as páginas exceto as do array acima, mas permite testar em /header */}
      {(showHeader || location.pathname === "/header") && (
        <Header
          userName={user.name}
          userEmail={user.email}
          avatarUrl={user.avatarUrl}
          unreadMessages={user.unreadMessages}
          unreadNotifications={user.unreadNotifications}
        />
      )}
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/password-reset" element={<ForgotPassword />} />
        <Route
          path="/offcanvas-forgot-password"
          element={<div>OffcanvasForgotPassword</div>}
        />
        <Route path="/account-activation" element={<AccountActivation />} />
        <Route path="/activated-account" element={<ActivatedAccount />} />
        <Route path="/header" element={<div />} /> {/* Só mostra o Header */}
        <Route path="*" element={<NotFound404 />} />
      </Routes>
    </>
  );
}

function App() {
  const locale = useLocaleStore((state) => state.locale);

  return (
    <IntlProvider locale={locale} messages={languages[locale]}>
      <Router>
        <AppRoutes />
      </Router>
      <ToastContainer limit={3} />
    </IntlProvider>
  );
}

export default App;
