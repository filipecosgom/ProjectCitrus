import React, { useState } from "react";
import { Link } from "react-router-dom";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./Register.css"; // You can copy Login.css and adjust as needed
import "../../styles/AuthTransition.css"; // Import the transition styles

export default function Register() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [language, setLanguage] = useState("en");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  return (
    <div className="register-container d-flex auth-slide-in">
      {/* Formulário à esquerda */}
      <div className="register-left">
        <h1 className="register-title">Welcome to CITRUS</h1>
        <div className="register-subtitle">Register Your Account</div>
        <form className="register-form">
          <div className="register-fields">
            <div className="register-field">
              <label className="register-label" htmlFor="register-email">
                E-mail address
              </label>
              <input
                id="register-email"
                type="email"
                className="register-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                autoComplete="username"
              />
            </div>
            <div className="register-field" style={{ position: "relative" }}>
              <label className="register-label" htmlFor="register-password">
                Password
              </label>
              <input
                id="register-password"
                type={showPassword ? "text" : "password"}
                className="register-input"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="new-password"
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
                {showPassword ? <FaRegEyeSlash /> : <FaRegEye />}
              </button>
            </div>
            <div className="register-field" style={{ position: "relative" }}>
              <label
                className="register-label"
                htmlFor="register-confirm-password"
              >
                Confirm Password
              </label>
              <input
                id="register-confirm-password"
                type={showConfirmPassword ? "text" : "password"}
                className="register-input"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                autoComplete="new-password"
              />
              <button
                type="button"
                className="password-eye-btn"
                onMouseDown={() => setShowConfirmPassword(true)}
                onMouseUp={() => setShowConfirmPassword(false)}
                onMouseLeave={() => setShowConfirmPassword(false)}
                tabIndex={-1}
                aria-label={
                  showConfirmPassword ? "Hide password" : "Show password"
                }
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
                {showConfirmPassword ? <FaRegEyeSlash /> : <FaRegEye />}
              </button>
            </div>
          </div>
          <button className="main-button" type="submit">
            Create account
          </button>
        </form>
        <div className="register-bottom-row">
          <div className="register-already-account">
            Already have an account?{" "}
            <Link className="register-login-link" to="/login">
              Log in.
            </Link>
          </div>
          <div className="register-language-dropdown">
            <LanguageDropdown language={language} setLanguage={setLanguage} />
          </div>
        </div>
      </div>
      {/* Branding à direita */}
      <div className="register-right d-flex flex-column align-items-center">
        <img
          src={citrusLogo}
          alt="CITRUS Logo"
          className="register-logo"
          width={280}
        />
        <div className="register-right-title hide-on-tablet-mobile">
          Join the CITRUS community!
        </div>
      </div>
    </div>
  );
}