/**
 * @file NotificationDropdown.js
 * @module NotificationDropdown
 * @description Dropdown for displaying other notifications and navigation to notification center.
 * @author Project Citrus Team
 */

import React from "react";
import { Link, useNavigate } from "react-router-dom";
import NotificationItem from "./NotificationItem";
import useNotificationStore from "../../stores/useNotificationStore";
import { handleUpdateNotification } from "../../handles/handleNotificationApi";
import "./NotificationDropdown.css";
import { useTranslation } from "react-i18next";
import useAuthStore from "../../stores/useAuthStore";

/**
 * NotificationDropdown component for displaying other notifications and navigation.
 * @param {Object} props - Component props
 * @param {boolean} props.isVisible - Whether the dropdown is visible
 * @param {Function} props.onClose - Callback to close the dropdown
 * @returns {JSX.Element}
 */
export default function NotificationDropdown({ isVisible, onClose }) {
  const notifications = useNotificationStore(
    (state) => state.otherNotifications || []
  );
  const markOtherAsSeen = useNotificationStore(
    (state) => state.markOtherAsSeen
  );
  const userId = useAuthStore((state) => state.user?.id);
  const navigate = useNavigate();
  const { t } = useTranslation();

  // Mark as read logic should be handled in the parent (Header) when opening the dropdown.

  /**
   * Handles click on a notification: marks as seen, updates local store, and navigates based on type.
   * @param {Object} notification - Notification object
   */
  const handleNotificationClick = (notification) => {
    if (!notification.notificationIsSeen) {
      handleUpdateNotification({
        notificationId: notification.id,
        notificationIsSeen: true,
      });
      markOtherAsSeen(notification.id); // Update local store for real-time UI
    }
    // Navigate based on notification type
    if (notification.type === "APPRAISAL") {
      navigate(`/profile?id=${notification.recipient.id}&tab=appraisals`);
    } else if (notification.type === "CYCLE") {
      navigate(`/appraisals?state=IN_PROGRESS`);
    } else if (notification.type === "COURSE") {
      navigate(`/profile?id=${notification.recipient.id}&tab=training`);
    } else {
      navigate(`/notifications`);
    }
    onClose?.();
  };

  const visibleNotifications = notifications.slice(0, 6);

  return (
    <div className="notification-dropdown">
      <div className="notification-list">
        {visibleNotifications.length === 0 ? (
          <div className="notification-empty">{t("No notifications")}</div>
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
      <Link
        to="/notifications"
        className="notification-center-btn"
        onClick={onClose}
      >
        {t("TO NOTIFICATION CENTER")}{" "}
        <span className="notification-center-arrow">↗</span>
      </Link>
    </div>
  );
}
