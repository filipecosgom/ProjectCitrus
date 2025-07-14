/**
 * @file ConfirmModal.js
 * @module ConfirmModal
 * @description Modal component for user confirmation actions (danger, warning, info).
 * @author Project Citrus Team
 */

import React, { useState, useEffect } from "react";
import { FaTimes, FaExclamationTriangle } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import "./ConfirmModal.css";

/**
 * ConfirmModal component for displaying a confirmation dialog.
 * @param {Object} props - Component props
 * @param {boolean} props.isOpen - Whether the modal is open
 * @param {Function} props.onClose - Callback to close the modal
 * @param {Function} props.onConfirm - Callback for confirm action
 * @param {string} [props.title] - Modal title
 * @param {string} [props.message] - Modal message
 * @param {string} [props.confirmText] - Confirm button text
 * @param {string} [props.cancelText] - Cancel button text
 * @param {"danger"|"warning"|"info"} [props.variant="danger"] - Modal variant
 * @returns {JSX.Element|null}
 */
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

  /**
   * Controls rendering and animation of the modal.
   * Handles open/close transitions for smooth UX.
   */
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

  /**
   * Prevents background scrolling when modal is open.
   */
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

  /**
   * Handles closing the modal with ESC key.
   */
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

  /**
   * Handles click on backdrop to close modal.
   * @param {React.MouseEvent} e - Mouse event
   */
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  /**
   * Handles confirm button click.
   * Calls onConfirm and closes modal.
   */
  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  // Only render modal if shouldRender is true (for animation)
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
