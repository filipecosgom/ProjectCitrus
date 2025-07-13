import React, { useEffect, useState } from "react";
import { FaBuromobelexperte } from "react-icons/fa6";
import { useTranslation } from "react-i18next";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import flagEs from "../../assets/flags/flag-es.png";
import flagFr from "../../assets/flags/flag-fr.png";
import flagIt from "../../assets/flags/flag-it.png";
import "./CourseCard.css";
import handleGetCourseImage from "../../handles/handleGetCourseImage";
import courseTemplateImage from "../../assets/templates/courseTemplate.png";
import Spinner from "../spinner/Spinner";

const CourseCard = ({ course, onViewDetails }) => {
  const { t } = useTranslation();
  const hasImage = course?.courseHasImage ?? false;
  const [courseImageUrl, setCourseImageUrl] = useState(null);
  const [loading, setLoading] = useState(false);

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
    setLoading(true);
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
      .finally(() => setLoading(false));
    return () => {
      if (courseBlobUrl?.startsWith("blob:"))
        URL.revokeObjectURL(courseBlobUrl);
    };
  }, [course?.id, hasImage]);

  const handleViewCourse = () => {
    // MUDANÇA: Chamar callback para abrir offcanvas
    if (onViewDetails) {
      onViewDetails(course);
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

  const toTitleCase = (str) =>
    str.replace(/\w\S*/g, (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());

  if (loading) return <Spinner />;

  const isInactive = course?.courseIsActive === false;

  return (
    <div className={`course-card${isInactive ? " course-card-inactive" : ""}`}> 
      <div className="course-card-image">
        <img
          src={
            hasImage && courseImageUrl ? courseImageUrl : courseTemplateImage
          }
          alt={`${course.title}`}
          onError={(e) => {
            // ✅ FALLBACK se imagem falhar
            e.target.src = courseTemplateImage;
          }}
          className={isInactive ? "course-card-image-inactive" : ""}
        />
        {isInactive && <div className="course-card-overlay" />}
      </div>

      <div className="course-card-content">
        <h3 className="course-card-title">
          {course.title} {isInactive && <span className="course-card-inactive-label">({t("courses.inactive")})</span>}
        </h3>

        <div className="course-card-info">
          <span className="course-card-category">{toTitleCase(course.area)}</span>

          <div className="course-card-language">
            <img
              src={getLanguageFlag(course.language)}
              alt={course.language}
              className="course-card-flag"
            />
          </div>
        </div>

        <button className="course-card-button" onClick={handleViewCourse}>
          {t("courses.viewCourse")}
        </button>
      </div>
    </div>
  );
};

export default CourseCard;
