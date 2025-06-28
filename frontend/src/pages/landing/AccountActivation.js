import React, { useEffect, useState } from "react";
import { FaRegCheckCircle } from "react-icons/fa";
import "./AccountActivation.css";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

export default function AccountActivation({ email }) {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const segundos = 10; // valor inicial em segundos
  const [countdown, setCountdown] = useState(segundos);

  // Countdown e redirecionamento
  useEffect(() => {
    if (countdown === 0) {
      navigate("/login");
      return;
    }
    const interval = setInterval(() => {
      setCountdown((prev) => prev - 1);
    }, 1000);
    return () => clearInterval(interval);
  }, [countdown, navigate]);

  return (
    <div className="activation-container">
      <div className="activation-card">
        <FaRegCheckCircle className="activation-check-icon" />
        <h1 className="activation-title">
          {t("activationTitle")}
        </h1>
        <div className="activation-message">
          <strong>
            {t("activationSuccessMessage")}
          </strong>
          <p>
            {t("activationEmailConfirmation", { email: email || "x" })}
          </p>
          <p style={{ marginTop: 16, color: "#888", fontSize: "0.95em" }}>
            {t("activationRedirectMessage", { segundos: countdown })}
          </p>
        </div>
        <div>
          <button
            className="main-button"
            type="button"
            onClick={() => navigate("/login")}
          >
            {t("registerButton")}
          </button>
        </div>
      </div>
    </div>
  );
}
