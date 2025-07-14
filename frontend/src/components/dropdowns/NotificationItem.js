import React from "react";
import { useTranslation } from "react-i18next";
import UserIcon from "../userIcon/UserIcon";
import { GrCycle } from "react-icons/gr";
import { FaAward, FaBook } from "react-icons/fa";
import { formatMessageTimestamp } from "../../utils/utilityFunctions";

/**
 * @file NotificationItem.js
 * @module NotificationItem
 * @description Generic notification item for message or other notification types.
 * @author Project Citrus Team
 */

/**
 * NotificationItem component for displaying a single notification.
 * @param {Object} props - Component props
 * @param {Object} props.notification - Notification data object
 * @param {Function} props.onClick - Callback when notification is clicked
 * @returns {JSX.Element}
 */
export default function NotificationItem({ notification, onClick }) {
  const { t } = useTranslation();

  // Use shared timestamp formatting

  let icon = null;
  let senderName = "";
  let message = "";
  let timestamp = null;

  /**
   * Determines icon, sender name, message, and timestamp based on notification type.
   */
  switch (notification.type) {
    case "MESSAGE":
      icon = (
        <UserIcon
          user={notification.sender}
          messageCount={notification.messageCount}
        />
      );
      senderName = notification.sender
        ? `${notification.sender.name} ${notification.sender.surname}`
        : "";
      message = notification.content || t("messageCenter.typeMessage");
      timestamp = notification.timestamp;
      break;
    case "APPRAISAL":
      icon = <FaAward className="notif-icon notif-icon-appraisal" />;
      senderName = notification.sender
        ? `${notification.sender.name} ${notification.sender.surname}`
        : "";
      message = t("notifications.appraisal", { content: notification.content });
      timestamp = notification.timestamp;
      break;
    case "CYCLE":
      icon = <GrCycle className="notif-icon notif-icon-cycle" />;
      senderName = notification.sender
        ? `${notification.sender.name} ${notification.sender.surname}`
        : "";
      message = t("notifications.cycle", { content: notification.content });
      timestamp = notification.timestamp;
      break;
    case "COURSE":
      icon = <FaBook className="notif-icon notif-icon-course" />;
      senderName = notification.sender
        ? `${notification.sender.name} ${notification.sender.surname}`
        : "";
      message = t("notifications.course", { content: notification.content });
      timestamp = notification.timestamp;
      break;
    default:
      icon = <UserIcon user={notification.sender} />;
      senderName = notification.sender
        ? `${notification.sender.name} ${notification.sender.surname}`
        : "";
      message = notification.content || t("Notification");
      timestamp = notification.timestamp;
  }

  return (
    <div
      className={`notification-item${
        notification.notificationIsSeen ? " notification-seen" : ""
      }`}
      key={notification.id}
      onClick={() => onClick(notification)}
      style={{ cursor: "pointer" }}
    >
      <div className="notification-icon-cell">
        <div className="message-user-icon">{icon}</div>
      </div>
      <div className="notification-text-cell">
        <div className="notification-sender">{senderName}</div>
        <div
          className={`notification-message ${
            !notification.notificationIsRead ? "unread" : ""
          }`}
        >
          {message}
        </div>
        <div className="notification-timestamp">
          {formatMessageTimestamp(timestamp)}
        </div>
      </div>
    </div>
  );
}
