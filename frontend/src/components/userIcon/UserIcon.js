/**
 * @file UserIcon.js
 * @module UserIcon
 * @description Component for displaying a user's avatar, online status, and message count badge.
 * Handles avatar loading, fallback to initials, and defensive checks for missing user data.
 * @author Project Citrus Team
 */

import React, { useState, useEffect } from "react";
import "./UserIcon.css";
import { generateInitialsAvatar } from "../../components/userOffcanvas/UserOffcanvas";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import { FaUserCircle } from "react-icons/fa";

/**
 * UserIcon component for displaying a user's avatar, online status, and message count.
 * @param {Object} props - Component props
 * @param {Object} props.user - User object containing id, name, surname, hasAvatar, onlineStatus, etc.
 * @param {string} [props.avatar] - Optional avatar URL to override fetch
 * @param {number} [props.messageCount] - Optional message count to display as badge
 * @returns {JSX.Element} The rendered user icon
 */
export default function UserIcon({ user, avatar, messageCount }) {
  // Avatar URL state, loading state
  const [avatarUrl, setAvatarUrl] = useState(avatar || null);
  const [loading, setLoading] = useState(false);

  // Defensive checks for user properties
  const hasAvatar = user?.hasAvatar ?? false;
  const userName = user?.name || "";
  const userSurname = user?.surname || "";
  const onlineStatus = user?.onlineStatus || false;

  /**
   * Loads the user's avatar from backend if available, otherwise uses initials.
   * Cleans up blob URLs on unmount.
   */
  useEffect(() => {
    if (!user || !user.id) {
      setAvatarUrl(null);
      return;
    }

    if (avatar) {
      setAvatarUrl(avatar);
      return;
    }

    let userBlobUrl = null;
    if (!hasAvatar) {
      setAvatarUrl(null);
      return;
    }

    setLoading(true);
    handleGetUserAvatar(user.id)
      .then((result) => {
        if (result.success && result.avatar) {
          userBlobUrl = result.avatar;
          setAvatarUrl(result.avatar);
        } else {
          setAvatarUrl(null);
        }
      })
      .catch(() => setAvatarUrl(null))
      .finally(() => setLoading(false));

    return () => {
      if (userBlobUrl?.startsWith("blob:")) URL.revokeObjectURL(userBlobUrl);
    };
  }, [user?.id, hasAvatar, avatar]);

  // Defensive rendering if user is missing
  if (!user) {
    console.warn("⚠️ UserIcon: user is null - rendering placeholder");
    return (
      <div className="user-icon user-icon-placeholder">
        <FaUserCircle style={{ fontSize: "38px", color: "#ccc" }} />
      </div>
    );
  }

  return (
    <div className="user-icon" style={{ position: "relative" }}>
      <img
        src={
          hasAvatar && avatarUrl
            ? avatarUrl
            : generateInitialsAvatar(userName, userSurname)
        }
        alt={`${userName} ${userSurname}`}
        onError={(e) => {
          // Fallback to initials if image fails
          e.target.src = generateInitialsAvatar(userName, userSurname);
        }}
      />
      {/* Status badge: green if online, gray if offline */}
      <div
        className={`user-icon-status ${onlineStatus ? "online" : "offline"}`}
        title={onlineStatus ? "Online" : "Offline"}
      />
      {/* Message count badge */}
      {typeof messageCount === "number" && messageCount > 0 && (
        <span className="user-icon-message-badge">
          {messageCount > 99 ? "99+" : messageCount}
        </span>
      )}
    </div>
  );
}
