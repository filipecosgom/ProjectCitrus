/**
 * @file OffCanvasTwoFactor.js
 * @module OffCanvasTwoFactor
 * @description Offcanvas panel for two-factor authentication setup and code request.
 * Integrates with the TwoFactor component and provides reset and close actions.
 * @author Project Citrus Team
 */

import TwoFactor from "./TwoFactor";
import "./OffCanvasTwoFactor.css";
import React, { useRef } from "react";

/**
 * OffCanvasTwoFactor component for displaying two-factor authentication offcanvas.
 * @param {Object} props - Component props
 * @param {boolean} props.show - Whether the offcanvas is visible
 * @param {Function} props.onClose - Callback to close the offcanvas
 * @returns {JSX.Element} The rendered offcanvas
 */
export default function OffCanvasTwoFactor({ show, onClose }) {
  const twoFactorRef = useRef(null);

  /**
   * Calls the resetAuthCode method on the TwoFactor component to clear the authentication code.
   */
  const handleReset = () => {
    if (twoFactorRef.current) {
      twoFactorRef.current.resetAuthCode();
    }
  };

  return (
    <>
      {/* Overlay backdrop */}
      <div
        className={`offcanvas-overlay${show ? " show" : ""}`}
        onClick={onClose}
      />
      {/* Offcanvas panel */}
      <div className={`offcanvas-forgot${show ? " show" : ""}`}>
        <button
          className="offcanvas-twofactor-close-btn"
          onClick={() => {
            handleReset();
            onClose();
          }}
          aria-label="Close"
          type="button"
        >
          &times;
        </button>
        {/* TwoFactor authentication form */}
        <TwoFactor ref={twoFactorRef} />
      </div>
    </>
  );
}
