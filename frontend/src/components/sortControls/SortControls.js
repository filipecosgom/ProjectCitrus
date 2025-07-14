/**
 * @file SortControls.js
 * @module SortControls
 * @description Sort controls component for selecting sort field and order (A-Z/Z-A) for lists.
 * Supports desktop and mobile/card modes, integrates with translation and custom fields.
 * @author Project Citrus Team
 */

import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";
import { FaSortAmountDownAlt, FaSortAmountUpAlt, FaSort } from "react-icons/fa";
import "./SortControls.css";

/**
 * SortControls component for sorting lists by field and order.
 * @param {Object} props - Component props
 * @param {Array<{id: string, key: string, label?: Function}>} props.fields - List of sort fields
 * @param {string} props.sortBy - Current sort field key
 * @param {"ASCENDING"|"DESCENDING"} props.sortOrder - Current sort order
 * @param {Function} props.onSortChange - Callback when sort changes ({sortBy, sortOrder})
 * @param {string} [props.className] - Optional CSS class
 * @param {boolean} [props.isCardMode] - If true, uses mobile/card dropdown mode
 * @returns {JSX.Element} The rendered sort controls
 */
const SortControls = ({
  fields,
  sortBy,
  sortOrder,
  onSortChange,
  className = "",
  isCardMode = false,
}) => {
  const { t } = useTranslation();
  const isUsersMode = className.includes("users-mode");

  const [showDropdown, setShowDropdown] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 480);

  /**
   * Handles window resize to update mobile state.
   */
  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 480);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  /**
   * Handles sort field click, toggling sort order if same field.
   * @param {string} fieldKey - The field key to sort by
   */
  const handleSort = (fieldKey) => {
    const newOrder =
      sortBy === fieldKey && sortOrder === "ASCENDING"
        ? "DESCENDING"
        : "ASCENDING";
    onSortChange({ sortBy: fieldKey, sortOrder: newOrder });
  };

  /**
   * Returns the appropriate sort icon for a field.
   * @param {string} fieldKey - The field key
   * @returns {JSX.Element} Icon element
   */
  const getSortIcon = (fieldKey) => {
    if (sortBy !== fieldKey) {
      return <FaSort className="sort-icon" />;
    }
    return sortOrder === "ASCENDING" ? (
      <FaSortAmountDownAlt className="sort-icon" />
    ) : (
      <FaSortAmountUpAlt className="sort-icon" />
    );
  };

  /**
   * Returns the display fields based on mode.
   * @returns {Array<{id: string, key: string, label?: Function}>}
   */
  const getDisplayFields = () => {
    if (isUsersMode) {
      // For users: 4 columns (no state)
      return [
        { id: "userSortControlsName", key: "name" },
        { id: "userSortControlsRole", key: "role" },
        { id: "userSortControlsOffice", key: "office" },
        { id: "userSortControlsManager", key: "manager" },
      ];
    } else {
      // For appraisals: use fields from props (5 columns)
      return fields;
    }
  };

  const displayFields = getDisplayFields();
  const currentSortFieldObj =
    displayFields.find((f) => f.key === sortBy) || displayFields[0];

  // Mobile/card dropdown version
  if (isCardMode || isMobile) {
    return (
      <div className={`sortControls-mobile${isCardMode ? " cardMode" : ""}`}>
        <div
          className="sortControls-mobile-dropdown-wrapper"
          style={{ position: "relative", width: "100%" }}
        >
          <button type="button" onClick={() => setShowDropdown(!showDropdown)}>
            {currentSortFieldObj.label
              ? currentSortFieldObj.label(t)
              : t(currentSortFieldObj.id)}{" "}
            {getSortIcon(sortBy)}
          </button>
          {showDropdown && (
            <div className="sortControls-dropdown">
              {displayFields.map((field) => (
                <div
                  key={field.key}
                  className={`sortControls-div ${field.key} ${
                    sortBy === field.key ? "active" : ""
                  }`}
                  onClick={() => {
                    handleSort(field.key);
                    setShowDropdown(false);
                  }}
                >
                  {field.label ? field.label(t) : t(field.id)}
                  {getSortIcon(field.key)}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  }

  // Desktop/tablet version
  return (
    <div className={`sortControls-container ${className}`}>
      {displayFields.map((field) => (
        <div
          key={field.key}
          className={`sortControls-div ${field.key} ${
            sortBy === field.key ? "active" : ""
          }`}
          onClick={() => handleSort(field.key)}
        >
          {field.label ? field.label(t) : t(field.id)}
          {getSortIcon(field.key)}
        </div>
      ))}
    </div>
  );
};

SortControls.propTypes = {
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      key: PropTypes.string.isRequired,
      label: PropTypes.func,
    })
  ).isRequired,
  sortBy: PropTypes.string.isRequired,
  sortOrder: PropTypes.oneOf(["ASCENDING", "DESCENDING"]).isRequired,
  onSortChange: PropTypes.func.isRequired,
  className: PropTypes.string,
  isCardMode: PropTypes.bool,
};

export default SortControls;
