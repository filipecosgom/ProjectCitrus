import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./Register.css"; // You can copy Login.css and adjust as needed
import "../../styles/AuthTransition.css"; // Import the transition styles
import { useIntl } from "react-intl";

export default function Register() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [language, setLanguage] = useState("en");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [animationClass, setAnimationClass] = useState("auth-slide-in");
  const navigate = useNavigate();

  // Handler para animação de saída
  const handleLoginClick = (e) => {
    e.preventDefault();
    setAnimationClass("auth-slide-out");
    setTimeout(() => {
      navigate("/login");
    },200); // igual à duração da animação
  };

  //Internacionalização
  const intl = useIntl();

  return (
  <div className={`register-container d-flex ${animationClass}`}>
    {/* Formulário à esquerda */}
    <div className="register-form-container">
      <h1 className="register-title">{intl.formatMessage({ id: "registerTitle" })}</h1>
      <div className="register-subtitle">{intl.formatMessage({ id: "registerSubtitle" })}</div>
      <form className="register-form">
        <div className="register-fields">
          <div className="register-field">
            <label className="register-label" htmlFor="register-email">
              {intl.formatMessage({ id: "registerFieldEmail" })}
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
              {intl.formatMessage({ id: "registerFieldPassword" })}
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
              aria-label={intl.formatMessage({ id: showPassword ? "registerHidePassword" : "registerShowPassword" })}
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
            <label className="register-label" htmlFor="register-confirm-password">
              {intl.formatMessage({ id: "registerFieldConfirmPassword" })}
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
              aria-label={intl.formatMessage({ id: showConfirmPassword ? "registerHidePassword" : "registerShowPassword" })}
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
          {intl.formatMessage({ id: "registerSubmit" })}
        </button>
      </form>
      <div className="register-bottom-row">
        <div className="register-already-account">
          {intl.formatMessage({ id: "registerAlreadyAccount" })}{" "}
          <Link
            className="register-login-link"
            to="/login"
            onClick={handleLoginClick}
          >
            {intl.formatMessage({ id: "registerLogin" })}
          </Link>
        </div>
        <div className="register-language-dropdown">
          <LanguageDropdown language={language} setLanguage={setLanguage} />
        </div>
      </div>
    </div>
    {/* Branding à direita */}
    <div className="register-logo-container d-flex flex-grow-1 flex-column align-items-center">
      <img
        src={citrusLogo}
        alt={intl.formatMessage({ id: "registerLogo" })}
        className="register-logo"
        width={280}
      />
      <div className="register-logo-container-title hide-on-mobile">
        {intl.formatMessage({ id: "registerLogoContainerTitle" })}
      </div>
    </div>
  </div>
);
}
