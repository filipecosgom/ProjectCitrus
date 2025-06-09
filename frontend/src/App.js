import React from 'react';  
import './App.css';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/login/Login";
import Register from "./pages/register/Register";
import { IntlProvider } from "react-intl";
import languages from "./utils/translations";
import useLocaleStore from './stores/useLocaleStore';
import ForgotPassword from "./pages/forgotpassword/ForgotPassword";
import { ToastContainer } from 'react-toastify';
import AccountActivation from "./pages/landing/AccountActivation";

function App() {
   const locale = useLocaleStore((state) => state.locale);
   
  return (
    <IntlProvider locale={locale} messages={languages[locale]}>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          {/* <-- esta linha é essencial */}
          <Route path="/password-reset" element={<ForgotPassword />} />
          <Route path="/account-activation" element={<AccountActivation />} />
          <Route path="*" element={<Login />} /> {/* fallback opcional */}
        </Routes>
      </Router>
      <ToastContainer limit={3} />
    </IntlProvider>
  );
}

export default App;