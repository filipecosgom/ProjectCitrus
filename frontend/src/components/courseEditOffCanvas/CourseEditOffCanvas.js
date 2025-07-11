import React, { useState, useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { FaTimes, FaPen } from "react-icons/fa";
import handleUpdateCourse from "../../handles/handleUpdateCourse";
import {handleGetCourseAreas } from "../../handles/handleGetEnums";
import handleGetCourseImage from "../../handles/handleGetCourseImage";
import { handleUploadCourseImage } from "../../handles/handleCreateNewCourse";
import courseTemplateImage from "../../assets/templates/courseTemplate.png";
import "./CourseEditOffCanvas.css";

const LANGUAGES = [
  { code: "pt", label: "Português", flag: require("../../assets/flags/flag-pt.png") },
  { code: "en", label: "English", flag: require("../../assets/flags/flag-en.png") },
  { code: "es", label: "Español", flag: require("../../assets/flags/flag-es.png") },
  { code: "fr", label: "Français", flag: require("../../assets/flags/flag-fr.png") },
  { code: "it", label: "Italiano", flag: require("../../assets/flags/flag-it.png") },
];

const CourseEditOffCanvas = ({ isOpen, onClose, course, onSuccess }) => {
  const { t } = useTranslation();
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [imagePreview, setImagePreview] = useState(null);
  const [imageFile, setImageFile] = useState(null);
  const [language, setLanguage] = useState(course?.language || "pt");
  const [areaOptions, setAreaOptions] = useState([]);

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm({
    defaultValues: {
      ...course,
      language: course?.language || "pt",
    },
  });

  useEffect(() => {
    setValue("language", language);
  }, [language, setValue]);

  useEffect(() => {
    if (isOpen) {
      setShouldRender(true);
      setTimeout(() => setIsAnimating(true), 10);
      reset({ ...course, language: course?.language || "pt" });
      setLanguage(course?.language || "pt");
      setImagePreview(null);
    } else {
      setIsAnimating(false);
      setTimeout(() => setShouldRender(false), 400);
    }
  }, [isOpen, course, reset]);

  useEffect(() => {
    // Fetch course areas
    handleGetCourseAreas().then((areas) => setAreaOptions(areas || []));
  }, [isOpen]);

  useEffect(() => {
    // Set image preview to course image if available
    let courseBlobUrl = null;
    if (isOpen && course?.id && course?.courseHasImage) {
      handleGetCourseImage(course.id)
        .then((result) => {
          if (result.success && result.image) {
            courseBlobUrl = result.image;
            setImagePreview(result.image);
          } else {
            setImagePreview(null);
          }
        })
        .catch(() => setImagePreview(null));
    } else {
      setImagePreview(null);
    }
    return () => {
      if (courseBlobUrl?.startsWith("blob:")) URL.revokeObjectURL(courseBlobUrl);
    };
  }, [isOpen, course]);

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) onClose();
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const validTypes = ["image/jpeg", "image/png", "image/webp"];
    const maxSize = 5 * 1024 * 1024; // 5mb
    if (!validTypes.includes(file.type)) {
      // Optionally show notification
      return;
    }
    if (file.size > maxSize) {
      // Optionally show notification
      return;
    }
    setImageFile(file);
    setImagePreview(URL.createObjectURL(file));
  };

  const onFormSubmit = async (data) => {
  const updateData = { ...course, ...data, language };
  const result = await handleUpdateCourse(updateData);
  if (result.success && imageFile && course?.id) {
    await handleUploadCourseImage(course.id, imageFile);
  }
  if (result.success && onSuccess) onSuccess(updateData);
  if (result.success) onClose();
  };

  const errorMessages = {
    title: t("courses.errorTitleRequired", "Title is required"),
    area: t("courses.errorAreaRequired", "Area is required"),
    language: t("courses.errorLanguageRequired", "Language is required"),
    description: t("courses.errorDescriptionRequired", "Description is required"),
    link: t("courses.errorLinkRequired", "Link is required"),
    linkInvalid: t("courses.errorLinkInvalid", "Invalid URL"),
    duration: t("courses.errorDurationRequired", "Duration is required"),
    durationInvalid: t("courses.errorDurationInvalid", "Duration must be a positive number"),
  };

  if (!shouldRender) return null;

  return (
    <div
      className={`course-newCourse-offcanvas-backdrop${isAnimating ? " open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`course-newCourse-offcanvas${isAnimating ? " open" : ""}`}>
        <FaTimes className="course-newCourse-offcanvas-close" onClick={onClose} />
        <div className="course-newCourse-offcanvas-content">
          <div className="course-newCourse-image">
            <img
              src={imagePreview || courseTemplateImage}
              alt={t("courses.imagePreviewAlt")}
            />
            <label
              htmlFor="course-image-upload-edit"
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
              id="course-image-upload-edit"
              style={{ display: "none" }}
              onChange={handleImageChange}
            />
          </div>
          <form className="course-newCourse-form" onSubmit={handleSubmit(onFormSubmit)}>
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">{t("courses.newTitle")}:</span>
              <div className="course-newCourse-inputAndError">
                <input
                  className="course-newCourse-input"
                  type="text"
                  {...register("title", { required: errorMessages.title })}
                  defaultValue={course?.title || ""}
                />
                <span className="error-message">{errors.title ? errors.title.message : "\u00A0"}</span>
              </div>
            </div>
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">{t("courses.area")}:</span>
              <div className="course-newCourse-inputAndError">
                <select
                  className="course-newCourse-input"
                  {...register("area", { required: errorMessages.area })}
                  onChange={e => setValue("area", e.target.value)}
                >
                  <option value="" disabled>{t("courses.selectArea")}</option>
                  {areaOptions.map((enumVal) => (
                    <option key={enumVal} value={enumToAreaValue(enumVal)}>
                      {enumToLabel(enumVal)}
                    </option>
                  ))}
                </select>
                <span className="error-message">{errors.area ? errors.area.message : "\u00A0"}</span>
              </div>
            </div>
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">{t("courses.language")}:</span>
              <div className="course-newCourse-inputAndError">
                <LanguageDropdownForForm value={language} onChange={setLanguage} />
                <span className="error-message">{errors.language ? errors.language.message : "\u00A0"}</span>
              </div>
            </div>
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">{t("courses.duration.label")}:</span>
              <div className="course-newCourse-inputAndError">
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
                      validate: (v) => Number.isInteger(v) && v > 0 ? true : errorMessages.durationInvalid,
                    })}
                    defaultValue={course?.duration || ""}
                  />
                  <span>{t("courses.duration.hoursShort", "h")}</span>
                </div>
                <span className="error-message" style={{ flexBasis: "100%" }}>
                  {errors.duration ? errors.duration.message : "\u00A0"}
                </span>
              </div>
            </div>
            <div className="course-newCourse-section">
              <span className="course-newCourse-section-title">{t("courses.newDescription")}:</span>
              <div className="course-newCourse-inputAndError">
                <div className="course-newCourse-description">
                  <textarea
                    className="course-newCourse-input"
                    {...register("description", { required: errorMessages.description })}
                    defaultValue={course?.description || ""}
                  />
                  <span className="error-message">{errors.description ? errors.description.message : "\u00A0"}</span>
                </div>
              </div>
            </div>
            <div className="course-newCourse-info-item">
              <span className="course-newCourse-label">{t("courses.link")}:</span>
              <div className="course-newCourse-inputAndError">
                <input
                  className="course-newCourse-input"
                  type="text"
                  {...register("link", {
                    required: errorMessages.link,
                    validate: (value) => /^https?:\/\//.test(value) || errorMessages.linkInvalid,
                  })}
                  defaultValue={course?.link || ""}
                />
                <span className="error-message">{errors.link ? errors.link.message : "\u00A0"}</span>
              </div>
            </div>
            <div className="course-newCourse-actions">
              <button type="submit" className="course-newCourse-start-btn">
                <FaPen className="course-newCourse-play-icon" />
                {t("courses.saveChanges", "Save Changes")}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

// Utility to map enum to course area value and label
function enumToAreaValue(enumStr) {
  return enumStr.replace(/_/g, "/").toLowerCase();
}
function enumToLabel(enumStr) {
  return enumStr
    .replace(/_/g, "/")
    .replace(/\b([a-z])/g, (m) => m.toUpperCase())
    .replace("/Ui", "/UI")
    .replace("/Ux", "/UX");
}

function LanguageDropdownForForm({ value, onChange }) {
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);
  const selectedLang = LANGUAGES.find((lang) => lang.code === value) || LANGUAGES[0];

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
      <div className="language-selected" onClick={() => setShowDropdown((v) => !v)}>
        <img src={selectedLang.flag} alt={selectedLang.label} className="language-flag" width={32} height={18} />
        <span className="language-label" style={{ marginLeft: 8 }}>{selectedLang.label}</span>
        <span className={`dropdown-arrow${showDropdown ? " open" : ""}`} style={{ marginLeft: "auto", fontSize: 16 }}>▼</span>
      </div>
      {showDropdown && (
        <div className="language-options">
          {LANGUAGES.filter((l) => l.code !== value).map((lang) => (
            <div key={lang.code} className="language-option" onClick={() => { setShowDropdown(false); onChange(lang.code); }}>
              <img src={lang.flag} alt={lang.label} className="language-flag" width={32} height={18} />
              <span className="language-label" style={{ marginLeft: 8 }}>{lang.label}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default CourseEditOffCanvas;
