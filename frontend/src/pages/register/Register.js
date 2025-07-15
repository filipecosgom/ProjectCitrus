import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./Register.css";
import "../../styles/AuthTransition.css";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import handleRegistration from "../../handles/handleRegistration";
import handleNotification from "../../handles/handleNotification";
import AccountActivation from "../landing/AccountActivation";
import useLocaleStore from "../../stores/useLocaleStore";

export default function Register() {
  const { t } = useTranslation();
  //Modal de Registo de novo utilizador
  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors },
  } = useForm();
  const [language, setLanguage] = useState("en");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [animationClass, setAnimationClass] = useState("auth-slide-in");
  const [showActivation, setShowActivation] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState("");
  const strongPasswordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{12,}$/;
  const navigate = useNavigate();
  const lang = useLocaleStore((state) => state.locale);

  // Handler para animação de saída
  const handleLoginClick = (e) => {
    e.preventDefault();
    setAnimationClass("auth-slide-out");
    setTimeout(() => {
      navigate("/login");
    }, 200); // igual à duração da animação
  };

  const onSubmit = async (registerData) => {
    const newUser = {
      email: registerData.email,
      password: registerData.password,
    };
    try {
      const success = await handleRegistration(newUser, lang);

      if (success) {
        setRegisteredEmail(registerData.email);
        setShowActivation(true);
        reset();
      } else {
        reset();
        console.warn("Registration failed, not updating UI.");
      }
    } catch (error) {
      console.error("Unexpected registration error:", error);
      handleNotification("error", "errorUnexpected");
    }
  };

  return (
    <div className={`register-container d-flex ${animationClass}`}>
      {/* Formulário à esquerda */}
      <div className="register-form-container">
        {showActivation ? (
          <AccountActivation email={registeredEmail} />
        ) : (
          <>
            <h1 className="register-title">{t("registerTitle")}</h1>
            <div className="register-subtitle">{t("registerSubtitle")}</div>
            <form
              className="register-form"
              id="register-form"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="register-field">
                <div className="register-labelAndError">
                  <label className="register-label" htmlFor="register-email">
                    Email
                  </label>
                  <span className="error-message">
                    {errors.email ? errors.email.message : "\u00A0"}
                  </span>
                </div>
              </div>
              <input
                id="register-email"
                className={`register-input`}
                {...register("email", {
                  required: t("registerErrorEmailMissing"),
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: t("registerErrorEmailInvalid"),
                  },
                })}
              />

              <div className="register-field" style={{ position: "relative" }}>
                <div className="password-input-container">
                  <div className="register-labelAndError">
                    <label
                      className="register-label"
                      htmlFor="register-password"
                    >
                      {t("registerFieldPassword")}
                    </label>
                    <span className="error-message">
                      {errors.password ? errors.password.message : "\u00A0"}
                    </span>
                  </div>

                  <div className="password-input-wrapper">
                    <input
                      id="register-password"
                      type={showPassword ? "text" : "password"}
                      className="register-input"
                      {...register("password", {
                        required: t("registerErrorPasswordMissing"),
                        pattern: {
                          value: strongPasswordPattern,
                          message: t("registerErrorPasswordWeak"),
                        },
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
                </div>
                <div className="password-input-container">
                  <div className="register-labelAndError">
                    <label
                      className="register-label"
                      htmlFor="register-confirm-password"
                    >
                      {t("registerFieldConfirmPassword")}
                    </label>
                    <span className="error-message">
                      {errors.passwordConfirm
                        ? errors.passwordConfirm.message
                        : "\u00A0"}
                    </span>
                  </div>

                  <div className="password-input-wrapper">
                    <input
                      id="register-confirm-password"
                      type={showConfirmPassword ? "text" : "password"}
                      className="register-input"
                      {...register("passwordConfirm", {
                        required: t("registerErrorConfirmPasswordMissing"),
                        validate: {
                          isConfirmed: (value) =>
                            value === watch("password") ||
                            t("registerErrorPasswordMismatch"),
                        },
                      })}
                    />
                    <button
                      type="button"
                      className="password-toggle-btn"
                      onClick={() =>
                        setShowConfirmPassword(!showConfirmPassword)
                      }
                      aria-label={t(
                        showConfirmPassword
                          ? "registerHidePassword"
                          : "registerShowPassword"
                      )}
                    >
                      {showConfirmPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                    </button>
                  </div>
                </div>
              </div>
              <button className="main-button" type="submit">
                {t("registerSubmit")}
              </button>
            </form>
            <div className="register-bottom-row">
              <div className="register-already-account">
                {t("registerAlreadyAccount")}{" "}
                <Link
                  className="register-login-link"
                  to="/login"
                  onClick={handleLoginClick}
                >
                  {t("registerLogin")}
                </Link>
              </div>
              <div className="register-language-dropdown">
                <LanguageDropdown
                  language={language}
                  setLanguage={setLanguage}
                />
              </div>
            </div>
          </>
        )}
      </div>
      {/* Branding à direita */}
      <div className="register-logo-container d-flex flex-grow-1 flex-column align-items-center">
        <img
          src={citrusLogo}
          alt={t("registerLogo")}
          className="register-logo"
          width={280}
        />
        <div className="register-logo-container-title hide-on-mobile">
          {t("registerLogoContainerTitle")}
        </div>
      </div>
    </div>
  );
}
