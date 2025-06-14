import TwoFactor from "./TwoFactor";
import "./OffCanvasTwoFactor.css";

export default function OffCanvasTwoFactor({ show, onClose }) {
  return (
    <>
      <div
        className={`offcanvas-overlay${show ? " show" : ""}`}
        onClick={onClose}
      />
      <div className={`offcanvas-forgot${show ? " show" : ""}`}>
        <button
          className="offcanvas-twofactor-close-btn"
          onClick={onClose}
          aria-label="Close"
          type="button"
        >
          &times;
        </button>
        <TwoFactor />
      </div>
    </>
  );
}
