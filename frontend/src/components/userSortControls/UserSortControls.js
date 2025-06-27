// UserSortControls.jsx
import React from "react";
import "./UserSortControls.css";
import { FaSortAlphaDown, FaSortAlphaUp } from "react-icons/fa";
import { useIntl } from "react-intl";

const sortFields = [
  { id: "userSortControlsName", key: "name" },
  { id: "userSortControlsRole", key: "role" },
  { id: "userSortControlsOffice", key: "office" },
  { id: "userSortControlsManager", key: "manager" },
];

const UserSortControls = ({ sortBy, sortOrder, onSortChange }) => {
  const intl = useIntl();

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
          {intl.formatMessage({ id })}

          {/* sort-direction icon */}
          {sortBy === key &&
            (sortOrder === "ascending" ? (
              <FaSortAlphaDown
                className="sort-icon"
                title={intl.formatMessage({
                  id: "userSortControlsSortAscending",
                })}
              />
            ) : (
              <FaSortAlphaUp
                className="sort-icon"
                title={intl.formatMessage({
                  id: "userSortControlsSortDescending",
                })}
              />
            ))}
        </div>
      ))}
    </div>
  );
};

export default UserSortControls;