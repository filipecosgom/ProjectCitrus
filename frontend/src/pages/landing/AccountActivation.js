/**
 * @file AccountActivation.js
 * @module AccountActivation
 * @description React page component for displaying account activation confirmation and redirecting to login.
 * Shows activation success message, email confirmation, and countdown timer for automatic redirection.
 * Integrates with internationalization and supports manual navigation to login.
 * @author Project Citrus Team
 */

/**
 * Account Activation Page Component.
 *
 * Renders the account activation confirmation interface, including:
 * - Success icon and activation title
 * - Confirmation message with activated email address
 * - Countdown timer for automatic redirection to login page
 * - Manual button for immediate navigation to login
 * - Internationalization of all labels and messages
 *
 * @param {Object} props - Component props
 * @param {string} props.email - The email address associated with the activated account
 * @returns {JSX.Element} The rendered account activation confirmation page
 *
 * @example
 * <AccountActivation email="user@example.com" />
 */

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
        <h1 className="activation-title">{t("activationTitle")}</h1>
        <div className="activation-message">
          <strong>{t("activationSuccessMessage")}</strong>
          <p>{t("activationEmailConfirmation", { email: email || "x" })}</p>
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
