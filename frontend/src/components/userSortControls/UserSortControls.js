import "./UserSortControls.css";
import { FaSortAlphaDown, FaSortAlphaUp } from "react-icons/fa";

const sortFields = [
  { label: "Name", key: "name" },
  { label: "Role", key: "role" },
  { label: "Office", key: "office" },
  { label: "Manager", key: "manager" },
];

const UserSortControls = ({ sortBy, sortOrder, onSortChange }) => {
  const handleSort = (field) => {
    if (sortBy === field) {
      // Toggle direction
      const newOrder = sortOrder === "ascending" ? "descending" : "ascending";
      onSortChange({ sortBy: field, sortOrder: newOrder });
    } else {
      // New sort field, default to ascending
      onSortChange({ sortBy: field, sortOrder: "ascending" });
    }
  };

  return (
    <div className="userSortControls-container">
      {sortFields.map(({ label, key }) => (
        <div
          key={key}
          className={`userSortControls-div ${sortBy === key ? "active" : ""}`}
          onClick={() => handleSort(key)}
        >
          {label}
          {sortBy === key && (
            sortOrder === "ascending" ? (
              <FaSortAlphaDown className="sort-icon" />
            ) : (
              <FaSortAlphaUp className="sort-icon" />
            )
          )}
        </div>
      ))}
    </div>
  );
};

export default UserSortControls;
