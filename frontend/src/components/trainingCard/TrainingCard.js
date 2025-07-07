import React from "react";
import { FaClock } from "react-icons/fa6";
import { useTranslation } from "react-i18next";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import "./TrainingCard.css";

const TrainingCard = ({ training, onViewDetails }) => {
  const { t } = useTranslation();

  const handleViewCourse = () => {
    // MUDANÇA: Chamar callback para abrir offcanvas
    if (onViewDetails) {
      onViewDetails(training);
    }
  };

  const getLanguageFlag = (language) => {
    return language === "pt" ? flagPt : flagEn;
  };

  const formatDuration = (hours) => {
    if (hours < 1) {
      return t("training.duration.minutes", {
        minutes: Math.round(hours * 60),
      });
    }
    return t("training.duration.hours", { hours });
  };

  return (
    <div className="training-card">
      <div className="training-card-image">
        <img
          src={training.image}
          alt={training.title}
          onError={(e) => {
            e.target.src = "https://picsum.photos/170/95?random=" + training.id;
          }}
        />
      </div>

      <div className="training-card-content">
        <h3 className="training-card-title">{training.title}</h3>

        <div className="training-card-info">
          <span className="training-card-category">{training.category}</span>

          <div className="training-card-language">
            <img
              src={getLanguageFlag(training.language)}
              alt={training.language}
              className="training-card-flag"
            />
          </div>

          <div className="training-card-duration">
            <FaClock className="training-card-clock-icon" />
            <span>{formatDuration(training.duration)}</span>
          </div>
        </div>

        <button className="training-card-button" onClick={handleViewCourse}>
          {t("training.viewCourse")}
        </button>
      </div>
    </div>
  );
};

export default TrainingCard;
