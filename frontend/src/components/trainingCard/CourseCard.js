import React, { useEffect, useState } from "react";
import { FaClock } from "react-icons/fa6";
import { useTranslation } from "react-i18next";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import "./CourseCard.css";
import handleGetCourseImage from "../../handles/handleGetCourseImage";
import image from "../../assets/templates/image.png";
import Spinner from "../../components/spinner/spinner";

const TrainingCard = ({ course, onViewDetails }) => {
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
      if (courseBlobUrl?.startsWith("blob:")) URL.revokeObjectURL(courseBlobUrl);
    };
  }, [course?.id, hasImage]);

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
      return t("training.duration.minutes", {
        minutes: Math.round(hours * 60),
      });
    }
    return t("training.duration.hours", { hours });
  };

  if(loading) return <Spinner />;

  return (
    <div className="training-card">
      <div className="training-card-image">
        <img
        src={
          hasImage && courseImageUrl
            ? courseImageUrl
            : image
        }
        alt={`${course.title}`}
        onError={(e) => {
          // ✅ FALLBACK se imagem falhar
          e.target.src = image;
        }}
      />
      </div>

      <div className="training-card-content">
        <h3 className="training-card-title">{course.title}</h3>

        <div className="training-card-info">
          <span className="training-card-category">{course.area}</span>

          <div className="training-card-language">
            <img
              src={getLanguageFlag(course.language)}
              alt={course.language}
              className="training-card-flag"
            />
          </div>

          <div className="training-card-duration">
            <FaClock className="training-card-clock-icon" />
            <span>{formatDuration(course.duration)}</span>
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
