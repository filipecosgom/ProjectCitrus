import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import LanguageDropdown from "../../components/languages/LanguageDropdown";
import "./PasswordReset.css";
import "../../styles/AuthTransition.css";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import handleCheckPasswordResetToken from "../../handles/handleCheckPasswordResetToken";
import handleChangePassword from "../../handles/handleChangePassword";
import Spinner from "../../components/spinner/Spinner";
import useLocaleStore from "../../stores/useLocaleStore";
import handleNotification from "../../handles/handleNotification";

export default function PasswordReset() {
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors },
  } = useForm();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const strongPasswordPattern =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{12,}$/;
  const navigate = useNavigate();
  const token = new URLSearchParams(window.location.search).get("token");
  const lang = new URLSearchParams(window.location.search).get("lang") || "en";
  const { locale, setLocale } = useLocaleStore();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkToken = async () => {
      setLocale(lang);
      try {
        const response = await handleCheckPasswordResetToken(token);
        if (!response) {
          navigate("/login");
        }
      } catch (error) {
        navigate("/login", { replace: true });
      }
    };
    checkToken();
    setLoading(false);
  }, []);

  const onSubmit = async (passwordData) => {
    const response = await handleChangePassword(token, passwordData.password);
    if (response) {
      reset();
      navigate("/login");
      handleNotification("success", "passwordResetSuccess");
    } else {
      handleNotification("error", "passwordResetError");
      reset();
      navigate("/login");
    }
  };

  if (loading) return <Spinner />;

  return (
    <div className="passwordReset-container">
      {/* DIVISÃO DO LOGO */}
      <div className="passwordReset-logo-container">
        <img
          src={citrusLogo}
          alt={t("passwordResetLogo")}
          className="passwordReset-logo"
        />
        <div className="passwordReset-undertitle">
          {t("passwordResetSubtitle")}
        </div>
      </div>

      {/* FORMULÁRIO */}
      <div className="passwordResetform-container">
        <div className="passwordReset-title-group">
          <h1 className="passwordReset-title">{t("passwordResetTitle")}</h1>
          <div className="passwordReset-subtitle">
            {t("passwordResetSubtitle")}
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
                <label
                  className="passwordReset-label"
                  htmlFor="passwordReset-password"
                >
                  {t("passwordResetFieldPassword")}
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
                    required: t("passwordResetErrorPasswordMissing"),
                    pattern: {
                      value: strongPasswordPattern,
                      message: t("passwordResetErrorPasswordWeak"),
                    },
                  })}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={t(
                    showPassword
                      ? "passwordResetHidePassword"
                      : "passwordResetShowPassword"
                  )}
                >
                  {showPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                </button>
              </div>
            </div>

            <div className="passwordReset-field">
              <div className="passwordReset-labelAndError">
                <label
                  className="passwordReset-label"
                  htmlFor="passwordReset-confirm-password"
                >
                  {t("passwordResetFieldConfirmPassword")}
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
                    required: t("passwordResetErrorConfirmPasswordMissing"),
                    validate: {
                      isConfirmed: (value) =>
                        value === watch("password") ||
                        t("passwordResetErrorPasswordMismatch"),
                    },
                  })}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  aria-label={t(
                    showConfirmPassword
                      ? "passwordResetHidePassword"
                      : "passwordResetShowPassword"
                  )}
                >
                  {showConfirmPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                </button>
              </div>

              <button className="main-button" type="submit">
                {t("passwordResetSubmit")}
              </button>
            </div>
          </div>
        </form>

        <div className="login-language-dropdown">
          {/* Dropdown de idioma dentro da coluna do formulário */}
          <LanguageDropdown language={locale} setLanguage={setLocale} />
        </div>
      </div>
    </div>
  );
}
