import React from "react";
import PropTypes from "prop-types";
import styles from "./UserCard.css";
import UserIcon from "../userIcon/UserIcon";
import Spinner from "../spinner/spinner";
import { ImCross } from "react-icons/im";
import { useTranslation } from "react-i18next";

const UserCard = ({
  user,
  onClick,
  showCheckbox = false,
  isSelected = false,
  onSelectionChange,
}) => {
  const { t } = useTranslation();

  // Determine status for UserIcon
  const getStatusIcon = () => {
    if (user.userIsAdmin) return "check";
    if (user.userIsDeleted) return "cross";
    if (user.accountState === "INCOMPLETE") return "stroke";
    return null;
  };

  const getStatusClass = () => {
    if (user.userIsAdmin) return styles.admin;
    if (user.isManager) return styles.manager;
    return "";
  };

  // ✅ HANDLER para click do card
  const handleCardClick = (e) => {
    // Se clicou na checkbox, não propagar
    if (e.target.type === "checkbox") return;

    if (onClick) {
      onClick(user);
    }
  };

  // ✅ HANDLER para checkbox
  const handleCheckboxChange = (e) => {
    e.stopPropagation(); // Evitar propagação para o card
    if (onSelectionChange) {
      onSelectionChange(user.id, e.target.checked);
    }
  };

  return (
    <div className={`userCard-wrapper${showCheckbox ? " with-checkbox" : ""}`}>
      {/* ✅ CHECKBOX - sempre visível para admins */}
      {showCheckbox && (
        <div className="userCard-checkbox">
          <input
            type="checkbox"
            checked={isSelected}
            onChange={handleCheckboxChange}
            onClick={(e) => e.stopPropagation()} // Evitar propagação dupla
          />
        </div>
      )}
      <div
        className={`userCard-container ${getStatusClass()} ${
          showCheckbox && isSelected ? "selected" : ""
        }`} // ✅ USAR showCheckbox
        onClick={handleCardClick}
        style={{ cursor: onClick ? "pointer" : "default" }} // ✅ REMOVER condição isSelectionMode
      >
        <div className="userCard-avatarAndInfoContainer container-user">
          {/* User Avatar */}
          <div className="userCard-avatarContainer">
            <UserIcon user={user} status={getStatusIcon()} />
          </div>

          {/* User Info */}
          <div className="userCard-userInfo">
            <div className="userCard-name">
              {user.name} {user.surname}
            </div>
            <div className="userCard-email">{user.email}</div>
          </div>
        </div>

        {/* Role */}
        <div className="userCard-role">
          {user.role?.replace(/_/g, " ") || "N/A"}
        </div>

        {/* Office */}
        <div className="userCard-office">
          {user.office?.replace(/_/g, " ") || "N/A"}
        </div>

        {/* Manager Section */}
        {user.manager ? (
          <div className="userCard-avatarAndInfoContainer container-manager">
            <div className="userCard-managerAvatar">
              <UserIcon user={user.manager} status={user.manager.status} />
            </div>
            <div className="userCard-managerInfo">
              <div className="userCard-managerName">
                {user.manager.name} {user.manager.surname}
              </div>
              <div className="userCard-managerEmail">{user.manager.email}</div>
            </div>
          </div>
        ) : (
          <div className="userCard-noManager">
            <ImCross title="No manager assigned" className="noManager-icon" />
            <span className="noManager-label">{t("userCardNoManager")}</span>
          </div>
        )}
      </div>
    </div>
  );
};

UserCard.propTypes = {
  user: PropTypes.shape({
    id: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired,
    surname: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired,
    phone: PropTypes.string,
    role: PropTypes.string.isRequired,
    office: PropTypes.string.isRequired,
    municipality: PropTypes.string.isRequired,
    birthdate: PropTypes.array,
    accountState: PropTypes.string.isRequired,
    userIsAdmin: PropTypes.bool.isRequired,
    isManager: PropTypes.bool.isRequired,
    userIsDeleted: PropTypes.bool.isRequired,
    hasAvatar: PropTypes.bool.isRequired,
    manager: PropTypes.shape({
      id: PropTypes.number,
      name: PropTypes.string,
      surname: PropTypes.string,
    }),
  }).isRequired,
  onClick: PropTypes.func,
  showCheckbox: PropTypes.bool, // ✅ RENOMEADO
  isSelected: PropTypes.bool,
  onSelectionChange: PropTypes.func,
};

export default UserCard;
