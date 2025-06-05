import React, { useState } from "react";
import { Link } from "react-router-dom";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import OffcanvasForgotPassword from "../../pages/forgotpassword/OffcanvasForgotPassword";
import "./Login.css";
import "../../styles/AuthTransition.css";
import { useIntl } from "react-intl";
import useLocaleStore from "../../stores/useLocaleStore";


export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [language, setLanguage] = useState("en");
  const [showPassword, setShowPassword] = useState(false);
  const [showForgot, setShowForgot] = useState(false);

  //Internacionalização
    const intl = useIntl();
    const locale = useLocaleStore((state) => state.locale);

  return (
  <div className="login-container">
    {/* DIVISÃO DO LOGO */}
    <div className="login-logo-container">
      <img src={citrusLogo} alt={intl.formatMessage({ id: "loginLogo" })} className="login-logo" />
      <div className="logo-undertitle">
        {intl.formatMessage({ id: "loginSubtitle" })}
      </div>
    </div>

    {/* FORMULÁRIO */}
    <div className="loginform-container">
      <div className="login-title-group">
        <h1 className="login-title">{intl.formatMessage({ id: "loginTitle" })}</h1>
        <div className="login-subtitle">{intl.formatMessage({ id: "loginSubtitle" })}</div>
      </div>
      <form className="login-form">
        <div className="login-fields">
          <div className="login-field">
            <label className="login-label" htmlFor="login-email">
              {intl.formatMessage({ id: "loginFieldEmail" })}
            </label>
            <input
              id="login-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="username"
            />
          </div>
          <div className="login-field" style={{ position: "relative" }}>
            <label className="login-label" htmlFor="login-password">
              {intl.formatMessage({ id: "loginFieldPassword" })}
            </label>
            <input
              id="login-password"
              type={showPassword ? "text" : "password"}
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
              aria-label={intl.formatMessage({ id: showPassword ? "loginHidePassword" : "loginShowPassword" })}
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
        </div>
        <div className="login-forgot-row">
          <button
            className="login-forgot-link"
            type="button"
            style={{
              background: "none",
              border: "none",
              padding: 0,
              color: "#424359",
              cursor: "pointer",
            }}
            onClick={() => setShowForgot(true)}
          >
            {intl.formatMessage({ id: "loginForgotPassword" })}
          </button>
        </div>
        <button className="main-button" type="submit">
          {intl.formatMessage({ id: "loginSubmit" })}
        </button>
      </form>
      <div className="login-register-row">
        {intl.formatMessage({ id: "loginRegisterPrompt" })}{" "}
        <Link className="login-register-link" to="/register">
          {intl.formatMessage({ id: "loginRegister" })}
        </Link>
      </div>
      <div className="login-language-dropdown">
        {/* Dropdown de idioma dentro da coluna do formulário */}
        <LanguageDropdown language={language} setLanguage={setLanguage} />
      </div>
    </div>
    <OffcanvasForgotPassword show={showForgot} onClose={() => setShowForgot(false)} />
  </div>
);
}