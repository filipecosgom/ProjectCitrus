// sortControls.jsx
import React from "react";
import "./SortControls.css";
import { FaSortAmountDown , FaSortAmountUp  } from "react-icons/fa";
import { useTranslation } from "react-i18next";

// Now accepts a 'fields' prop for generic usage!
const SortControls = ({ fields, sortBy, sortOrder, onSortChange }) => {
  const { t } = useTranslation();

  const handleSort = (field) => {
    if (sortBy === field) {
      const newOrder = sortOrder === "ascending" ? "descending" : "ascending";
      onSortChange({ sortBy: field, sortOrder: newOrder });
    } else {
      onSortChange({ sortBy: field, sortOrder: "ascending" });
    }
  };

  return (
    <div className="sortControls-container">
      {fields.map(({ id, key }) => (
        <div
          key={key}
          className={`sortControls-div ${key} ${
            sortBy === key ? "active" : ""
          }`}
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

export default SortControls;