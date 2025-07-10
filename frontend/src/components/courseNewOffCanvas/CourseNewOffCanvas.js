import React, { useEffect, useState, useRef } from "react";
import { useForm } from "react-hook-form";
import { FaTimes, FaPlus, FaPen } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import "./CourseNewOffCanvas.css";
import handleNotification from "../../handles/handleNotification";
import { handleGetCourseAreas } from "../../handles/handleGetEnums";
import { handleCreateNewCourse, handleUploadCourseImage } from "../../handles/handleCreateNewCourse";

const LANGUAGES = [
  {
    code: "pt",
    label: "Português",
    flag: require("../../assets/flags/flag-pt.png"),
  },
  {
    code: "en",
    label: "English",
    flag: require("../../assets/flags/flag-en.png"),
  },
];

const CourseNewOffCanvas = ({ isOpen, onClose, onSubmit }) => {
  const { t } = useTranslation();
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [imagePreview, setImagePreview] = useState(null);
  const [imageFile, setImageFile] = useState(null);
  const [language, setLanguage] = useState("pt");
  const [areaOptions, setAreaOptions] = useState([]);
  const courseTemplateImage = require("../../assets/templates/courseTemplate.png");

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm({
    defaultValues: {
      title: "",
      area: "",
      language: "pt",
      description: "",
      link: "",
      courseHasImage: false,
      courseIsActive: true,
    },
  });

  useEffect(() => {
    setValue("language", language);
  }, [language, setValue]);

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
    if (isOpen) {
      reset();
      setImagePreview(null);
      setImageFile(null);
      setLanguage("pt");
    }
  }, [isOpen, reset]);

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

  useEffect(() => {
    // Fetch course areas
    handleGetCourseAreas().then((areas) => setAreaOptions(areas || []));
  }, [isOpen]);

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const onFormSubmit = async (data) => {
    // Remove image from course data for backend
    const { image, ...courseData } = {
      ...data
    };
    let createdCourseId = null;
    let courseResult = await handleCreateNewCourse(courseData);
    console.log("Course creation result:", courseResult);

    // If course creation fails, do not proceed to image upload or close offcanvas
    if (!courseResult.success) {
      return; // Error notification is already handled in the handler
    }

    createdCourseId = courseResult.data?.data?.id;
    if (imageFile && createdCourseId) {
      await handleUploadCourseImage(createdCourseId, imageFile);
    }
    handleNotification("success", "courses.courseCreated");
    onClose();
    if (onSubmit && courseResult.data?.data) {
      const courseObj = { ...courseResult.data.data };
      if (imageFile) courseObj.courseHasImage = true;
      onSubmit(courseObj);
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const validTypes = ["image/jpeg", "image/png", "image/webp"];
    const maxSize = 5 * 1024 * 1024; // 5mb
    if (!validTypes.includes(file.type)) {
      handleNotification("error", "invalidImageType");
      return;
    }
    if (file.size > maxSize) {
      handleNotification("error", "imageTooLarge");
      return;
    }
    setImageFile(file);
    setImagePreview(URL.createObjectURL(file));
  };

  const errorMessages = {
    title: t("courses.errorTitleRequired", "Title is required"),
    area: t("courses.errorAreaRequired", "Area is required"),
    language: t("courses.errorLanguageRequired", "Language is required"),
    description: t(
      "courses.errorDescriptionRequired",
      "Description is required"
    ),
    link: t("courses.errorLinkRequired", "Link is required"),
    linkInvalid: t("courses.errorLinkInvalid", "Invalid URL"),
    duration: t("courses.errorDurationRequired", "Duration is required"),
    durationInvalid: t(
      "courses.errorDurationInvalid",
      "Duration must be a positive number"
    ),
  };

  if (!shouldRender) return null;

  return (
    <div
      className={`course-newCourse-offcanvas-backdrop${
        isAnimating ? " open" : ""
      }`}
      onClick={handleBackdropClick}
    >
      <div
        className={`course-newCourse-offcanvas${isAnimating ? " open" : ""}`}
      >
        <FaTimes
          className="course-newCourse-offcanvas-close"
          onClick={onClose}
        />
        <div className="course-newCourse-offcanvas-content">
          <div className="course-newCourse-image">
            <img
              src={imagePreview || courseTemplateImage}
              alt={t("courses.imagePreviewAlt")}
            />
            <label
              htmlFor="course-image-upload"
              className="course-newCourse-edit-label"
            >
              <FaPen
                className="course-newCourse-edit-icon"
                title={t("courses.editImage")}
              />
            </label>
            <input
              type="file"
              accept="image/*"
              id="course-image-upload"
              onChange={handleImageChange}
            />
          </div>
          <form
            className="course-newCourse-form"
            onSubmit={handleSubmit(onFormSubmit)}
          >
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">
                {t("courses.newTitle")}:
              </span>
              <div className="course-newCourse-inputAndError">
                <input
                  className="course-newCourse-input"
                  type="text"
                  {...register("title", { required: errorMessages.title })}
                />
                <span className="error-message">
                  {errors.title ? errors.title.message : "\u00A0"}
                </span>
              </div>
            </div>
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">
                {t("courses.area")}:
              </span>
              <div className="course-newCourse-inputAndError">
                <select
                  className="course-newCourse-input"
                  {...register("area", { required: errorMessages.area })}
                  defaultValue=""
                >
                  <option value="" disabled>
                    {t("courses.selectArea")}
                  </option>
                  {areaOptions.map((area) => (
                    <option key={area} value={area}>
                      {area}
                    </option>
                  ))}
                </select>
                <span className="error-message">
                  {errors.area ? errors.area.message : "\u00A0"}
                </span>
              </div>
            </div>
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">
                {t("courses.language")}:
              </span>
              <div className="course-newCourse-inputAndError">
                  <LanguageDropdownForForm
                    value={language}
                    onChange={setLanguage}
                  />
                <span className="error-message">
                  {errors.language ? errors.language.message : "\u00A0"}
                </span>
                </div>
            </div>

            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">
                {t("courses.duration.label")}:
              </span>
              <div
                className="course-newCourse-inputAndError"
              >
                <div className="course-newCourse-duration">
                <input
                  className="course-newCourse-input-duration"
                  type="number"
                  min="0"
                  step="1"
                  {...register("duration", {
                    required: errorMessages.duration,
                    min: { value: 1, message: errorMessages.durationInvalid },
                    valueAsNumber: true,
                    validate: (v) =>
                      Number.isInteger(v) && v > 0
                        ? true
                        : errorMessages.durationInvalid,
                  })}
                />
                <span>
                  {t("courses.duration.hoursShort", "h")}
                </span>
                </div>
                <span className="error-message" style={{ flexBasis: "100%" }}>
                  {errors.duration ? errors.duration.message : "\u00A0"}
                </span>
              </div>
            </div>

            <div className="course-newCourse-section">
              <span className="course-newCourse-section-title">
                {t("courses.newDescription")}:
              </span>
              <div className="course-newCourse-inputAndError">
                <div className="course-newCourse-description">
                  <textarea
                    className="course-newCourse-input"
                    {...register("description", {
                      required: errorMessages.description,
                    })}
                  />
                  <span className="error-message">
                    {errors.description ? errors.description.message : "\u00A0"}
                  </span>
                </div>
              </div>
            </div>

            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">
                {t("courses.link")}:
              </span>
              <div className="course-newCourse-inputAndError">
                <input
                  className="course-newCourse-input"
                  type="text"
                  {...register("link", {
                    required: errorMessages.link,
                    validate: (value) => {
                      if (!value) return errorMessages.link;
                      try {
                        new URL(value);
                        return true;
                      } catch {
                        return errorMessages.linkInvalid;
                      }
                    },
                  })}
                />
                <span className="error-message">
                  {errors.link ? errors.link.message : "\u00A0"}
                </span>
              </div>
            </div>

            <div className="course-newCourse-actions">
              <button type="submit" className="course-newCourse-start-btn">
                <FaPlus className="course-newCourse-play-icon" />
                {t("courses.createCourse", t("courses.addNewCourse"))}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

// Controlled dropdown for form use
function LanguageDropdownForForm({ value, onChange }) {
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);
  const selectedLang =
    LANGUAGES.find((lang) => lang.code === value) || LANGUAGES[0];

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="language-dropdown" ref={dropdownRef}>
      <div
        className="language-selected"
        onClick={() => setShowDropdown((v) => !v)}
      >
        <img
          src={selectedLang.flag}
          alt={selectedLang.label}
          className="language-flag"
          width={32}
          height={18}
        />
        <span className="language-label" style={{ marginLeft: 8 }}>
          {selectedLang.label}
        </span>
        <span
          className={`dropdown-arrow${showDropdown ? " open" : ""}`}
          style={{ marginLeft: "auto", fontSize: 16 }}
        >
          ▼
        </span>
      </div>
      {showDropdown && (
        <div className="language-options">
          {LANGUAGES.filter((l) => l.code !== value).map((lang) => (
            <div
              key={lang.code}
              className="language-option"
              onClick={() => {
                onChange(lang.code);
                setShowDropdown(false);
              }}
            >
              <img
                src={lang.flag}
                alt={lang.label}
                className="language-flag"
                width={32}
                height={18}
              />
              <span className="language-label" style={{ marginLeft: 8 }}>
                {lang.label}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default CourseNewOffCanvas;
