import React from "react";
import { FaRegCheckCircle } from "react-icons/fa";
import "./AccountActivation.css";
import { useLocation } from "react-router-dom";

export default function AccountActivation() {
    const location = useLocation();
    const email = location.state?.email;

  return (
    <div className="activation-container">
      <div className="activation-card">
        <FaRegCheckCircle className="activation-check-icon" />
        <h1 className="activation-title">Account Activation</h1>
        <div className="activation-message">
          <strong>Conta criada com sucesso!</strong>
          <p>
            A tua conta com o email <b>{email || "x"}</b> foi criada.<br />
            Enviámos um email de confirmação com um link para validares a tua conta.<br />
            Por favor verifica a tua caixa de entrada (e o spam também).
          </p>
        </div>
      </div>
    </div>
  );
}