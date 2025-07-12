import React, { useEffect, useState } from "react";
import { FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import Spinner from "../spinner/spinner";
import CourseSearchBar from "../courseSearchBar/CourseSearchBar";
import CourseRow from "../courseRow/CourseRow";
import "./AddCompletedCourseOffcanvas.css";
import handleNotification from "../../handles/handleNotification";
import { handleAddCompletedCourseToUser } from "../../handles/handleAddCompletedCourseToUser";

const AddCompletedCourseOffcanvas = ({
  isOpen,
  onClose,
  onAdd,
  availableCourses = [],
  userId,
  userName,
  userSurname
}) => {
  const { t } = useTranslation();
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [selectedCourses, setSelectedCourses] = useState([]);
  const [isAdding, setIsAdding] = useState(false);

  // Handler for course selection from CourseSearchBar
  const handleCourseSelect = (course) => {
    if (!course) return;
    setSelectedCourses((prev) => {
      if (prev.some((c) => c.id === course.id)) return prev;
      return [...prev, course];
    });
  };

  // Handler for Add button
  const handleAddClick = async () => {
    if (!selectedCourses.length || isAdding) {
      handleNotification("info", "courses.selectCourseFirst");
      return;
    }
    setIsAdding(true);
    try {
      console.log("Adding completed courses:", selectedCourses);
      const courseIds = selectedCourses.map((c) => c.id);
      await handleAddCompletedCourseToUser(userId, courseIds);
      // Notify parent with the new courses (if onAdd is provided)
      if (typeof onAdd === "function") {
        console.log("Calling onAdd with selected courses:", selectedCourses);
        await onAdd(selectedCourses);
        console.log("onAdd completed");
        onClose(); // Close after adding
      }
      setSelectedCourses([]);
    } catch (error) {
      console.error("Error adding completed courses:", error);
    } finally {
      setIsAdding(false);
      // Do not call onClose here; let parent control closing after onAdd
    }
  };

  // Reset state when closing
  useEffect(() => {
    console.log(userId)
    console.log("Offcanvas open state changed:", isOpen);
    if (!isOpen) {
      setSelectedCourses([]);
      setIsAdding(false);
    }
  }, [isOpen]);

  // Control render and animation
  useEffect(() => {
    if (isOpen) {
      setShouldRender(true);
      const timer = setTimeout(() => setIsAnimating(true), 10);
      return () => clearTimeout(timer);
    } else {
      setIsAnimating(false);
      const timer = setTimeout(() => setShouldRender(false), 400);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  // Control page scroll
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

  // Close with ESC
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") onClose();
    };
    if (isOpen) document.addEventListener("keydown", handleEsc);
    return () => document.removeEventListener("keydown", handleEsc);
  }, [isOpen, onClose]);

  // Close on backdrop click
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      setSelectedCourses([]);
      onClose();
    }
  };

  if (!shouldRender) return null;

  return (
    <div
      className={`add-completed-course-backdrop ${isAnimating ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div
        className={`add-completed-course-offcanvas ${
          isAnimating ? "open" : ""
        }`}
      >
        {/* Header */}
        <div className="add-completed-course-header">
          <h2 className="add-completed-course-title">
            {t("courses.addCompletedCourseTitle", { userName, userSurname })}
          </h2>
          <button
            className="add-completed-course-close"
            onClick={onClose}
            disabled={isAdding}
          >
            <FaTimes />
          </button>
        </div>

        {/* Content */}
        <div className="add-completed-course-content">
          <div className="select-course-section">
            <h3 className="section-title">{t("courses.selectCourse")}</h3>
            <CourseSearchBar
              onCourseSelect={handleCourseSelect}
              excludeCompletedByUserId={userId}
              maxResults={50}
              className="add-completed-course-searchbar"
              excludeCourseIds={selectedCourses.map((c) => c.id)}
            />
            {selectedCourses.length > 0 && (
              <div className="selected-course-feedback">
                {selectedCourses.map((course) => (
                  <div
                    key={course.id}
                    style={{ position: "relative", marginBottom: 8 }}
                  >
                    <CourseRow course={course} onSelect={() => {}} />
                    <button
                      type="button"
                      className="add-completed-course-remove-btn"
                      title={t("courses.remove")}
                      onClick={() =>
                        setSelectedCourses((prev) =>
                          prev.filter((c) => c.id !== course.id)
                        )
                      }
                      disabled={isAdding}
                    >
                      ×
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="add-completed-course-footer">
          <button
            className="add-completed-course-btn add-completed-course-btn-add"
            onClick={handleAddClick}
            disabled={!selectedCourses.length || isAdding}
          >
            {isAdding ? (
              <>
                <Spinner size="small" />
                {t("courses.adding")}
              </>
            ) : (
              t("courses.addCompletedCourse")
                   )}
          </button>
          <button
            className="add-completed-course-btn add-completed-course-btn-cancel"
            onClick={onClose}
            disabled={isAdding}
          >
            {t("courses.cancel")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddCompletedCourseOffcanvas;
