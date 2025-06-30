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
        <h1 className="activation-title">
          {t("activatedAccountTitle")}
        </h1>
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
