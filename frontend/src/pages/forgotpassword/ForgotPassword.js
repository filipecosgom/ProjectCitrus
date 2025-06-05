import React, { useState } from "react";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./ForgotPassword.css"; // Copia Register.css e adapta o nome
import "../../styles/AuthTransition.css";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [language, setLanguage] = useState("en");

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aqui podes adicionar lógica para reset de password
  };

  return (
    <div className="forgot-container d-flex auth-slide-in">
      <div className="forgot-form-container">
        <h1 className="forgot-title">Password Reset</h1>
        <div className="forgot-subtitle">Write your email address</div>
        <form className="forgot-form" onSubmit={handleSubmit}>
          <div className="forgot-fields">
            <div className="forgot-field">
              <label className="forgot-label" htmlFor="forgot-email">
                E-mail address
              </label>
              <input
                id="forgot-email"
                type="email"
                className="forgot-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                autoComplete="username"
                required
              />
            </div>
          </div>
          <button className="main-button" type="submit">
            Reset my password
          </button>
        </form>
      </div>
    </div>
  );
}
