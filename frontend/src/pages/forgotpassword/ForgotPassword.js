import React, { useState } from "react";
import "./ForgotPassword.css"; // Copia Register.css e adapta o nome
import "../../styles/AuthTransition.css";
import { useIntl } from "react-intl";
import { useForm } from "react-hook-form";
import handleNotification from "../../handles/handleNotification";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const { register, handleSubmit, watch, reset } = useForm();

  //Internacionalização
  const intl = useIntl();

  const handleResetPassword = (e) => {
    e.preventDefault();
    // Aqui podes adicionar lógica para reset de password
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
          onSubmit={handleSubmit(handleResetPassword, onError)}
        >
          <div className="forgot-fields">
            <div className="forgot-field">
              <label className="forgot-label" htmlFor="forgot-email">
                {intl.formatMessage({ id: "forgotPasswordFieldEmail" })}
              </label>
              <input
                id="forgot-email"
                type="email"
                className="forgot-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                autoComplete="username"
                required
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