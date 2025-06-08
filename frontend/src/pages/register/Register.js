import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./Register.css"; // You can copy Login.css and adjust as needed
import "../../styles/AuthTransition.css"; // Import the transition styles
import { useIntl } from "react-intl";
import { useForm } from "react-hook-form";
import handleRegistration from "../../handles/handleRegistration";
import AccountActivation from "../landing/AccountActivation";

export default function Register() {
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

  // as duas linhas abaixo adicionam estado para saber se deve mostrar o formulário ou o AccountActivation.
  const [showActivation, setShowActivation] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState("");
  const strongPasswordPattern =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{12,}$/;

  const navigate = useNavigate();
  //Internacionalização
  const intl = useIntl();

  // Handler para animação de saída
  const handleLoginClick = (e) => {
    e.preventDefault();
    setAnimationClass("auth-slide-out");
    setTimeout(() => {
      navigate("/login");
    }, 200); // igual à duração da animação
  };

  //Submissão do formulário (admin é sempre falso)
  const onSubmit = async (registerData) => {
    const newUser = {
      email: registerData.email,
      password: registerData.password,
    };

    // Aqui podes chamar a API de registo se quiseres
    setRegisteredEmail(registerData.email);
    setShowActivation(true);
    reset();
  };

  return (
    <div className={`register-container d-flex ${animationClass}`}>
      {/* Formulário à esquerda */}
      <div className="register-form-container">
        {showActivation ? (
          <AccountActivation email={registeredEmail} />
        ) : (
          <>
            <h1 className="register-title">
              {intl.formatMessage({ id: "registerTitle" })}
            </h1>
            <div className="register-subtitle">
              {intl.formatMessage({ id: "registerSubtitle" })}
            </div>
            <form
              className="register-form"
              id="register-form"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="register-fields">
                <div className="register-field">
                  <label className="register-label" htmlFor="register-email">
                    Email
                  </label>
                  <input
                    id="register-email"
                    className={`register-input`}
                    {...register("email", {
                      required: intl.formatMessage({
                        id: "registerErrorEmailMissing",
                      }),
                      pattern: {
                        value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                        message: intl.formatMessage({
                          id: "registerErrorEmailInvalid",
                        }),
                      },
                    })}
                  />
                  <span className="error-message">
                    {errors.email ? errors.email.message : "\u00A0"}
                  </span>
                </div>

                <div
                  className="register-field"
                  style={{ position: "relative" }}
                >
                  <label className="register-label" htmlFor="register-password">
                    {intl.formatMessage({ id: "registerFieldPassword" })}
                  </label>
                  <input
                    id="register-password"
                    type={showPassword ? "text" : "password"}
                    className="register-input"
                    {...register("password", {
                      required: intl.formatMessage({
                        id: "registerErrorPasswordMissing",
                      }),
                      pattern: {
                        value: strongPasswordPattern,
                        message: intl.formatMessage({
                          id: "registerErrorPasswordWeak",
                        }),
                      },
                    })}
                  />
                  <button
                    type="button"
                    className="password-eye-btn"
                    onMouseDown={() => setShowPassword(true)}
                    onMouseUp={() => setShowPassword(false)}
                    onMouseLeave={() => setShowPassword(false)}
                    tabIndex={-1}
                    aria-label={intl.formatMessage({
                      id: showPassword
                        ? "registerHidePassword"
                        : "registerShowPassword",
                    })}
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
                  <span className="error-message">
                    {errors.password ? errors.password.message : "\u00A0"}
                  </span>
                </div>
                <div
                  className="register-field"
                  style={{ position: "relative" }}
                >
                  <label
                    className="register-label"
                    htmlFor="register-confirm-password"
                  >
                    {intl.formatMessage({ id: "registerFieldConfirmPassword" })}
                  </label>
                  <input
                    id="register-confirm-password"
                    type={showConfirmPassword ? "text" : "password"}
                    className="register-input"
                    {...register("passwordConfirm", {
                      required: intl.formatMessage({
                        id: "registerErrorConfirmPasswordMissing",
                      }),
                      validate: {
                        isConfirmed: (value) =>
                          value === watch("password") ||
                          intl.formatMessage({
                            id: "registerErrorPasswordMismatch",
                          }),
                      },
                    })}
                  />
                  <span className="error-message">
                    {errors.passwordConfirm
                      ? errors.passwordConfirm.message
                      : "\u00A0"}
                  </span>
                  <button
                    type="button"
                    className="password-eye-btn"
                    onMouseDown={() => setShowConfirmPassword(true)}
                    onMouseUp={() => setShowConfirmPassword(false)}
                    onMouseLeave={() => setShowConfirmPassword(false)}
                    tabIndex={-1}
                    aria-label={intl.formatMessage({
                      id: showConfirmPassword
                        ? "registerHidePassword"
                        : "registerShowPassword",
                    })}
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
