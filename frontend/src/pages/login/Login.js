import React, { useState } from "react";
import { Link } from "react-router-dom";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./Login.css";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [language, setLanguage] = useState("en");
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="login-root d-flex justify-content-center align-items-center">
      <div className="login-container d-flex">
        {/* Branding à esquerda */}
        <div className="login-left d-flex flex-column align-items-center">
          <img
            src={citrusLogo}
            alt="CITRUS Logo"
            className="login-logo"
            width={280}
            style={{ marginTop: 60 }}
          />
          <div className="login-left-title hide-on-tablet-mobile">
            Sign in to your account
          </div>
        </div>
        {/* Formulário à direita */}
        <div className="login-right">
          <h1 className="login-title">Login</h1>
          <div className="login-subtitle hide-on-tablet-mobile">
            Sign-in to your account
          </div>
          <form className="login-form">
            <div className="login-fields">
              <div className="login-field">
                <label className="login-label" htmlFor="login-email">
                  E-mail address
                </label>
                <input
                  id="login-email"
                  type="email"
                  className="login-input"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoComplete="username"
                />
              </div>
              <div className="login-field" style={{ position: "relative" }}>
                <label className="login-label" htmlFor="login-password">
                  Password
                </label>
                <input
                  id="login-password"
                  type={showPassword ? "text" : "password"}
                  className="login-input"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete="current-password"
                />
                <button
                  type="button"
                  className="password-eye-btn"
                  onMouseDown={() => setShowPassword(true)}
                  onMouseUp={() => setShowPassword(false)}
                  onMouseLeave={() => setShowPassword(false)}
                  tabIndex={-1}
                  aria-label={showPassword ? "Hide password" : "Show password"}
                  style={{
                    position: "absolute",
                    right: 12,
                    top: 38,
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    padding: 0,
                    outline: "none",
                  }}
                >
                  {showPassword ? (
                    // Olho aberto (podes substituir por um SVG ou imagem)
                    <FaRegEyeSlash />
                  ) : (
                    // Olho fechado
                    <FaRegEye />
                  )}
                </button>
              </div>
            </div>
            <button className="login-signin-btn" type="submit">
              Sign in
            </button>
          </form>
          <div className="login-forgot-row">
            <Link className="login-forgot-link" to="/forgot-password">
              Forgot your password?
            </Link>
          </div>
          <div className="login-register-row">
            Don’t have an account yet?{" "}
            <Link className="login-register-link" to="/register">
              Join CITRUS today.
            </Link>
          </div>
          {/* Dropdown de idioma dentro da coluna do formulário */}
          <LanguageDropdown language={language} setLanguage={setLanguage} />
        </div>
      </div>
    </div>
  );
}
