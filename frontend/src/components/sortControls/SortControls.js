// sortControls.jsx
import React from "react";
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";
import "./SortControls.css";

const SortControls = ({ sortCriteria, sortOrder, onSort, className = "" }) => {
  const { t } = useTranslation();

  // ✅ DETECTAR se é modo Users
  const isUsersMode = className.includes("users-mode");

  const handleSort = (criteria) => {
    const newOrder =
      sortCriteria === criteria && sortOrder === "asc" ? "desc" : "asc";
    onSort(criteria, newOrder);
  };

  const getSortIcon = (criteria) => {
    if (sortCriteria !== criteria) return "↕️";
    return sortOrder === "asc" ? "⬆️" : "⬇️";
  };

  return (
    <div className={`sortControls-container ${className}`}>
      {/* ✅ COLUNA 1: USER/NOME */}
      <div
        className={`sortControls-div ${
          sortCriteria === "name" ? "active" : ""
        }`}
        onClick={() => handleSort("name")}
      >
        {isUsersMode ? t("name") : t("user")}
        {sortCriteria === "name" && (
          <span className="sort-icon">{getSortIcon("name")}</span>
        )}
      </div>

      {/* ✅ COLUNA 2: SCORE/ROLE */}
      <div
        className={`sortControls-div ${
          sortCriteria === (isUsersMode ? "role" : "score") ? "active" : ""
        }`}
        onClick={() => handleSort(isUsersMode ? "role" : "score")}
      >
        {isUsersMode ? t("role") : t("score")}
        {sortCriteria === (isUsersMode ? "role" : "score") && (
          <span className="sort-icon">
            {getSortIcon(isUsersMode ? "role" : "score")}
          </span>
        )}
      </div>

      {/* ✅ COLUNA 3: MANAGER/OFFICE */}
      <div
        className={`sortControls-div manager ${
          sortCriteria === (isUsersMode ? "office" : "manager") ? "active" : ""
        }`}
        onClick={() => handleSort(isUsersMode ? "office" : "manager")}
      >
        {isUsersMode ? t("office") : t("manager")}
        {sortCriteria === (isUsersMode ? "office" : "manager") && (
          <span className="sort-icon">
            {getSortIcon(isUsersMode ? "office" : "manager")}
          </span>
        )}
      </div>

      {/* ✅ COLUNA 4: END DATE/MANAGER */}
      <div
        className={`sortControls-div ${
          sortCriteria === (isUsersMode ? "manager" : "endDate") ? "active" : ""
        }`}
        onClick={() => handleSort(isUsersMode ? "manager" : "endDate")}
      >
        {isUsersMode ? t("manager") : t("endDate")}
        {sortCriteria === (isUsersMode ? "manager" : "endDate") && (
          <span className="sort-icon">
            {getSortIcon(isUsersMode ? "manager" : "endDate")}
          </span>
        )}
      </div>

      {/* ✅ COLUNA 5: STATE - ESCONDIDA EM USERS MODE */}
      <div
        className={`sortControls-div ${
          sortCriteria === "state" ? "active" : ""
        }`}
        onClick={() => handleSort("state")}
      >
        {t("state")}
        {sortCriteria === "state" && (
          <span className="sort-icon">{getSortIcon("state")}</span>
        )}
      </div>
    </div>
  );
};

SortControls.propTypes = {
  sortCriteria: PropTypes.string.isRequired,
  sortOrder: PropTypes.oneOf(["asc", "desc"]).isRequired,
  onSort: PropTypes.func.isRequired,
  className: PropTypes.string,
};

export default SortControls;
