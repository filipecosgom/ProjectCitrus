import React, { useState, useCallback } from "react";
import { FaSearch } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import { handleGetCourses } from "../../handles/handleGetCourses";
import Spinner from "../spinner/Spinner";
import courseTemplateImage from "../../assets/templates/courseTemplate.png";
import CourseRow from "../courseRow/CourseRow";
import "./CourseSearchBar.css";

function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}


const CourseSearchBar = ({
  onCourseSelect,
  maxResults = 50,
  excludeCourseIds = [],
  excludeCompletedByUserId = null,
  className = "",
}) => {
  const { t } = useTranslation();
  const [courses, setCourses] = useState([]);
  const [coursesLoading, setCoursesLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [isOpen, setIsOpen] = useState(false);

  const debouncedSearch = useCallback(
    debounce((query) => {
      searchCourses(query);
    }, 300),
    [excludeCourseIds, maxResults, excludeCompletedByUserId]
  );

  const searchCourses = async (query = "") => {
    setCoursesLoading(true);
    try {
      const searchParams = {
        query,
        searchType: "title",
        offset: 0,
        limit: maxResults,
        courseIsActive: true,
        excludeCompletedByUserId,
      };
      const result = await handleGetCourses(searchParams);
      if (result && result.courses) {
        // Exclude completed courses by id array (legacy/local)
        const filtered = result.courses.filter(
          (c) => !excludeCourseIds.includes(c.id) && c.courseIsActive !== false
        );
        setCourses(filtered);
      } else {
        setCourses([]);
      }
    } catch (error) {
      setCourses([]);
    } finally {
      setCoursesLoading(false);
    }
  };

  const handleSearchChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    setIsOpen(query.length > 0);
    if (query.trim()) {
      debouncedSearch(query);
    } else {
      setCourses([]);
    }
  };

  const handleCourseSelect = (course) => {
    setSearchQuery("");
    setIsOpen(false);
    onCourseSelect(course);
  };

  const handleClear = () => {
    setSearchQuery("");
    setCourses([]);
    setIsOpen(false);
  };

  const handleFocus = () => {
    if (searchQuery && courses.length > 0) {
      setIsOpen(true);
    }
  };

  const handleBlur = () => {
    setTimeout(() => {
      setIsOpen(false);
    }, 200);
  };

  return (
    <div className={`course-search-bar ${className}`}>
      <div className="course-search-input-wrapper">
        <FaSearch className="search-icon" />
        <input
          type="text"
          placeholder={t("courses.searchPlaceholder")}
          value={searchQuery}
          onChange={handleSearchChange}
          onFocus={handleFocus}
          onBlur={handleBlur}
          className="course-search-input"
        />
        {searchQuery && (
          <button
            onClick={handleClear}
            className="course-search-clear"
            title={t("courses.clear")}
          >
            ✕
          </button>
        )}
      </div>
      {isOpen && (
        <div className="course-search-dropdown">
          {coursesLoading ? (
            <div className="course-search-loading">
              <Spinner size="small" />
              <span>{t("courses.searching")}</span>
            </div>
          ) : courses.length === 0 ? (
            <div className="course-search-empty">
              <p>
                {searchQuery
                  ? t("courses.noResults", { query: searchQuery })
                  : t("courses.noAvailable")}
              </p>
            </div>
          ) : (
            <div className="course-search-results">
              {courses.map((course) => (
                <CourseRow
                  key={course.id}
                  course={course}
                  onSelect={handleCourseSelect}
                />
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default CourseSearchBar;
