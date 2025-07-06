import React from "react";
import { useTranslation } from "react-i18next";
import "./CycleCard.css";

const CycleCard = ({ cycle, onCloseCycle }) => {
  const { t } = useTranslation();

  const formatDate = (dateString) => {
    if (!dateString) return t("cycles.na");

    const date = new Date(dateString);
    return date.toLocaleDateString("pt-PT", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  const getStatusClass = (state) => {
    return state === "OPEN" ? "active" : "closed";
  };

  const getStatusText = (state) => {
    return state === "OPEN" ? t("cycles.statusOpen") : t("cycles.statusClosed");
  };

  const handleCloseCycle = () => {
    onCloseCycle(cycle.id);
  };

  return (
    <div className="cycle-card">
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
            <strong>{t("cycles.evaluations")}:</strong>{" "}
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
