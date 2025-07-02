// sortControls.jsx
import React from "react";
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";
import "./SortControls.css";

const SortControls = ({
  fields,
  sortBy,
  sortOrder,
  onSortChange,
  className = "",
}) => {
  const { t } = useTranslation();

  // ✅ DETECTAR se é modo Users
  const isUsersMode = className.includes("users-mode");

  const handleSort = (fieldKey) => {
    const newOrder =
      sortBy === fieldKey && sortOrder === "ASCENDING"
        ? "DESCENDING"
        : "ASCENDING";
    onSortChange({ sortBy: fieldKey, sortOrder: newOrder });
  };

  const getSortIcon = (fieldKey) => {
    if (sortBy !== fieldKey) return "↕️";
    return sortOrder === "ASCENDING" ? "⬆️" : "⬇️";
  };

  // ✅ MAPEAR fields baseado no modo
  const getDisplayFields = () => {
    if (isUsersMode) {
      // ✅ PARA USERS: 4 colunas
      return [
        { key: "name", labelKey: "userName" },
        { key: "role", labelKey: "userRole" },
        { key: "office", labelKey: "userOffice" },
        { key: "manager", labelKey: "userManager" },
      ];
    } else {
      // ✅ PARA APPRAISALS: 5 colunas (usar fields do props)
      return Object.entries(fields).map(([key, labelKey]) => ({
        key,
        labelKey,
      }));
    }
  };

  const displayFields = getDisplayFields();

  return (
    <div className={`sortControls-container ${className}`}>
      {displayFields.map(({ key, labelKey }) => (
        <div
          key={key}
          className={`sortControls-div ${
            key === "office" || key === "manager" ? key : ""
          } ${sortBy === key ? "active" : ""}`}
          onClick={() => handleSort(key)}
        >
          {t(labelKey)}
          {sortBy === key && (
            <span className="sort-icon">{getSortIcon(key)}</span>
          )}
        </div>
      ))}
    </div>
  );
};

SortControls.propTypes = {
  fields: PropTypes.object.isRequired,
  sortBy: PropTypes.string.isRequired,
  sortOrder: PropTypes.oneOf(["ASCENDING", "DESCENDING"]).isRequired,
  onSortChange: PropTypes.func.isRequired,
  className: PropTypes.string,
};

export default SortControls;
