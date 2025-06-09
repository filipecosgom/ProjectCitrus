import React from "react";
import { FcApproval } from "react-icons/fc";
import "./ActivatedAccount.css";

export default function ActivatedAccount() {
  return (
    <div className="activation-container">
      <div className="activation-card">
        <FcApproval className="activation-check-icon" size={72} />
        <h1 className="activation-title">Conta ativada!</h1>
        <div className="activation-message">
          <strong>
            A sua conta foi activada, pode efetuar login.
            <br />
            Bem-vindo ao Citrus.
          </strong>
        </div>
      </div>
    </div>
  );
}
