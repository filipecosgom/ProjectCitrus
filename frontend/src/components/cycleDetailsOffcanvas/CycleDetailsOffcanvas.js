/**
 * @file CycleDetailsOffcanvas.js
 * @module CycleDetailsOffcanvas
 * @description Offcanvas panel for displaying cycle details and appraisals.
 * @author Project Citrus Team
 */

import React, { useState, useEffect } from "react";
import { FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import { useSearchParams } from "react-router-dom"; // ✅ ADICIONAR
import useLocaleStore from "../../stores/useLocaleStore";
import "./CycleDetailsOffcanvas.css";

/**
 * CycleDetailsOffcanvas component for displaying cycle details and appraisals.
 * @param {Object} props - Component props
 * @param {boolean} props.isOpen - Whether the offcanvas is open
 * @param {Function} props.onClose - Callback to close the offcanvas
 * @param {Object} props.cycle - Cycle data object
 * @returns {JSX.Element|null}
 */
const CycleDetailsOffcanvas = ({ isOpen, onClose, cycle }) => {
  const { t } = useTranslation();
  const locale = useLocaleStore((state) => state.locale);
  const [searchParams] = useSearchParams(); // ✅ ADICIONAR
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);

  /**
   * Handles open/close animation for offcanvas.
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
      }, 400);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  /**
   * Updates the page title when cycle changes and offcanvas is open.
   */
  useEffect(() => {
    if (cycle && isOpen) {
      const originalTitle = document.title;
      document.title = `${t("cycles.cycleTitle", {
        id: cycle.id,
      })} - ${originalTitle}`;

      return () => {
        document.title = originalTitle;
      };
    }
  }, [cycle, isOpen, t]);

  /**
   * Locks body scroll when offcanvas is open.
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
   * Handles Escape key to close offcanvas.
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
   * Handles click on backdrop to close offcanvas.
   * @param {React.MouseEvent} e - Mouse event
   */
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  /**
   * Formats a date string according to locale.
   * @param {string} dateString - Date string
   * @returns {string} Formatted date
   */
  const formatDate = (dateString) => {
    if (!dateString) return t("cycles.na");

    const localeMap = {
      pt: "pt-PT",
      en: "en-US",
    };

    const date = new Date(dateString);
    return date.toLocaleDateString(localeMap[locale] || "pt-PT", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  /**
   * Calculates the number of days in the cycle.
   * @returns {number} Number of days
   */
  const calculateDays = () => {
    if (!cycle?.startDate || !cycle?.endDate) return 0;

    const start = new Date(cycle.startDate);
    const end = new Date(cycle.endDate);

    if (start.getTime() === end.getTime()) {
      return 1;
    }

    const diffTime = end.getTime() - start.getTime();
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;

    return diffDays;
  };

  /**
   * Returns the CSS class for cycle status.
   * @param {string} state - Cycle state
   * @returns {string} Status class
   */
  const getStatusClass = (state) => {
    return state === "OPEN" ? "open" : "closed";
  };

  /**
   * Returns the translated status text for cycle.
   * @param {string} state - Cycle state
   * @returns {string} Status text
   */
  const getStatusText = (state) => {
    return state === "OPEN" ? t("cycles.statusOpen") : t("cycles.statusClosed");
  };

  /**
   * Maps appraisal state to translated status text.
   * @param {string} state - Appraisal state
   * @returns {string} Status text
   */
  const getAppraisalStatusText = (state) => {
    switch (state) {
      case "COMPLETED":
        return t("cycles.statusCompleted");
      case "IN_PROGRESS":
        return t("cycles.statusInProgress");
      case "CLOSED":
        return t("cycles.statusClosed");
      default:
        return t("cycles.statusPending");
    }
  };

  /**
   * Gets the display name for the appraised user in an appraisal.
   * @param {Object} appraisal - Appraisal object
   * @returns {string} User display name
   */
  const getAppraisalUserName = (appraisal) => {
    if (appraisal.appraisedUser) {
      const user = appraisal.appraisedUser;
      if (user.name && user.surname) {
        return `${user.name} ${user.surname}`;
      }
      if (user.name) {
        return user.name;
      }
      if (user.email) {
        return user.email;
      }
    }
    if (appraisal.appraisedUserId) {
      return `${t("cycles.userId")} ${appraisal.appraisedUserId}`;
    }
    return t("cycles.na");
  };

  if (!shouldRender || !cycle) return null;

  return (
    <div
      className={`cycle-details-offcanvas-backdrop ${
        isAnimating ? "open" : ""
      }`}
      onClick={handleBackdropClick}
    >
      <div className={`cycle-details-offcanvas ${isAnimating ? "open" : ""}`}>
        <button className="cycle-details-offcanvas-close" onClick={onClose}>
          <FaTimes />
        </button>

        <div className="cycle-details-offcanvas-content">
          <div className="cycle-details-header">
            <h2 className="cycle-details-title">
              {t("cycles.cycleTitle", { id: cycle.id })}
            </h2>
            <span
              className={`cycle-details-status ${getStatusClass(cycle.state)}`}
            >
              {getStatusText(cycle.state)}
            </span>
          </div>

          <div className="cycle-details-info">
            <div className="cycle-details-section">
              <h3 className="cycle-details-section-title">
                {t("cycles.dateRange")}
              </h3>
              <div className="cycle-details-date-info">
                <div className="cycle-details-date-item">
                  <strong>{t("cycles.startDate")}:</strong>
                  {formatDate(cycle.startDate)}
                </div>
                <div className="cycle-details-date-item">
                  <strong>{t("cycles.endDate")}:</strong>
                  {formatDate(cycle.endDate)}
                </div>
              </div>
            </div>

            <div className="cycle-details-section">
              <h3 className="cycle-details-section-title">
                {t("cycles.summary")}
              </h3>
              <div className="cycle-details-summary">
                <div className="cycle-details-summary-item">
                  <span className="cycle-details-summary-label">
                    {t("cycles.duration")}:
                  </span>
                  <span className="cycle-details-summary-value">
                    {t("cycles.daysDuration", { days: calculateDays() })}
                  </span>
                </div>
                <div className="cycle-details-summary-item">
                  <span className="cycle-details-summary-label">
                    {t("cycles.appraisals")}:
                  </span>
                  <span className="cycle-details-summary-value">
                    {/* MUDANÇA: Usar evaluations em vez de evaluations */}
                    {cycle.evaluations?.length || 0}
                  </span>
                </div>
                <div className="cycle-details-summary-item">
                  <span className="cycle-details-summary-label">
                    {t("cycles.status")}:
                  </span>
                  <span className="cycle-details-summary-value">
                    {getStatusText(cycle.state)}
                  </span>
                </div>
              </div>
            </div>

            {/* MUDANÇA: Usar evaluations e adaptar estrutura */}
            {cycle.evaluations && cycle.evaluations.length > 0 && (
              <div className="cycle-details-section">
                <h3 className="cycle-details-section-title">
                  {t("cycles.appraisalsList")}
                </h3>
                <div className="cycle-details-evaluations">
                  {cycle.evaluations.map((appraisal, index) => (
                    <div key={index} className="cycle-details-evaluation-item">
                      <span className="cycle-details-evaluation-user">
                        {getAppraisalUserName(appraisal)}
                      </span>
                      <span className="cycle-details-evaluation-status">
                        {getAppraisalStatusText(appraisal.state)}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CycleDetailsOffcanvas;
