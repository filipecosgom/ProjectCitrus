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
import flagEs from "../../assets/flags/flag-es.png";
import flagFr from "../../assets/flags/flag-fr.png";
import flagIt from "../../assets/flags/flag-it.png";
import "./CourseDetailsOffcanvas.css";
import handleGetCourseImage from "../../handles/handleGetCourseImage";
import courseTemplateImage from "../../assets/templates/courseTemplate.png";
import { formatStringToDate } from "../../utils/utilityFunctions";
import handleUpdateCourse from "../../handles/handleUpdateCourse";
import useAuthStore from "../../stores/useAuthStore";
import ConfirmationModal from "../confirmationModal/ConfirmationModal";

const CourseDetailsOffcanvas = ({ isOpen, onClose, course, onCourseStatusChange }) => {
  const { t } = useTranslation();
  const hasImage = course?.courseHasImage ?? false;
  const [courseImageUrl, setCourseImageUrl] = useState(null);
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [criationDate, setCriationDate] = useState(null);
  const [editOpen, setEditOpen] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [pendingActiveState, setPendingActiveState] = useState(null);
  const userIsAdmin = useAuthStore((state) => state.isUserAdmin());

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
  }, [course, course?.id, hasImage]);

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
    switch (language) {
      case "pt":
        return flagPt;
      case "en":
        return flagEn;
      case "es":
        return flagEs;
      case "fr":
        return flagFr;
      case "it":
        return flagIt;
      default:
        return flagEn;
    }
  };

  const getLanguageLabel = (language) => {
    switch (language) {
      case "pt":
        return t("courses.filterPortuguese");
      case "en":
        return t("courses.filterEnglish");
      case "es":
        return t("courses.filterSpanish");
      case "fr":
        return t("courses.filterFrench");
      case "it":
        return t("courses.filterItalian");
      default:
        return language;
    }
  };

  const formatDuration = (hours) => {
    if (hours < 1) {
      return t("courses.duration.minutes", {
        minutes: Math.round(hours * 60),
      });
    }
    return t("courses.duration.hours", { hours });
  };

  const isInactive = course?.courseIsActive === false;

  const handleToggleActiveCourse = () => {
    setPendingActiveState(isInactive ? true : false);
    setShowConfirmModal(true);
  };

  const handleConfirmToggle = async () => {
    if (!course?.id) return;
    const updatedCourse = { ...course, courseIsActive: pendingActiveState };
    await handleUpdateCourse(updatedCourse);
    setShowConfirmModal(false);
    if (onCourseStatusChange) {
      onCourseStatusChange(updatedCourse);
    }
    onClose();
  };

  const handleCancelToggle = () => {
    setShowConfirmModal(false);
    setPendingActiveState(null);
  };

  if (!shouldRender || !course) return null;

  return (
    <>
      <div
        className={`course-details-offcanvas-backdrop ${isAnimating ? "open" : ""}`}
        onClick={handleBackdropClick}
      >
        <div className={`course-details-offcanvas ${isAnimating ? "open" : ""}${isInactive ? " course-details-offcanvas-inactive" : ""}`}>
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
                className={isInactive ? "course-details-image-inactive" : ""}
              />
              {isInactive && <div className="course-details-offcanvas-overlay" />}
            </div>

            <h1 className="course-details-title">
              {course.title} {isInactive && <span className="course-details-inactive-label"> - {t("courses.inactive")}</span>}
            </h1>

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
              {userIsAdmin && (
                <>
                  <button
                    className="course-details-edit-btn"
                    onClick={() => setEditOpen(true)}
                  >
                    {t("courses.editCourse", "Edit Course")}
                  </button>
                  <button
                    className={`course-details-inactivate-btn${isInactive ? " course-details-activate-btn" : ""}`}
                    onClick={handleToggleActiveCourse}
                    disabled={false}
                  >
                    {isInactive
                      ? t("courses.activateCourse")
                      : t("courses.inactivateCourse")}
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
      <ConfirmationModal
        isOpen={showConfirmModal}
        title={isInactive ? t("courses.activateCourse") : t("courses.inactivateCourse")}
        message1={isInactive ? t("courses.confirmActivate") : t("courses.confirmInactivate")}
        message2={course.title}
        onConfirm={handleConfirmToggle}
        onClose={handleCancelToggle}
      />
    </>
  );
};

export default CourseDetailsOffcanvas;
