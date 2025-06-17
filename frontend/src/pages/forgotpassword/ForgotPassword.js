import React, { useState } from "react";
import "./ForgotPassword.css"; // Copia Register.css e adapta o nome
import "../../styles/AuthTransition.css";
import { useIntl } from "react-intl";
import { useForm } from "react-hook-form";
import handleNotification from "../../handles/handleNotification";
import handleRequestPasswordReset from "../../handles/handleRequestPasswordReset";
import useLocaleStore from "../../stores/useLocaleStore";

export default function ForgotPassword({ onClose }) {
  const [email, setEmail] = useState("");
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();

  //Internacionalização
  const intl = useIntl();
  const lang = useLocaleStore((state) => state.locale);

  const resetPassword = async(email) => {
    await handleRequestPasswordReset(email, lang);
    reset();
    onClose();
  };

  // Apresentação de erros ao utilizador
  const onError = (errors) => {
    Object.entries(errors).forEach(([errorKey, errorValue]) => {
      handleNotification(intl, "error", errorValue.message);
    });
  };

  return (
    <div className="forgot-container d-flex auth-slide-in">
      <div className="forgot-form-container">
        <h1 className="forgot-title">
          {intl.formatMessage({ id: "forgotPasswordTitle" })}
        </h1>
        <div className="forgot-subtitle">
          {intl.formatMessage({ id: "forgotPasswordSubtitle" })}
        </div>

        <form
          className="forgot-form"
          onSubmit={handleSubmit(resetPassword, onError)}
        >
          <div className="forgot-fields">
            <div className="forgot-field">
              <label className="forgot-label" htmlFor="forgot-email">
                {intl.formatMessage({ id: "forgotPasswordFieldEmail" })}
              </label>
              <span className="error-message">
                {errors.email ? errors.email.message : "\u00A0"}
              </span>
              <input
                id="forgot-email"
                type="email"
                className="forgot-input"
                {...register("email", {
                  required: intl.formatMessage({
                    id: "forgotPasswordEmailMissing",
                  }),
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: intl.formatMessage({
                      id: "forgotPasswordEmailInvalid",
                    }),
                  },
                })}
              />
            </div>
          </div>
          <button className="main-button" type="submit">
            {intl.formatMessage({ id: "forgotPasswordSubmit" })}
          </button>
        </form>
      </div>
    </div>
  );
}
