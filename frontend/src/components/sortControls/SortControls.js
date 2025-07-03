// sortControls.jsx
import React from "react";
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";
import { FaSortAmountDown, FaSortAmountUp, FaSort } from "react-icons/fa"; // ✅ ADICIONAR
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

  // ✅ FUNÇÃO PARA RETORNAR ÍCONES REACT-ICONS
  const getSortIcon = (fieldKey) => {
    if (sortBy !== fieldKey) {
      return <FaSort className="sort-icon" />; // ✅ Ícone neutro
    }
    return sortOrder === "ASCENDING" ? (
      <FaSortAmountUp className="sort-icon" /> // ✅ A-Z
    ) : (
      <FaSortAmountDown className="sort-icon" /> // ✅ Z-A
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

  return (
    <div className={`sortControls-container ${className}`}>
      {displayFields.map(({ id, key }) => (
        <div
          key={key}
          className={`sortControls-div ${
            key === "office" || key === "manager" ? key : ""
          } ${sortBy === key ? "active" : ""}`}
          onClick={() => handleSort(key)}
        >
          {t(id)}
          {sortBy === key &&
            (sortOrder === "ascending" ? (
              <FaSortAmountDown  className="sort-icon" />
            ) : (
              <FaSortAmountUp  className="sort-icon" />
            ))}
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
