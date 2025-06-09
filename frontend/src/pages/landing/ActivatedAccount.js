import React, { useEffect, useState } from "react";
import { FcApproval } from "react-icons/fc";
import "./ActivatedAccount.css";
import { useNavigate } from "react-router-dom";
import { useIntl } from "react-intl";

export default function ActivatedAccount() {
  const navigate = useNavigate();
  const intl = useIntl();
  const segundos = 10;
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
        <FcApproval className="activation-check-icon" size={72} />
        <h1 className="activation-title">
          {intl.formatMessage({ id: "activatedAccountTitle" })}
        </h1>
        <div className="activation-message">
          <strong
            dangerouslySetInnerHTML={{
              __html: intl.formatMessage({ id: "activatedAccountMessage" }),
            }}
          />
          <p style={{ marginTop: 16, color: "#888", fontSize: "0.95em" }}>
            {intl.formatMessage(
              { id: "activatedAccountRedirectMessage" },
              { segundos: countdown }
            )}
          </p>
        </div>
        <div>
          <button
            className="main-button"
            type="button"
            onClick={() => navigate("/login")}
          >
            {intl.formatMessage({ id: "activatedAccountButton" })}
          </button>
        </div>
      </div>
    </div>
  );
}
