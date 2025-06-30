import "./TwoFactor.css";
import "../../styles/AuthTransition.css";
import React, { forwardRef, useImperativeHandle, useState } from "react";
import { useTranslation } from "react-i18next";
import { useForm } from "react-hook-form";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import handleRequestSecretKey from "../../handles/handleRequestSecretKey";

const TwoFactor = forwardRef((props, ref) => {
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();
  const [showPassword, setShowPassword] = useState(false);
  const [authCode, setAuthCode] = useState(null);

  const handleRequestAuthCode = async (data) => {
    try {
      const authCode = await handleRequestSecretKey(data);
      if(authCode) {
        reset();
        setAuthCode(authCode);
      }
    } finally {
      reset()
    }
  };

  useImperativeHandle(ref, () => ({
    resetAuthCode() {
      setAuthCode(""); // Clears the authentication code
    }
  }));

  return (
    <div className="twofactor-container">
      <div className="twofactor-form-container">
        <h1 className="twofactor-title">
          {t("twoFactorTitle")}
        </h1>
        {authCode ? (
          <div className="twofactor-instructions">
            <ol>
              <li>{t("twoFactorSetupStep1")}</li>
              <li>{t("twoFactorSetupStep2")}</li>
              <li>{t("twoFactorSetupStep3")}</li>
              <div className="twofactor-secret-placeholder">
                {authCode
                  ? authCode
                  : t("twoFactorSecretPlaceholder")}
              </div>
              <li>{t("twoFactorSetupStep4")}</li>
            </ol>            
          </div>
        ) : (
          <form
            className="twofactor-form"
            onSubmit={handleSubmit(handleRequestAuthCode)}
          >
            <div className="twofactor-fields">
              <div className="twofactor-field">
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
                    required: t("twofactorErrorEmailMissing"),
                    pattern: {
                      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                      message: t("twofactorErrorEmailInvalid"),
                    },
                  })}
                />
              </div>
              <div className="twofactor-field">
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
                    aria-label={t(showPassword ? "registerHidePassword" : "registerShowPassword")}
                  >
                    {showPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                  </button>
                </div>
              </div>
            </div>
            <div className="twofactor-button-container">
              <button className="twoFactor-button" type="submit">
                {t("twoFactorRequest")}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
});

export default TwoFactor;
