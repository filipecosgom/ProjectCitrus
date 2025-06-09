import React, { useEffect } from "react";
import { FaRegCheckCircle } from "react-icons/fa";
import "./AccountActivation.css";
import { useIntl } from "react-intl";
import { useNavigate } from "react-router-dom";

export default function AccountActivation({ email }) {
  const intl = useIntl();
  const navigate = useNavigate();
  const segundos = 10; // valor em segundos

  // Redireciona para login após X segundos
  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/login");
    }, segundos * 1000);
    return () => clearTimeout(timer);
  }, [navigate, segundos]);

  return (
    <div className="activation-container">
      <div className="activation-card">
        <FaRegCheckCircle className="activation-check-icon" />
        <h1 className="activation-title">
          {intl.formatMessage({ id: "activationTitle" })}
        </h1>
        <div className="activation-message">
          <strong>
            {intl.formatMessage({ id: "activationSuccessMessage" })}
          </strong>
          <p>
            {intl.formatMessage(
              { id: "activationEmailConfirmation" },
              { email: email || "x" }
            )}
          </p>
          <p style={{ marginTop: 16, color: "#888", fontSize: "0.95em" }}>
            {intl.formatMessage(
              { id: "activationRedirectMessage" },
              { segundos }
            )}
          </p>
        </div>
        <div>
          <button
            className="main-button"
            type="button"
            onClick={() => navigate("/login")}
          >
            {intl.formatMessage({ id: "registerButton" })}
          </button>
        </div>
      </div>
    </div>
  );
}
