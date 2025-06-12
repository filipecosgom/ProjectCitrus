import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import OffcanvasForgotPassword from "../../pages/forgotpassword/OffcanvasForgotPassword";
import "./Login.css";
import "../../styles/AuthTransition.css";
import { useIntl } from "react-intl";
import handleLogin from "../../handles/handleLogin";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors },
  } = useForm();
  const [language, setLanguage] = useState("en");
  const [showPassword, setShowPassword] = useState(false);
  const [showForgot, setShowForgot] = useState(false);
  const navigate = useNavigate();
  //Internacionalização
  const intl = useIntl();

  const onSubmit = async (loginData) => {
    const user = {
      email: loginData.email,
      password: loginData.password,
    };
    try {
      const success = await handleLogin(user);
      console.log("Login attempt:", user);
      console.log("Login success:", success);
      if (success) {
        reset();
        navigate("/profile");
      } else {
        reset();
      }
    } catch (error) {
      reset();
    }
  };

  return (
    <div className="login-container">
      {/* DIVISÃO DO LOGO */}
      <div className="login-logo-container">
        <img
          src={citrusLogo}
          alt={intl.formatMessage({ id: "loginLogo" })}
          className="login-logo"
        />
        <div className="logo-undertitle">
          {intl.formatMessage({ id: "loginSubtitle" })}
        </div>
      </div>

      {/* FORMULÁRIO */}
      <div className="loginform-container">
        <div className="login-title-group">
          <h1 className="login-title">
            {intl.formatMessage({ id: "loginTitle" })}
          </h1>
          <div className="login-subtitle">
            {intl.formatMessage({ id: "loginSubtitle" })}
          </div>
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
                  {intl.formatMessage({ id: "loginFieldEmail" })}
                </label>
                <span className="error-message">
                  {errors.email ? errors.email.message : "\u00A0"}
                </span>
              </div>

              <input
                id="login-email"
                className={`login-input`}
                {...register("email", {
                  required: intl.formatMessage({
                    id: "loginErrorEmailMissing",
                  }),
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: intl.formatMessage({
                      id: "loginErrorEmailInvalid",
                    }),
                  },
                })}
              />
            </div>

            <div className="login-field">
              <div className="login-labelAndError">
                <label className="login-label" htmlFor="login-password">
                  {intl.formatMessage({ id: "loginFieldPassword" })}
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
                    required: intl.formatMessage({
                      id: "loginErrorPasswordMissing",
                    }),
                  })}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={intl.formatMessage({
                    id: showPassword
                      ? "registerHidePassword"
                      : "registerShowPassword",
                  })}
                >
                  {showPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                </button>
              </div>
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
      <OffcanvasForgotPassword
        show={showForgot}
        onClose={() => setShowForgot(false)}
      />
    </div>
  );
}
