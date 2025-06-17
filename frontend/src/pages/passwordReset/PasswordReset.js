import { useState } from "react";
import { useForm } from "react-hook-form";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./PasswordReset.css";
import "../../styles/AuthTransition.css";
import { useIntl } from "react-intl";
import { useNavigate } from "react-router-dom";
import handleResetPassword from "../../";

export default function PasswordReset() {
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
  const strongPasswordPattern =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{12,}$/;
  const navigate = useNavigate();
  //Internacionalização
  const intl = useIntl();

  const onSubmit = async (newPassword) => {
    handlePasswordReset(newPassword);
    console.log(newPassword);

    reset();
  };

  return (
    <div className="passwordReset-container">
      {/* DIVISÃO DO LOGO */}
      <div className="passwordReset-logo-container">
        <img
          src={citrusLogo}
          alt={intl.formatMessage({ id: "passwordResetLogo" })}
          className="passwordReset-logo"
        />
        <div className="passwordReset-undertitle">
          {intl.formatMessage({ id: "passwordResetSubtitle" })}
        </div>
      </div>

      {/* FORMULÁRIO */}
      <div className="passwordResetform-container">
        <div className="passwordReset-title-group">
          <h1 className="passwordReset-title">
            {intl.formatMessage({ id: "passwordResetTitle" })}
          </h1>
          <div className="passwordReset-subtitle">
            {intl.formatMessage({ id: "passwordResetSubtitle" })}
          </div>
        </div>
        <form
          className="passwordReset-form"
          id="passwordReset-form"
          onSubmit={handleSubmit(onSubmit)}
        >
          <div className="passwordReset-fields">
            <div className="passwordReset-field">
              <div className="passwordReset-labelAndError">
                <label className="passwordReset-label" htmlFor="passwordReset-password">
                  {intl.formatMessage({ id: "passwordResetFieldPassword" })}
                </label>
                <span className="error-message">
                  {errors.password ? errors.password.message : "\u00A0"}
                </span>
              </div>

              <div className="passwordReset-input-wrapper">
                <input
                  id="passwordReset-password"
                  type={showPassword ? "text" : "password"}
                  className="passwordReset-input"
                  {...register("password", {
                    required: intl.formatMessage({
                      id: "passwordResetErrorPasswordMissing",
                    }),
                    pattern: {
                          value: strongPasswordPattern,
                          message: intl.formatMessage({
                            id: "passwordResetErrorPasswordWeak",
                          }),
                        },
                      })}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={intl.formatMessage({
                    id: showPassword
                      ? "passwordResetHidePassword"
                      : "passwordResetShowPassword",
                  })}
                >
                  {showPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                </button>
              </div>
            </div>

            <div className="password-input-container">
              <div className="passwordReset-labelAndError">
                <label
                  className="passwordReset-label"
                  htmlFor="passwordReset-confirm-password"
                >
                  {intl.formatMessage({
                    id: "passwordResetFieldConfirmPassword",
                  })}
                </label>
                <span className="error-message">
                  {errors.passwordConfirm
                    ? errors.passwordConfirm.message
                    : "\u00A0"}
                </span>
              </div>

              <div className="passwordReset-input-wrapper">
                <input
                  id="passwordReset-confirm-password"
                  type={showConfirmPassword ? "text" : "password"}
                  className="passwordReset-input"
                  {...register("passwordConfirm", {
                    required: intl.formatMessage({
                      id: "passwordResetErrorConfirmPasswordMissing",
                    }),
                    validate: {
                      isConfirmed: (value) =>
                        value === watch("password") ||
                        intl.formatMessage({
                          id: "passwordResetErrorPasswordMismatch",
                        }),
                    },
                  })}
                />
                <button
                  type="button"
                  className="passwordReset-toggle-btn"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  aria-label={intl.formatMessage({
                    id: showConfirmPassword
                      ? "passwordResetHidePassword"
                      : "passwordResetShowPassword",
                  })}
                >
                  {showConfirmPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                </button>
              </div>

              <button className="main-button" type="submit">
                {intl.formatMessage({ id: "passwordResetSubmit" })}
              </button>
            </div>
          </div>
        </form>

        <div className="login-language-dropdown">
          {/* Dropdown de idioma dentro da coluna do formulário */}
          <LanguageDropdown language={language} setLanguage={setLanguage} />
        </div>
      </div>
    </div>
  );
}
