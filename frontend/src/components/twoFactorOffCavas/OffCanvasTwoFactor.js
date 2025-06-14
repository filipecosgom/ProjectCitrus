import TwoFactor from "./TwoFactor";
import "./OffCanvasTwoFactor.css";
import React, { useRef } from "react";


export default function OffCanvasTwoFactor({ show, onClose }) {
  const twoFactorRef = useRef(null);

  const handleReset = () => {
    if (twoFactorRef.current) {
      twoFactorRef.current.resetAuthCode();
    }
  };

  return (
    <>
      <div
        className={`offcanvas-overlay${show ? " show" : ""}`}
        onClick={onClose}
      />
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
        <TwoFactor ref={twoFactorRef} />
      </div>
    </>
  );
}
