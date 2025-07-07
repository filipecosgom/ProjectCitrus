import React, { useState, useEffect } from "react";
import {
  FaTimes,
  FaClock,
  FaUser,
  FaCalendar,
  FaGraduationCap,
  FaPlay,
} from "react-icons/fa";
import { useTranslation } from "react-i18next";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import "./TrainingDetailsOffcanvas.css";

const TrainingDetailsOffcanvas = ({ isOpen, onClose, training }) => {
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
      }, 400);
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

  const getLanguageFlag = (language) => {
    return language === "pt" ? flagPt : flagEn;
  };

  const getLanguageLabel = (language) => {
    return language === "pt"
      ? t("training.languages.pt")
      : t("training.languages.en");
  };

  const formatDuration = (hours) => {
    if (hours < 1) {
      return t("training.duration.minutes", {
        minutes: Math.round(hours * 60),
      });
    }
    return t("training.duration.hours", { hours });
  };

  const handleStartCourse = () => {
    console.log("Iniciar curso:", training.title);
  };

  if (!shouldRender || !training) return null;

  return (
    <div
      className={`training-details-offcanvas-backdrop ${
        isAnimating ? "open" : ""
      }`}
      onClick={handleBackdropClick}
    >
      <div
        className={`training-details-offcanvas ${isAnimating ? "open" : ""}`}
      >
        <button className="training-details-offcanvas-close" onClick={onClose}>
          <FaTimes />
        </button>

        <div className="training-details-offcanvas-content">
          <div className="training-details-image">
            <img
              src={training.image}
              alt={training.title}
              onError={(e) => {
                e.target.src =
                  "https://picsum.photos/400/200?random=" + training.id;
              }}
            />
          </div>

          <h1 className="training-details-title">{training.title}</h1>

          <div className="training-details-info">
            <div className="training-details-info-item">
              <FaGraduationCap className="training-details-icon" />
              <span className="training-details-label">
                {t("training.filters.category")}:
              </span>
              <span className="training-details-value">
                {training.category}
              </span>
            </div>

            <div className="training-details-info-item">
              <img
                src={getLanguageFlag(training.language)}
                alt={training.language}
                className="training-details-flag"
              />
              <span className="training-details-label">
                {t("training.filters.language")}:
              </span>
              <span className="training-details-value">
                {getLanguageLabel(training.language)}
              </span>
            </div>

            <div className="training-details-info-item">
              <FaClock className="training-details-icon" />
              <span className="training-details-label">Duração:</span>
              <span className="training-details-value">
                {formatDuration(training.duration)}
              </span>
            </div>

            <div className="training-details-info-item">
              <FaUser className="training-details-icon" />
              <span className="training-details-label">Instrutor:</span>
              <span className="training-details-value">
                {training.instructor || "N/A"}
              </span>
            </div>

            <div className="training-details-info-item">
              <FaCalendar className="training-details-icon" />
              <span className="training-details-label">Criado em:</span>
              <span className="training-details-value">
                {training.createdDate || "N/A"}
              </span>
            </div>
          </div>

          <div className="training-details-section">
            <h3 className="training-details-section-title">Descrição</h3>
            <div className="training-details-description">
              <p>
                {training.description ||
                  `Este é um curso completo de ${
                    training.title
                  } que abrange todos os conceitos fundamentais e avançados. 
                  Aprenda com exemplos práticos e desenvolva suas habilidades em ${training.category.toLowerCase()}.`}
              </p>
            </div>
          </div>

          <div className="training-details-section">
            <h3 className="training-details-section-title">
              Objetivos de Aprendizagem
            </h3>
            <div className="training-details-objectives">
              <ul>
                <li>Dominar os conceitos fundamentais de {training.title}</li>
                <li>Aplicar conhecimentos em projetos práticos</li>
                <li>Desenvolver competências em {training.category}</li>
                <li>Preparar-se para desafios profissionais</li>
              </ul>
            </div>
          </div>

          <div className="training-details-section">
            <h3 className="training-details-section-title">Pré-requisitos</h3>
            <div className="training-details-prerequisites">
              <ul>
                <li>Conhecimentos básicos de programação</li>
                <li>Familiaridade com desenvolvimento web</li>
                <li>Vontade de aprender e praticar</li>
              </ul>
            </div>
          </div>

          <div className="training-details-actions">
            <button
              className="training-details-start-btn"
              onClick={handleStartCourse}
            >
              <FaPlay className="training-details-play-icon" />
              {t("training.startCourse")}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TrainingDetailsOffcanvas;
