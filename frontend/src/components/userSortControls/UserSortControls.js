// UserSortControls.jsx
import React from "react";
import "./UserSortControls.css";
import { FaSortAlphaDown, FaSortAlphaUp } from "react-icons/fa";
import { useTranslation } from "react-i18next";

const sortFields = [
  { id: "userSortControlsName", key: "name" },
  { id: "userSortControlsRole", key: "role" },
  { id: "userSortControlsOffice", key: "office" },
  { id: "userSortControlsManager", key: "manager" },
];

const UserSortControls = ({ sortBy, sortOrder, onSortChange }) => {
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
    <div className="userSortControls-container">
      {sortFields.map(({ id, key }) => (
        <div
          key={key}
          className={`userSortControls-div ${key} ${
            sortBy === key ? "active" : ""
          }`}
          onClick={() => handleSort(key)}
        >
          {/* field label */}
          {t(id)}

          {/* sort-direction icon */}
          {sortBy === key &&
            (sortOrder === "ascending" ? (
              <FaSortAlphaDown className="sort-icon" />
            ) : (
              <FaSortAlphaUp className="sort-icon" />
            ))}
        </div>
      ))}
    </div>
  );
};

export default UserSortControls;