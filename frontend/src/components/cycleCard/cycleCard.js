/**
 * @file CycleCard.js
 * @module CycleCard
 * @description Card component for displaying a cycle and its details.
 * @author Project Citrus Team
 */

import React from "react";
import { useTranslation } from "react-i18next";
import useLocaleStore from "../../stores/useLocaleStore";
import "./CycleCard.css";

/**
 * CycleCard component for displaying a cycle and its details.
 * @param {Object} props - Component props
 * @param {Object} props.cycle - Cycle data object
 * @param {Function} props.onCloseCycle - Callback to close the cycle
 * @param {Function} props.onCardClick - Callback when card is clicked
 * @returns {JSX.Element}
 */
const CycleCard = ({ cycle, onCloseCycle, onCardClick }) => {
  const { t } = useTranslation();
  const locale = useLocaleStore((state) => state.locale);

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
   * Returns the CSS class for cycle status.
   * @param {string} state - Cycle state
   * @returns {string} Status class
   */
  const getStatusClass = (state) => {
    return state === "OPEN" ? "active" : "closed";
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
   * Handles click on close cycle button.
   * @param {React.MouseEvent} e - Mouse event
   */
  const handleCloseCycle = (e) => {
    e.stopPropagation(); // Prevents card click event
    onCloseCycle(cycle.id);
  };

  /**
   * Handles click on the card.
   */
  const handleCardClick = () => {
    onCardClick(cycle);
  };

  return (
    <div className="cycle-card" onClick={handleCardClick}>
      <div className="cycle-card-header">
        <h3 className="cycle-card-title">
          {t("cycles.cycleTitle", { id: cycle.id })}
        </h3>
        <span className={`cycle-card-status ${getStatusClass(cycle.state)}`}>
          {getStatusText(cycle.state)}
        </span>
      </div>

      <div className="cycle-card-body">
        <div className="cycle-card-info">
          <p>
            <strong>{t("cycles.startDate")}:</strong>{" "}
            {formatDate(cycle.startDate)}
          </p>
          <p>
            <strong>{t("cycles.endDate")}:</strong> {formatDate(cycle.endDate)}
          </p>
          <p>
            <strong>{t("cycles.appraisals")}:</strong>{" "}
            {cycle.evaluations?.length || 0}
          </p>
        </div>
      </div>

      <div className="cycle-card-actions">
        {cycle.state === "OPEN" && (
          <button
            className="cycle-card-button cycle-card-button-close"
            onClick={handleCloseCycle}
          >
            {t("cycles.closeCycle")}
          </button>
        )}
      </div>
    </div>
  );
};

export default CycleCard;
