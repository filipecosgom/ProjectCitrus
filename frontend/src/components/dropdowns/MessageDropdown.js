import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import { handleUpdateNotification } from "../../handles/handleNotificationApi";
import useNotificationStore from "../../stores/useNotificationStore";
import UserIcon from "../userIcon/UserIcon"; // ✅ ADICIONAR IMPORT
import NotificationItem from "./NotificationItem";
import "./NotificationDropdown.css";
import { useTranslation } from "react-i18next";

/**
 * @file MessageDropdown.js
 * @module MessageDropdown
 * @description Dropdown for displaying message notifications and navigation to message center.
 * @author Project Citrus Team
 */

/**
 * MessageDropdown component for displaying message notifications and navigation.
 * @param {Object} props - Component props
 * @param {boolean} props.isVisible - Whether the dropdown is visible
 * @param {Function} props.onUnreadCountChange - Callback when unread count changes
 * @param {Function} props.onClose - Callback to close the dropdown
 * @returns {JSX.Element}
 */
export default function MessageDropdown({
  isVisible,
  onUnreadCountChange,
  onClose,
}) {
  const messageNotifications = useNotificationStore(
    (state) => state.messageNotifications
  );
  const navigate = useNavigate(); // Hook para navegação programática
  const { t } = useTranslation();

  // Marking as read and backend update should be handled in the parent (Header) when opening the dropdown.

  const setMessageUnreadCountToZero = useNotificationStore(
    (state) => state.setMessageUnreadCountToZero
  );
  const markMessageAsSeen = useNotificationStore(
    (state) => state.markMessageAsSeen
  );

  /**
   * Handles click on a notification: marks as seen, updates unread count, and navigates to message.
   * @param {Object} notification - Notification object
   */
  const handleNotificationClick = (notification) => {
    console.log("Notification clicked:", notification);
    if (!notification.notificationIsSeen) {
      handleUpdateNotification({
        notificationId: notification.id,
        notificationIsSeen: true,
      });
      markMessageAsSeen(notification.id); // Update local store for real-time UI
    }
    setMessageUnreadCountToZero(notification.id);
    navigate(`/messages?id=${notification.sender.id}`);
    onClose?.();
  };

  const visibleNotifications = messageNotifications.slice(0, 6);

  return (
    <div className="notification-dropdown">
      <div className="notification-list">
        {visibleNotifications.length === 0 ? (
          <div className="notification-empty">No conversations found</div>
        ) : (
          visibleNotifications.map((notification, idx) => (
            <NotificationItem
              key={notification.id || idx}
              notification={notification}
              onClick={handleNotificationClick}
            />
          ))
        )}
      </div>
      {/* Botão para navegar para o centro de mensagens */}
      <Link
        to="/messages"
        className="notification-center-btn"
        onClick={onClose}
      >
        {t("messageCenter.toMessageCenter")}{" "}
        <span className="notification-center-arrow">↗</span>
      </Link>
    </div>
  );
}
