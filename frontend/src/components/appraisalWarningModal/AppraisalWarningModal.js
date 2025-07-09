import React from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom"; // ✅ ADICIONAR
import { IoWarning, IoClose } from "react-icons/io5";
import "./AppraisalWarningModal.css";

const AppraisalWarningModal = ({ isOpen, onClose, validationData = {} }) => {
  const { t } = useTranslation();
  const navigate = useNavigate(); // ✅ ADICIONAR

  if (!isOpen) return null;

  const {
    totalAppraisals = 0,
    inProgressCount = 0,
    completedCount = 0,
  } = validationData;

  // ✅ NOVA função para navegar para appraisals pendentes
  const handleCheckPendingAppraisals = () => {
    // Navegar para /appraisals com filtro state=IN_PROGRESS
    navigate("/appraisals?state=IN_PROGRESS");
    onClose(); // Fechar o modal
  };

  return (
    <div className="appraisal-warning-modal-overlay">
      <div className="appraisal-warning-modal">
        <div className="appraisal-warning-modal-header">
          <div className="appraisal-warning-icon">
            <IoWarning />
          </div>
          <h2>{t("cycles.cannotCloseCycle")}</h2>
          <button
            className="appraisal-warning-close-btn"
            onClick={onClose}
            aria-label="Close"
          >
            <IoClose />
          </button>
        </div>

        <div className="appraisal-warning-modal-content">
          <p className="appraisal-warning-message">
            {t("cycles.appraisalsNotCompleted")}
          </p>

          <div className="appraisal-warning-details">
            <div className="appraisal-warning-summary">
              <h3>{t("cycles.appraisalsSummary")}</h3>
              <div className="appraisal-warning-stats">
                <div className="appraisal-stat">
                  <span className="appraisal-stat-label">
                    {t("cycles.totalAppraisals")}:
                  </span>
                  <span className="appraisal-stat-value">
                    {totalAppraisals}
                  </span>
                </div>
                <div className="appraisal-stat warning">
                  <span className="appraisal-stat-label">
                    {t("cycles.pendingAppraisals")}:
                  </span>
                  <span className="appraisal-stat-value">
                    {inProgressCount}
                  </span>
                </div>
              </div>
            </div>

            {(inProgressCount > 0 || completedCount > 0) && (
              <div className="appraisal-warning-breakdown">
                <h4>{t("cycles.appraisalsBreakdown")}</h4>
                <ul>
                  {inProgressCount > 0 && (
                    <li>
                      {t("cycles.inProgressAppraisals", {
                        count: inProgressCount,
                      })}
                    </li>
                  )}
                  {completedCount > 0 && (
                    <li>
                      {t("cycles.completedAppraisals", {
                        count: completedCount,
                      })}
                    </li>
                  )}
                </ul>
              </div>
            )}

            <p className="appraisal-warning-instruction">
              {t("cycles.closeCycleInstructionCompleted")}
            </p>
          </div>
        </div>

        <div className="appraisal-warning-modal-footer">
          {/* ✅ CORRIGIR: Usar chave correta dentro do objeto cycles */}
          <button
            className="appraisal-warning-check-btn"
            onClick={handleCheckPendingAppraisals}
          >
            {t("cycles.checkPendingAppraisals", "Check Pending Appraisals")}
          </button>
          <button
            className="appraisal-warning-understand-btn"
            onClick={onClose}
          >
            {t("cycles.understood")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AppraisalWarningModal;
