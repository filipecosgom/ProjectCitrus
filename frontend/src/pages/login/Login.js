/**
 * @file Login.js
 * @module Login
 * @description React page component for user authentication and login.
 * Provides a login form with email, password, and two-factor authentication fields.
 * Integrates with backend authentication, notification system, and supports internationalization.
 * Includes password visibility toggle, forgot password modal, and two-factor authentication help.
 * Navigates users to dashboard or profile upon successful login.
 * @author Project Citrus Team
 */

/**
 * Login Page Component.
 *
 * Renders the user login interface, including:
 * - Logo and language selection dropdown
 * - Login form with email, password, and two-factor authentication fields
 * - Validation and error handling for all inputs
 * - Password visibility toggle and forgot password modal
 * - Two-factor authentication help modal
 * - Submission to backend for authentication
 * - Success and error notifications
 * - Internationalization of all labels and messages
 * - Navigation to dashboard or profile after login
 *
 * @returns {JSX.Element} The rendered login page
 *
 * @example
 *
 * ```jsx
 * <Login />
 * ```
 */

import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import OffcanvasForgotPassword from "../../pages/forgotpassword/OffcanvasForgotPassword";
import OffCanvasTwoFactor from "../../components/twoFactorOffCavas/OffCanvasTwoFactor";
import "./Login.css";
import "../../styles/AuthTransition.css";
import { useTranslation } from "react-i18next";
import handleLogin from "../../handles/handleLogin";
import useAuthStore from "../../stores/useAuthStore";
import handleNotification from "../../handles/handleNotification";
import useLocaleStore from "../../stores/useLocaleStore";

export default function Login() {
  const { t, i18n } = useTranslation();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, touchedFields, isSubmitted },
    trigger,
  } = useForm();
  useEffect(() => {
    trigger();
  }, [i18n.language, trigger]);
  const [showPassword, setShowPassword] = useState(false);
  const [showForgot, setShowForgot] = useState(false);
  const [showAuth, setShowAuth] = useState(false);
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const isUserAdmin = useAuthStore((state) => state.isUserAdmin());
  const isUserManager = useAuthStore((state) => state.isUserManager());

  const onSubmit = async (loginData) => {
    const userData = {
      email: loginData.email,
      password: loginData.password,
      authenticationCode: loginData.authenticationCode,
    };
    try {
      const success = await handleLogin(userData);
      reset();
    } catch (error) {
      reset();
    }
  };

  useEffect(() => {
    if (user) {
      handleNotification("success", "welcomeMessage", { name: user.name });
      if (isUserAdmin) {
        navigate("/dashboard");
      } else {
        navigate("/profile?id=" + user.id);
      }
    }
  }, [user, isUserAdmin, navigate]);

  return (
    <div className="login-container">
      {/* DIVISÃO DO LOGO */}
      <div className="login-logo-container">
        <img src={citrusLogo} alt={t("loginLogo")} className="login-logo" />
        <div className="logo-undertitle">{t("loginSubtitle")}</div>
      </div>

      {/* FORMULÁRIO */}
      <div className="loginform-container">
        <div className="login-title-group">
          <h1 className="login-title">{t("loginTitle")}</h1>
          <div className="login-subtitle">{t("loginSubtitle")}</div>
        </div>
        <form
          className="login-form"
          id="login-form"
          onSubmit={handleSubmit(onSubmit)}
        >
          <div className="login-fields">
            <div className="login-field">
              <div className="login-labelAndError">
                <label className="login-label" htmlFor="login-email">
                  {t("loginFieldEmail")}
                </label>
                {errors.email && isSubmitted && (
                  <span className="error-message">{errors.email.message}</span>
                )}
              </div>

              <input
                id="login-email"
                className={`login-input`}
                {...register("email", {
                  required: t("loginErrorEmailMissing"),
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: t("loginErrorEmailInvalid"),
                  },
                })}
              />
            </div>

            <div className="login-field">
              <div className="login-labelAndError">
                <label className="login-label" htmlFor="login-password">
                  {t("loginFieldPassword")}
                </label>
                {errors.password && isSubmitted && (
                  <span className="error-message">{errors.password.message}</span>
                )}
              </div>

              <div className="password-input-wrapper">
                <input
                  id="login-password"
                  type={showPassword ? "text" : "password"}
                  className="login-input"
                  {...register("password", {
                    required: t("loginErrorPasswordMissing"),
                  })}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={t(
                    showPassword
                      ? "registerHidePassword"
                      : "registerShowPassword"
                  )}
                >
                  {showPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                </button>
              </div>
              <div className="login-password-row">
                <button
                  className="login-help-link"
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
                  {t("loginForgotPassword")}
                </button>
              </div>
            </div>

            <div className="login-field">
              <div className="login-labelAndError">
                <label className="login-label" htmlFor="login-TwoFAuth">
                  {t("loginFieldTwoFAuth")}
                </label>
                {errors.authenticationCode && isSubmitted && (
                  <span className="error-message">{errors.authenticationCode.message}</span>
                )}
              </div>
              <input
                id="login-authenticationCode"
                className="login-input"
                {...register("authenticationCode", {
                  required: t("loginErrorAuthenticationCodeMissing"),
                  pattern: {
                    value: /^\d{6}$/, // Ensures exactly 6 digits
                    message: t("loginErrorAuthenticationCodeInvalid"),
                  },
                })}
              />

              <div className="login-forgot-row">
                <button
                  className="login-help-link"
                  type="button"
                  style={{
                    background: "none",
                    border: "none",
                    padding: 0,
                    color: "#424359",
                    cursor: "pointer",
                  }}
                  onClick={(e) => {
                    e.stopPropagation();
                    setShowAuth(true);
                  }}
                >
                  {t("loginHelpTwoFAuth")}
                </button>
              </div>
            </div>
          </div>

          <button className="main-button" type="submit">
            {t("loginSubmit")}
          </button>
        </form>
        <div className="login-register-row">
          {t("loginRegisterPrompt")}{" "}
          <Link className="login-register-link" to="/register">
            {t("loginRegister")}
          </Link>
        </div>
        <div className="login-language-dropdown">
          {/* Dropdown de idioma dentro da coluna do formulário */}
          <LanguageDropdown />
        </div>
      </div>
      <OffcanvasForgotPassword
        show={showForgot}
        onClose={() => setShowForgot(false)}
      />
      <OffCanvasTwoFactor show={showAuth} onClose={() => setShowAuth(false)} />
    </div>
  );
}
