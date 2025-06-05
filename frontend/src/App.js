import React from 'react';  
import './App.css';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/login/Login";
import Register from "./pages/register/Register"; // importa o Register
import ForgotPassword from "./pages/forgotpassword/ForgotPassword";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />{" "}
        {/* <-- esta linha é essencial */}
        <Route path="/password-reset" element={<ForgotPassword />} />
        <Route path="*" element={<Login />} /> {/* fallback opcional */}
      </Routes>
    </Router>
  );
}

export default App;




