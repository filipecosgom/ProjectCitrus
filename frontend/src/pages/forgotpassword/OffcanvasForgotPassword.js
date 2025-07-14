/**
 * @file OffcanvasForgotPassword.js
 * @module OffcanvasForgotPassword
 * @description React component for rendering a forgot password form inside an off-canvas modal.
 * Provides overlay and close button functionality, and integrates the ForgotPassword form.
 * Supports accessibility and responsive UI for password reset requests.
 * @author Project Citrus Team
 */

/**
 * Offcanvas Forgot Password Component.
 *
 * Renders an off-canvas modal containing the forgot password form, including:
 * - Overlay that closes the modal when clicked
 * - Close button with accessible label
 * - Integration of the ForgotPassword form component
 * - Responsive and accessible design for password reset requests
 *
 * @param {Object} props - Component props
 * @param {boolean} props.show - Controls visibility of the off-canvas modal
 * @param {Function} props.onClose - Callback to close the modal
 * @returns {JSX.Element} The rendered off-canvas forgot password modal
 *
 * @example
 * <OffcanvasForgotPassword show={showModal} onClose={handleClose} />
 */

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
        <ForgotPassword onClose={onClose} />
      </div>
    </>
  );
}
