import React from "react";
import "./confirmationModal.css";
import { useTranslation } from "react-i18next";
import { IoWarning } from "react-icons/io5";

const ConfirmationModal = ({
  isOpen,
  title,
  message1,
  message2,
  onConfirm,
  onClose,
}) => {
  const { t } = useTranslation();

  if (!isOpen) return null;

  return (
    <div id="modal-confirmation" className="modal-ConfirmModal">
      <div className="modal-content-ConfirmModal">
        <div className="modal-header-ConfirmModal" id="modal-header-ConfirmModal">
          <div className="modal-warning-icon">
            <IoWarning />
          </div>
          <p className="modal-title-ConfirmModal">{t(title)}</p>
          <span
            className="close-ConfirmModal"
            id="close-detail"
            onClick={onClose}
            role="button"
            tabIndex={0}
            aria-label={t("confirmationModalCloseButton", "Close")}
          >
            &times;
          </span>
        </div>
        <div className="modal-body-ConfirmModal">
          <p className="modal-message1-confirmation">{t(message1)}</p>
          <p className="modal-message2-confirmation">{t(message2)}</p>
          <div className="confirmModalButtons">
            <button className="confirm-button" type="button" onClick={onConfirm}>
              {t("confirmationModalConfirmButton", "Confirm")}
            </button>
            <button className="cancel-button" type="button" onClick={onClose}>
              {t("confirmationModalCancelButton", "Cancel")}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConfirmationModal;
