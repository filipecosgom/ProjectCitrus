import React, { useEffect, useState } from "react";
import { FaBuromobelexperte, FaClock } from "react-icons/fa6";
import { useTranslation } from "react-i18next";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import "./CourseCard.css";
import handleGetCourseImage from "../../handles/handleGetCourseImage";
import courseTemplateImage from "../../assets/templates/courseTemplate.png";
import Spinner from "../spinner/spinner";
import {
  dateToFormattedDate,
  transformArrayLocalDatetoLocalDate,
} from "../../utils/utilityFunctions";

const CourseCard = ({ course, onViewDetails }) => {
  const { t } = useTranslation();
  const hasImage = course?.courseHasImage ?? false;
  const [courseImageUrl, setCourseImageUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [completionDate, setCompletionDate] = useState(null);

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

  useEffect(() => {
    if (course?.completionDate) {
      let formattedCompletionDate;
      if (Array.isArray(course.completionDate)) {
        const dateObj = transformArrayLocalDatetoLocalDate(
          course.completionDate
        );
        formattedCompletionDate = dateToFormattedDate(dateObj);
      } else {
        formattedCompletionDate = dateToFormattedDate(course.completionDate);
      }
      setCompletionDate(formattedCompletionDate);
    }
  }, [course?.completionDate]);

  const handleViewCourse = () => {
    // MUDANÇA: Chamar callback para abrir offcanvas
    if (onViewDetails) {
      onViewDetails(course);
    }
  };

  const getLanguageFlag = (language) => {
    return language === "pt" ? flagPt : flagEn;
  };

  const formatDuration = (hours) => {
    if (hours < 1) {
      return t("courses.duration.minutes", {
        minutes: Math.round(hours * 60),
      });
    }
    return t("courses.duration.hours", { hours });
  };

  if (loading) return <Spinner />;

  return (
    <div className="course-card">
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
        />
      </div>

      <div className="course-card-content">
        <h3 className="course-card-title">{course.title}</h3>

        <div className="course-card-info">
          <span className="course-card-category">{course.area}</span>

          <div className="course-card-language">
            <img
              src={getLanguageFlag(course.language)}
              alt={course.language}
              className="course-card-flag"
            />
          </div>

          <div className="course-card-duration">
            <FaClock className="course-card-clock-icon" />
            <span>{formatDuration(course.duration)}</span>
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
