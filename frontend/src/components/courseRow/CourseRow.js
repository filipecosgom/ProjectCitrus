/**
 * @file CourseRow.js
 * @module CourseRow
 * @description Row component for displaying a course in search results.
 * @author Project Citrus Team
 */

import React, { useEffect, useState } from "react";
import courseTemplateImage from "../../assets/templates/courseTemplate.png";
import handleGetCourseImage from "../../handles/handleGetCourseImage";
import "./CourseRow.css";
import { useTranslation } from "react-i18next";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import flagEs from "../../assets/flags/flag-es.png";
import flagFr from "../../assets/flags/flag-fr.png";
import flagIt from "../../assets/flags/flag-it.png";

/**
 * Returns the flag image for a given language code.
 * @param {string} language - Language code
 * @returns {string} Path to flag image
 */
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

/**
 * CourseRow component for displaying a course in search results.
 * @param {Object} props - Component props
 * @param {Object} props.course - Course data object
 * @param {Function} props.onSelect - Callback when course is selected
 * @returns {JSX.Element}
 */
const CourseRow = ({ course, onSelect }) => {
  const { t } = useTranslation();
  const isInactive = course?.courseIsActive === false;
  const [courseImageUrl, setCourseImageUrl] = useState(null);
  const [loading, setLoading] = useState(false);

  /**
   * Loads course image if available, manages blob URL cleanup.
   */
  useEffect(() => {
    let courseBlobUrl = null;
    if (!course || !course.id || !course.courseHasImage) {
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
  }, [course?.id, course?.courseHasImage]);

  return (
    <div
      className={`course-search-row${isInactive ? " inactive" : ""}`}
      onClick={() => !isInactive && onSelect(course)}
      style={{ cursor: isInactive ? "not-allowed" : "pointer" }}
    >
      <div className="course-search-image">
        <img
          src={
            course.courseHasImage && courseImageUrl
              ? courseImageUrl
              : courseTemplateImage
          }
          alt={course.title}
          onError={(e) => {
            e.target.src = courseTemplateImage;
          }}
        />
      </div>
      <div className="course-search-info">
        <span className="course-search-title">{course.title}</span>
        <span className="course-search-area">{course.area}</span>
        <span className="course-search-duration">{course.duration}h</span>
      </div>
      <div className="course-search-language">
        <img
          src={getLanguageFlag(course.language)}
          alt={course.language}
          className="course-search-flag"
        />
      </div>
    </div>
  );
};

export default CourseRow;
