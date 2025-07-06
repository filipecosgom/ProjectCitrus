import React, { useState, useEffect } from "react";
import { FaTimes, FaExclamationTriangle } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import "./ConfirmModal.css";

const ConfirmModal = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText,
  cancelText,
  variant = "danger", // "danger", "warning", "info"
}) => {
  const { t } = useTranslation();
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);

  // Controlar renderização e animação
  useEffect(() => {
    if (isOpen) {
      setShouldRender(true);
      const timer = setTimeout(() => {
        setIsAnimating(true);
      }, 10);
      return () => clearTimeout(timer);
    } else {
      setIsAnimating(false);
      const timer = setTimeout(() => {
        setShouldRender(false);
      }, 300);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  // Controlar scroll da página
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  // Fechar com ESC
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") {
        onClose();
      }
    };
    if (isOpen) {
      document.addEventListener("keydown", handleEsc);
    }
    return () => {
      document.removeEventListener("keydown", handleEsc);
    };
  }, [isOpen, onClose]);

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  if (!shouldRender) return null;

  return (
    <div
      className={`confirm-modal-backdrop ${isAnimating ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`confirm-modal ${isAnimating ? "open" : ""}`}>
        <div className="confirm-modal-header">
          <div className={`confirm-modal-icon ${variant}`}>
            <FaExclamationTriangle />
          </div>
          <button className="confirm-modal-close" onClick={onClose}>
            <FaTimes />
          </button>
        </div>

        <div className="confirm-modal-content">
          <h3 className="confirm-modal-title">
            {title || t("common.confirm")}
          </h3>
          <p className="confirm-modal-message">
            {message || t("common.confirmMessage")}
          </p>
        </div>

        <div className="confirm-modal-actions">
          <button
            className="confirm-modal-button confirm-modal-button-cancel"
            onClick={onClose}
          >
            {cancelText || t("common.cancel")}
          </button>
          <button
            className={`confirm-modal-button confirm-modal-button-confirm ${variant}`}
            onClick={handleConfirm}
          >
            {confirmText || t("common.confirm")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;
