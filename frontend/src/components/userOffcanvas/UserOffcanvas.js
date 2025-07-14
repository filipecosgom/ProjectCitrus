/**
 * @file UserOffcanvas.js
 * @module UserOffcanvas
 * @description Offcanvas panel for displaying detailed user information, avatar, and quick actions.
 * Handles avatar loading, entry/exit animation, scroll lock, ESC/backdrop close, and profile navigation.
 * @author Project Citrus Team
 */

import React, { useEffect, useState } from "react";
import { FaPhoneAlt, FaMapMarkerAlt, FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import "./UserOffcanvas.css";

/**
 * UserOffcanvas component for displaying user details in an animated offcanvas panel.
 * @param {Object} props - Component props
 * @param {Object} props.user - User object to display
 * @param {boolean} props.isOpen - Whether the offcanvas is open
 * @param {Function} props.onClose - Callback to close the offcanvas
 * @returns {JSX.Element|null} The rendered offcanvas or null if not visible
 */
const UserOffcanvas = ({ user, isOpen, onClose }) => {
  const { t } = useTranslation();
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [avatarLoading, setAvatarLoading] = useState(false);
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);

  /**
   * Controls entry/exit animation and conditional rendering.
   */
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

  /**
   * Fetches user avatar when user changes.
   * Cleans up blob URLs on unmount.
   */
  useEffect(() => {
    if (!user?.id) {
      setAvatarUrl(null);
      return;
    }
    let isCancelled = false;
    const fetchAvatar = async () => {
      if (!user.hasAvatar) {
        setAvatarUrl(null);
        return;
      }
      setAvatarLoading(true);
      try {
        const result = await handleGetUserAvatar(user.id);
        if (!isCancelled) {
          if (result.success && result.avatar) {
            setAvatarUrl(result.avatar);
          } else {
            setAvatarUrl(null);
          }
        }
      } catch (error) {
        if (!isCancelled) {
          console.error("Error fetching avatar:", error);
          setAvatarUrl(null);
        }
      } finally {
        if (!isCancelled) {
          setAvatarLoading(false);
        }
      }
    };
    fetchAvatar();
    return () => {
      isCancelled = true;
      if (avatarUrl?.startsWith("blob:")) {
        URL.revokeObjectURL(avatarUrl);
      }
    };
  }, [user?.id, user?.hasAvatar]);

  /**
   * Locks/unlocks page scroll when offcanvas is open/closed.
   */
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

  /**
   * Handles ESC key to close the offcanvas.
   */
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
   * Handles backdrop click to close the offcanvas.
   * @param {React.MouseEvent} e - Click event
   */
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  /**
   * Formats the user's role for display.
   * @param {string} role - Role string
   * @returns {string} Formatted role
   */
  const formatRole = (role) => {
    if (!role) return t("users.na");
    return role.replace(/_/g, " ");
  };

  /**
   * Formats the user's office for display.
   * @param {string} office - Office string
   * @returns {string} Formatted office
   */
  const formatOffice = (office) => {
    if (!office) return t("users.na");
    return office.replace(/_/g, " ");
  };

  /**
   * Returns account state info for display.
   * @param {string} accountState - Account state string
   * @returns {Object} Info object with text and className
   */
  const getAccountStateInfo = (accountState) => {
    if (accountState === "COMPLETE") {
      return {
        text: t("users.accountStateComplete"),
        className: "account-state-complete",
      };
    }
    return {
      text: t("users.accountStateIncomplete"),
      className: "account-state-incomplete",
    };
  };

  /**
   * Navigates to the user's profile page.
   */
  const handleViewProfile = () => {
    window.location.href = `/profile?id=${user.id}`;
  };

  // Don't render if not open or user missing
  if (!user || !shouldRender) return null;

  const accountStateInfo = getAccountStateInfo(user.accountState);

  return (
    <div
      className={`user-offcanvas-backdrop ${isAnimating ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`user-offcanvas ${isAnimating ? "open" : ""}`}>
        <FaTimes className="user-offcanvas-close" onClick={onClose} />
        <div className="user-offcanvas-content">
          <div className="user-offcanvas-avatar">
            {avatarLoading ? (
              <div className="avatar-loading">{t("users.avatarLoading")}</div>
            ) : (
              <img
                src={
                  avatarUrl || generateInitialsAvatar(user.name, user.surname)
                }
                alt={`${user.name} ${user.surname}`}
                onError={(e) => {
                  e.target.src = generateInitialsAvatar(
                    user.name,
                    user.surname
                  );
                }}
              />
            )}
          </div>
          <h1 className="user-offcanvas-name">
            {user.name} {user.surname}
          </h1>
          <p className="user-offcanvas-role">{formatRole(user.role)}</p>
          <p className="user-offcanvas-email">{user.email}</p>
          <div className="user-offcanvas-info">
            <div className="user-offcanvas-info-item">
              <FaPhoneAlt className="user-offcanvas-icon" />
              <span>{user.phone || t("users.na")}</span>
            </div>
            <div className="user-offcanvas-info-item">
              <FaMapMarkerAlt className="user-offcanvas-icon" />
              <span>{formatOffice(user.office)}</span>
            </div>
          </div>
          <div
            className={`user-offcanvas-account-state ${accountStateInfo.className}`}
          >
            {accountStateInfo.text}
          </div>
          <button
            className="main-button user-offcanvas-profile-btn"
            onClick={handleViewProfile}
          >
            {t("users.viewProfile")}
          </button>
        </div>
      </div>
    </div>
  );
};

/**
 * Generates a base64 SVG avatar with user initials.
 * @param {string} name - User's first name
 * @param {string} surname - User's surname
 * @returns {string} Data URL for SVG avatar
 */
export const generateInitialsAvatar = (name, surname) => {
  const initials = `${name?.[0] || ""}${surname?.[0] || ""}`.toUpperCase();
  return `data:image/svg+xml;base64,${btoa(`
    <svg width="275" height="275" viewBox="0 0 275 275" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="137.5" cy="137.5" r="137.5" fill="#f0f0f0"/>
      <text x="50%" y="50%" text-anchor="middle" dy=".3em" font-family="Arial" font-size="80" fill="#666">
        ${initials}
      </text>
    </svg>
  `)}`;
};

export default UserOffcanvas;
