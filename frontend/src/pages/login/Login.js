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
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();
  const [showPassword, setShowPassword] = useState(false);
  const [showForgot, setShowForgot] = useState(false);
  const [showAuth, setShowAuth] = useState(false);
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const isUserAdmin = useAuthStore((state) => state.isUserAdmin());
  const isUserManager = useAuthStore((state) => state.isUserManager());
  const locale = useLocaleStore((state) => state.locale); // Get current locale from store

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
                <span className="error-message">
                  {errors.email ? errors.email.message : "\u00A0"}
                </span>
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
                <span className="error-message">
                  {errors.password ? errors.password.message : "\u00A0"}
                </span>
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
                <span className="error-message">
                  {errors.authenticationCode
                    ? errors.authenticationCode.message
                    : "\u00A0"}
                </span>
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
