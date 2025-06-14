import "./TwoFactor.css";
import "../../styles/AuthTransition.css";
import React, { useState } from "react";
import "../../styles/AuthTransition.css";
import { useIntl } from "react-intl";
import { set, useForm } from "react-hook-form";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";
import handleRequestSecretKey from "../../handles/handleRequestSecretKey";

export default function TwoFactor() {
  const [email, setEmail] = useState("");
  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors },
  } = useForm();
  const [showPassword, setShowPassword] = useState(false);

  //Internacionalização
  const intl = useIntl();
  const [twoFactorCode, setTwoFactorCode ] = useState(null);

  const handleRequestAuthCode = async (data) => {
    try {
      const authCode = await handleRequestSecretKey(data);
      console.log("Response from handleRequestSecretKey:", authCode);
      setTwoFactorCode(authCode);
      reset();

    }
    finally {
      console.log("THe end of handleRequestAuthCode");
    }
  };

  return (
    <div className="twofactor-container">
      <div className="twofactor-form-container">
        <h1 className="twofactor-title">
          {intl.formatMessage({ id: "twoFactorTitle" })}
        </h1>
        <form
          className="twofactor-form"
          onSubmit={handleSubmit(handleRequestAuthCode)}
        >
          <div className="twofactor-fields">
            <div className="twofactor-field">
              <div className="login-labelAndError">
                <label className="login-label" htmlFor="login-email">
                  {intl.formatMessage({ id: "loginFieldEmail" })}
                </label>
                <span className="error-message">
                  {errors.email ? errors.email.message : "\u00A0"}
                </span>
              </div>
              <input
                id="login-email"
                className={`login-input`}
                {...register("email", {
                  required: intl.formatMessage({
                    id: "twofactorErrorEmailMissing",
                  }),
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: intl.formatMessage({
                      id: "twofactorErrorEmailInvalid",
                    }),
                  },
                })}
              />
            </div>
            <div className="twofactor-field">
              <div className="login-labelAndError">
                <label className="login-label" htmlFor="login-password">
                  {intl.formatMessage({ id: "loginFieldPassword" })}
                </label>
                <span className="error-message">
                  {errors.password ? errors.password.message : "\u00A0"}
                </span>
              </div>
              <div className="password-input-wrapper">
                <input
                  id="login-password"
                  type={showPassword ? "text" : "password"}
                  className="login-input"
                  {...register("password", {
                    required: intl.formatMessage({
                      id: "loginErrorPasswordMissing",
                    }),
                  })}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={intl.formatMessage({
                    id: showPassword
                      ? "registerHidePassword"
                      : "registerShowPassword",
                  })}
                >
                  {showPassword ? <FaRegEye /> : <FaRegEyeSlash />}
                </button>
              </div>
            </div>
          </div>
          <div className="twofactor-button-container">
          <button className="twoFactor-button" type="submit">
            {intl.formatMessage({ id: "twoFactorRequest" })}
          </button>
          </div>
        </form>
        <div className="twofactor-instructions">
        <ol>
          <li>{intl.formatMessage({ id: "twoFactorSetupStep1" })}</li>
          <li>{intl.formatMessage({ id: "twoFactorSetupStep2" })}</li>
          <li>{intl.formatMessage({ id: "twoFactorSetupStep3" })}</li>
          <div className="twofactor-secret-placeholder">
            {twoFactorCode ? twoFactorCode : intl.formatMessage({ id: "twoFactorSecretPlaceholder" })}  
          </div>
          <li>{intl.formatMessage({ id: "twoFactorSetupStep4" })}</li>
        </ol>
        </div>
      </div>
    </div>
  );
}
