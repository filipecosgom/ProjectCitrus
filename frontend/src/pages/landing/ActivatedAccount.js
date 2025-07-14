/**
 * @file ActivatedAccount.js
 * @module ActivatedAccount
 * @description React page component for handling account activation via token and displaying confirmation.
 * Activates the user account using a token from the URL, shows success message, and redirects to login after a countdown.
 * Integrates with backend activation endpoint, notification system, and supports internationalization.
 * @author Project Citrus Team
 */

/**
 * Activated Account Page Component.
 *
 * Renders the account activation confirmation interface, including:
 * - Success icon and activation title
 * - Confirmation message for successful account activation
 * - Countdown timer for automatic redirection to login page
 * - Manual button for immediate navigation to login
 * - Internationalization of all labels and messages
 * - Error handling and notification for invalid or expired tokens
 *
 * @returns {JSX.Element} The rendered activated account confirmation page
 *
 * @example
 * <ActivatedAccount
 */

import React, { useEffect, useState } from "react";
import { FcApproval } from "react-icons/fc";
import "./ActivatedAccount.css";
import { useNavigate, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import handleActivateAccount from "../../handles/handleActivateAccount";
import handleNotification from "../../handles/handleNotification";

export default function ActivatedAccount() {
  const { t } = useTranslation();
  const token = new URLSearchParams(useLocation().search).get("token");
  const language =
    new URLSearchParams(useLocation().search).get("lang") || "en";
  const navigate = useNavigate();
  const segundos = 50;
  const [countdown, setCountdown] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const activateAccount = async () => {
      if (!token) {
        console.error("Token não encontrado");
        navigate("/login");
        return;
      }

      try {
        const response = await handleActivateAccount(token);

        if (response) {
          setCountdown(segundos);
          setSuccess(true);
        } else {
          handleNotification("error", "errorTokenExpired");
          navigate("/login");
        }
      } catch (error) {
        handleNotification("error", "unexpectedError");
        navigate("/login");
      }
    };

    activateAccount();
  }, [token, navigate]);

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
        <FcApproval className="activation-check-icon" size={72} />
        <h1 className="activation-title">{t("activatedAccountTitle")}</h1>
        <div className="activation-message">
          <strong
            dangerouslySetInnerHTML={{
              __html: t("activatedAccountMessage"),
            }}
          />
          <p style={{ marginTop: 16, color: "#888", fontSize: "0.95em" }}>
            {t("activatedAccountRedirectMessage", { segundos: countdown })}
          </p>
        </div>
        <div>
          <button
            className="main-button"
            type="button"
            onClick={() => navigate("/login")}
          >
            {t("activatedAccountButton")}
          </button>
        </div>
      </div>
    </div>
  );
}
