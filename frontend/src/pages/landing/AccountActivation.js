import React from "react";
import { FaRegCheckCircle } from "react-icons/fa";
import "./AccountActivation.css";
import { useIntl } from "react-intl";

export default function AccountActivation({ email }) {
    //Internacionalização
    const intl = useIntl();

    return (
        <div className="activation-container">
            <div className="activation-card">
                <FaRegCheckCircle className="activation-check-icon" />
                <h1 className="activation-title">
                    {intl.formatMessage({ id: "activationTitle" })}
                </h1>
                <div className="activation-message">
                    <strong>{intl.formatMessage({ id: "activationSuccessMessage" })}</strong>
                    <p>
                        {intl.formatMessage(
                            { id: "activationEmailConfirmation" },
                            { email: email || "x" }
                        )}
                    </p>
                </div>
            </div>
        </div>
    );
}