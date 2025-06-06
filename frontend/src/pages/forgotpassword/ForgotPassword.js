import React, { useState } from "react";
import "./ForgotPassword.css"; // Copia Register.css e adapta o nome
import "../../styles/AuthTransition.css";
import { useIntl } from "react-intl";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");

   //Internacionalização
  const intl = useIntl();

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aqui podes adicionar lógica para reset de password
  };

  return (
  <div className="forgot-container d-flex auth-slide-in">
    <div className="forgot-form-container">
      <h1 className="forgot-title">{intl.formatMessage({ id: "forgotPasswordTitle" })}</h1>
      <div className="forgot-subtitle">{intl.formatMessage({ id: "forgotPasswordSubtitle" })}</div>
      <form className="forgot-form" onSubmit={handleSubmit}>
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