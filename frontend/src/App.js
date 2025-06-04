import React from 'react';  
import './App.css';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/login2/Login2";
import Register from "./pages/register/Register"; // importa o Register

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} /> {/* <-- esta linha é essencial */}
        {/* ...outras rotas... */}
        <Route path="*" element={<Login />} /> {/* fallback opcional */}
      </Routes>
    </Router>
  );
}

export default App;




