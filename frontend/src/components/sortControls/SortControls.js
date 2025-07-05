// sortControls.jsx
import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";
import { FaSortAmountDownAlt , FaSortAmountUpAlt , FaSort } from "react-icons/fa"; // ✅ ADICIONAR
import "./SortControls.css";

const SortControls = ({
  fields,
  sortBy,
  sortOrder,
  onSortChange,
  className = "",
}) => {
  const { t } = useTranslation();
  const isUsersMode = className.includes("users-mode");

  const [showDropdown, setShowDropdown] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 480);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 480);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const handleSort = (fieldKey) => {
    const newOrder =
      sortBy === fieldKey && sortOrder === "ASCENDING"
        ? "DESCENDING"
        : "ASCENDING";
    onSortChange({ sortBy: fieldKey, sortOrder: newOrder });
  };

  // ✅ FUNÇÃO PARA RETORNAR ÍCONES REACT-ICONS
  const getSortIcon = (fieldKey) => {
    console.log(sortBy, fieldKey, sortOrder);
    if (sortBy !== fieldKey) {
      return <FaSort className="sort-icon" />; // ✅ Ícone neutro
    }
    return sortOrder === "ASCENDING" ? (
      <FaSortAmountDownAlt className="sort-icon" /> // ✅ A-Z
    ) : (
      <FaSortAmountUpAlt className="sort-icon" /> // ✅ Z-A
    );
  };

  // ✅ DEFINIR fields baseado no modo
  const getDisplayFields = () => {
    if (isUsersMode) {
      // ✅ PARA USERS: 4 colunas (sem state)
      return [
        { id: "userSortControlsName", key: "name" },
        { id: "userSortControlsRole", key: "role" },
        { id: "userSortControlsOffice", key: "office" },
        { id: "userSortControlsManager", key: "manager" },
      ];
    } else {
      // ✅ PARA APPRAISALS: usar fields do props (5 colunas)
      return fields;
    }
  };

  const displayFields = getDisplayFields();
  const currentSortField = displayFields.find(f => f.key === sortBy)?.id || displayFields[0].id;

  if (isMobile) {
    return (
      <div className="sortControls-mobile">
        <button type="button" onClick={() => setShowDropdown(!showDropdown)}>
          {t(currentSortField)} {getSortIcon(sortBy)}
        </button>
        {showDropdown && (
          <div className="sortControls-dropdown">
            {displayFields.map(field => (
              <div
                key={field.key}
                onClick={() => {
                  onSortChange({
                    sortBy: field.key,
                    sortOrder: sortBy === field.key && sortOrder === "ASCENDING" ? "DESCENDING" : "ASCENDING"
                  });
                  setShowDropdown(false);
                }}
              >
                {t(field.id)} {sortBy === field.key && getSortIcon(field.key)}
              </div>
            ))}
          </div>
        )}
      </div>
    );
  }

  // Desktop/tablet version
  return (
    <div className={`sortControls-container ${className}`}>
      {displayFields.map(({ id, key }) => (
        <div
          key={key}
          className={`sortControls-div ${key} ${sortBy === key ? "active" : ""}`}
          onClick={() => handleSort(key)}
        >
          {t(id)}
          {getSortIcon(key)}
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
    })
  ).isRequired,
  sortBy: PropTypes.string.isRequired,
  sortOrder: PropTypes.oneOf(["ASCENDING", "DESCENDING"]).isRequired,
  onSortChange: PropTypes.func.isRequired,
  className: PropTypes.string,
};

export default SortControls;
