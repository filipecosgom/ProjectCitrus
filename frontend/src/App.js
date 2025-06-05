import React from 'react';  
import './App.css';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/login/Login";
import Register from "./pages/register/Register"; // importa o Register
import { IntlProvider } from "react-intl";
import languages from "./utils/translations";
import useLocaleStore from './stores/useLocaleStore';
import ForgotPassword from "./pages/forgotpassword/ForgotPassword";

function App() {
   const locale = useLocaleStore((state) => state.locale);
   
  return (
    <IntlProvider locale={locale} messages={languages[locale]}>
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />{" "}
        {/* <-- esta linha é essencial */}
        <Route path="/password-reset" element={<ForgotPassword />} />
        <Route path="*" element={<Login />} /> {/* fallback opcional */}
      </Routes>
    </Router>
    </IntlProvider>
  );
}

export default App;




