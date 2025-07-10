import React, { useState, useEffect } from "react";
import {
  FaTimes,
  FaClock,
  FaUser,
  FaCalendar,
  FaGraduationCap,
  FaPlay,
} from "react-icons/fa";
import { FaCircleCheck } from "react-icons/fa6";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import "./CourseDetailsOffcanvas.css";
import handleGetCourseImage from "../../handles/handleGetCourseImage";
import courseTemplateImage from "../../assets/templates/courseTemplate.png";
import { formatStringToDate } from "../../utils/utilityFunctions";

const CourseDetailsOffcanvas = ({ isOpen, onClose, course }) => {
  const { t } = useTranslation();
  const hasImage = course?.courseHasImage ?? false;
  const [courseImageUrl, setCourseImageUrl] = useState(null);

  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [criationDate, setCriationDate] = useState(null);

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

  useEffect(() => {
    if (!course || !course.id) {
      setCourseImageUrl(null);
      return;
    }
    let courseBlobUrl = null;
    if (!hasImage) {
      setCourseImageUrl(null);
      return;
    }
    setShouldRender(false);
    handleGetCourseImage(course.id)
      .then((result) => {
        if (result.success && result.image) {
          courseBlobUrl = result.image;
          setCourseImageUrl(result.image);
        } else {
          setCourseImageUrl(null);
        }
      })
      .catch(() => setCourseImageUrl(null))
      .finally(() => setShouldRender(true));
    return () => {
      if (courseBlobUrl?.startsWith("blob:"))
        URL.revokeObjectURL(courseBlobUrl);
    };
  }, [course?.id, hasImage]);

  useEffect(() => {
    if (course?.creationDate) {
      setCriationDate(formatStringToDate(course.creationDate));
    } else {
      setCriationDate(null);
    }
  }, [course?.creationDate]);

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
    return language === "PORTUGUESE" ? flagPt : flagEn;
  };

  const getLanguageLabel = (language) => {
    return language === "PORTUGUESE"
      ? t("courses.filterPortuguese")
      : t("courses.filterEnglish");
  };

  const formatDuration = (hours) => {
    if (hours < 1) {
      return t("courses.duration.minutes", {
        minutes: Math.round(hours * 60),
      });
    }
    return t("courses.duration.hours", { hours });
  };

  if (!shouldRender || !course) return null;

  return (
    <div
      className={`course-details-offcanvas-backdrop ${
        isAnimating ? "open" : ""
      }`}
      onClick={handleBackdropClick}
    >
      <div className={`course-details-offcanvas ${isAnimating ? "open" : ""}`}>
        <FaTimes className="course-details-offcanvas-close" onClick={onClose} />
        <div className="course-details-offcanvas-content">
          <div className="course-details-image">
            <img
              src={
                hasImage && courseImageUrl
                  ? courseImageUrl
                  : courseTemplateImage
              }
              alt={`${course.title}`}
              onError={(e) => {
                // ✅ FALLBACK se imagem falhar
                e.target.src = courseTemplateImage;
              }}
            />
          </div>

          <h1 className="course-details-title">{course.title}</h1>

          <div className="course-details-info">
            <div className="course-details-info-item">
              <FaGraduationCap className="course-details-icon" />
              <span className="course-details-label">{t("courses.area")}:</span>
              <span className="course-details-value">{course.area}</span>
            </div>

            <div className="course-details-info-item">
              <img
                src={getLanguageFlag(course.language)}
                alt={course.language}
                className="course-details-flag"
              />
              <span className="course-details-label">
                {t("courses.language")}:
              </span>
              <span className="course-details-value">
                {getLanguageLabel(course.language)}
              </span>
            </div>

            <div className="course-details-info-item">
              <FaClock className="course-details-icon" />
              <span className="course-details-label">
                {t("courses.sortDuration")}:
              </span>
              <span className="course-details-value">
                {formatDuration(course.duration)}
              </span>
            </div>

            <div className="course-details-info-item">
              <FaCalendar className="course-details-icon" />
              <span className="course-details-label">
                {t("courses.createdDate") || "Criado a"}:
              </span>
              <span className="course-details-value">
                {course.creationDate ? criationDate : t("profilePlaceholderNA")}
              </span>
            </div>

            {course.completionDate && (
              <div className="course-details-info-item">
                <FaCircleCheck className="course-details-icon" />
                <span className="course-details-label">
                  {t("courses.completionDate") || "Concluído a"}:
                </span>
                <span className="course-details-value">
                  {course.creationDate
                    ? criationDate
                    : t("profilePlaceholderNA")}
                </span>
              </div>
            )}
          </div>

          <div className="course-details-section">
            <h3 className="course-details-section-title">
              {t("courses.description")}
            </h3>
            <div className="course-details-description">
              <p>
                {course.description ||
                  `${t("courses.details")}: ${course.title}.`}
              </p>
            </div>
          </div>

          <div className="course-details-actions">
            <Link to={course.link} className="course-details-start-btn">
              <FaPlay className="course-details-play-icon" />
              {t("courses.startCourse", t("course.startCourse"))}
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CourseDetailsOffcanvas;
