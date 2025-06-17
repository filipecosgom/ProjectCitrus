import React from "react";
import ForgotPassword from "../../pages/forgotpassword/ForgotPassword";
import "./OffcanvasForgotPassword.css";

export default function OffcanvasForgotPassword({ show, onClose }) {
  return (
    <>
      <div
        className={`offcanvas-overlay${show ? " show" : ""}`}
        onClick={onClose}
      />
      <div className={`offcanvas-forgot${show ? " show" : ""}`}>
        <button
          className="offcanvas-close-btn"
          onClick={onClose}
          aria-label="Close"
          type="button"
        >
          &times;
        </button>
        <ForgotPassword onClose={onClose}/>
      </div>
    </>
  );
}
