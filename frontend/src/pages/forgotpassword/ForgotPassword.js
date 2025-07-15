import React, { useState } from "react";
import "./ForgotPassword.css";
import "../../styles/AuthTransition.css";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import handleNotification from "../../handles/handleNotification";
import handleRequestPasswordReset from "../../handles/handleRequestPasswordReset";
import useLocaleStore from "../../stores/useLocaleStore";

export default function ForgotPassword({ onClose }) {
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();
  const lang = useLocaleStore((state) => state.locale);

  const resetPassword = async(email) => {
    await handleRequestPasswordReset(email, lang);
    reset();
    onClose();
  };

  // Apresentação de erros ao utilizador
  const onError = (errors) => {
    Object.entries(errors).forEach(([errorKey, errorValue]) => {
      handleNotification("error", errorValue.message);
    });
  };

  return (
    <div className="forgot-container d-flex auth-slide-in">
      <div className="forgot-form-container">
        <h1 className="forgot-title">
          {t("forgotPasswordTitle")}
        </h1>
        <div className="forgot-subtitle">
          {t("forgotPasswordSubtitle")}
        </div>

        <form
          className="forgot-form"
          onSubmit={handleSubmit(resetPassword, onError)}
        >
          <div className="forgot-fields">
            <div className="forgot-field">
              <label className="forgot-label" htmlFor="forgot-email">
                {t("forgotPasswordFieldEmail")}
              </label>
              <span className="error-message">
                {errors.email ? errors.email.message : "\u00A0"}
              </span>
              <input
                id="forgot-email"
                type="email"
                className="forgot-input"
                {...register("email", {
                  required: t("forgotPasswordEmailMissing"),
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: t("forgotPasswordEmailInvalid"),
                  },
                })}
              />
            </div>
          </div>
          <button className="main-button" type="submit">
            {t("forgotPasswordSubmit")}
          </button>
        </form>
      </div>
    </div>
  );
}
