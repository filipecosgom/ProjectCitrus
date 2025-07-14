/**
 * AssignManagerOffcanvas module.
 * Renders an offcanvas panel for assigning a manager to selected users.
 * Includes user selection, search, feedback, and assignment actions with loading and animation states.
 * @module AssignManagerOffcanvas
 */

import React, { useEffect, useState } from "react";
import { FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import UserSearchBar from "../userSearchBar/UserSearchBar";
import Spinner from "../spinner/Spinner";
import UserIcon from "../userIcon/UserIcon";
import "./AssignManagerOffcanvas.css";
import handleNotification from "../../handles/handleNotification";

/**
 * AssignManagerOffcanvas component for assigning a manager to selected users.
 * @param {Object} props - Component props
 * @param {string[]} props.selectedUserIds - Array of selected user IDs
 * @param {Object[]} props.selectedUsers - Array of selected user objects
 * @param {boolean} props.isOpen - Whether the offcanvas is open
 * @param {function} props.onClose - Function to close the offcanvas
 * @param {function} props.onAssign - Function to handle assignment
 * @returns {JSX.Element|null} The rendered offcanvas or null if not open
 */
const AssignManagerOffcanvas = ({
  selectedUserIds = [],
  selectedUsers = [],
  isOpen,
  onClose,
  onAssign,
}) => {
  const { t } = useTranslation();
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [selectedNewManager, setSelectedNewManager] = useState(null);
  const [isAssigning, setIsAssigning] = useState(false);

  /**
   * Handles selection of a user to promote as manager.
   * @param {Object} user - User object to select
   */
  const handleUserSelect = (user) => {
    setSelectedNewManager(user);
  };

  /**
   * Handles the assignment action, shows loading, and calls onAssign.
   * @async
   */
  const handleAssignClick = async () => {
    if (!selectedNewManager || isAssigning) {
      handleNotification("info", "users.selectUserFirst");
      return;
    }
    setIsAssigning(true);
    const assignments = {
      newManagerId: selectedNewManager.id,
      newManagerName: `${selectedNewManager.name} ${selectedNewManager.surname}`,
      newManagerEmail: selectedNewManager.email,
      userIds: selectedUserIds,
      users: selectedUsers,
      action: "promoteAndAssign",
    };

    try {
      await onAssign(assignments);
    } catch (error) {
      console.error("❌ Error in assignment:", error);
    } finally {
      setIsAssigning(false);
    }
  };

  // Reset states when closing
  useEffect(() => {
    if (!isOpen) {
      setSelectedNewManager(null);
      setIsAssigning(false);
    }
  }, [isOpen]);

  // Control rendering and animation
  useEffect(() => {
    if (isOpen) {
      setShouldRender(true);
      const timer = setTimeout(() => {
        setIsAnimating(true);
      }, 10);
      return () => clearTimeout(timer);
    } else {
      setIsAnimating(false);
      const timer = setTimeout(() => {
        setShouldRender(false);
      }, 400);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  // Control page scroll when open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  // Close on ESC key
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") {
        onClose();
      }
    };
    if (isOpen) {
      document.addEventListener("keydown", handleEsc);
    }
    return () => {
      document.removeEventListener("keydown", handleEsc);
    };
  }, [isOpen, onClose]);

  /**
   * Handles closing the offcanvas when clicking the backdrop.
   * @param {Object} e - Mouse event
   */
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  // Do not render if not shouldRender
  if (!shouldRender) {
    return null;
  }

  return (
    <div
      className={`assign-manager-backdrop ${isAnimating ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`assign-manager-offcanvas ${isAnimating ? "open" : ""}`}>
        {/* Header with title and close button */}
        <div className="assign-manager-header">
          <h2 className="assign-manager-title">
            {t("users.assignManagerTitle", { count: selectedUsers.length })}
          </h2>
          <button
            className="assign-manager-close"
            onClick={onClose}
            disabled={isAssigning}
          >
            <FaTimes />
          </button>
        </div>

        {/* Offcanvas content */}
        <div className="assign-manager-content">
          {/* Selected users list */}
          <div className="selected-users-section">
            <h3 className="section-title">{t("users.selectedUsers")}</h3>
            <div className="selected-users-list">
              {selectedUsers.map((user) => (
                <div key={user.id} className="selected-user-item">
                  {/* Avatar with UserIcon */}
                  <div className="selected-user-avatar">
                    <UserIcon
                      user={{
                        id: user.id,
                        name: user.name,
                        surname: user.surname,
                        hasAvatar: user.hasAvatar || user.avatar,
                        onlineStatus: user.onlineStatus || false,
                      }}
                    />
                  </div>
                  <span className="user-name">
                    {user.name} {user.surname}
                  </span>
                  <span className="user-role">
                    {user.role?.replace(/_/g, " ") || t("users.na")}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* New manager search section */}
          <div className="new-manager-section">
            <h3 className="section-title">{t("users.selectUserToPromote")}</h3>
            <p className="section-description">
              {t("users.promoteDescription")}
            </p>

            {/* UserSearchBar reusable component */}
            <UserSearchBar
              selectedUser={selectedNewManager}
              onUserSelect={handleUserSelect}
              placeholder={t("users.searchPromotePlaceholder")}
              maxResults={30}
              showUserInfo={true}
              compact={true}
              excludeUserIds={selectedUserIds}
              className="assign-manager-search"
              disableDropdown={false}
            />

            {/* Feedback for selected new manager */}
            {selectedNewManager && (
              <div className="selected-new-manager-feedback">
                <div className="feedback-icon">🎯</div>
                <div className="feedback-text">
                  <div className="feedback-primary">
                    <strong>
                      {selectedNewManager.name} {selectedNewManager.surname}
                    </strong>{" "}
                    {t("users.willBePromoted")}
                  </div>
                  <div className="feedback-secondary">
                    {selectedNewManager.email} •{" "}
                    {selectedNewManager.role?.replace(/_/g, " ")}
                  </div>
                  <div className="feedback-action">
                    ↳ {t("users.willManage", { count: selectedUsers.length })}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Footer with loading state */}
        <div className="assign-manager-footer">
          <button
            className="cancel-btn"
            onClick={onClose}
            disabled={isAssigning}
          >
            {t("users.cancel")}
          </button>
          <button
            className="assign-btn"
            onClick={handleAssignClick}
            disabled={!selectedNewManager || isAssigning}
          >
            {isAssigning ? (
              <>
                <Spinner size="small" />
                {t("users.assigning")}
              </>
            ) : selectedNewManager ? (
              <>
                {t("users.promoteAndAssign")}
                <span className="selected-manager-name">
                  → {selectedNewManager.name}
                </span>
              </>
            ) : (
              t("users.selectUserFirst")
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AssignManagerOffcanvas;
