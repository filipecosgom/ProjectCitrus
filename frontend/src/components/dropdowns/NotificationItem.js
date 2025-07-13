import React from "react";
import { useTranslation } from "react-i18next";
import UserIcon from "../userIcon/UserIcon";
import { GrCycle } from "react-icons/gr";
import { FaAward, FaBook } from "react-icons/fa";

// Generic notification item for message or other notification types
export default function NotificationItem({ notification, onClick }) {
  const { t } = useTranslation();

  const formatDate = (dateArray) => {
    if (!Array.isArray(dateArray) || dateArray.length < 6) {
      return "Unknown date";
    }

    try {
      // [2025,7,3,22,41,34,573317000] -> Date
      const [year, month, day, hour, minute, second] = dateArray;
      const date = new Date(year, month - 1, day, hour, minute, second);

      // Formatar para "22:41" ou "3 Jul" dependendo da data
      const now = new Date();
      const isToday = date.toDateString() === now.toDateString();

      if (isToday) {
        return date.toLocaleTimeString("en-US", {
          hour: "2-digit",
          minute: "2-digit",
          hour12: false,
        });
      } else {
        return date.toLocaleDateString("en-US", {
          month: "short",
          day: "numeric",
        });
      }
    } catch (err) {
      return "Invalid date";
    }
  };


  let icon = null;
  let senderName = "";
  let message = "";
  let timestamp = null;

  switch (notification.type) {
    case "MESSAGE":
      icon = <UserIcon user={notification.sender} messageCount={notification.messageCount}/>;
      senderName = notification.sender ? `${notification.sender.name} ${notification.sender.surname}` : "";
      message = notification.content || t("messageCenter.typeMessage");
      timestamp = notification.timestamp;
      break;
    case "APPRAISAL":
      icon = <FaAward className="notif-icon notif-icon-appraisal" />;
      senderName = notification.sender ? `${notification.sender.name} ${notification.sender.surname}` : "";
      message = t("notifications.appraisal", { content: notification.content });
      timestamp = notification.timestamp;
      break;
    case "CYCLE":
      icon = <GrCycle className="notif-icon notif-icon-cycle" />;
      senderName = notification.sender ? `${notification.sender.name} ${notification.sender.surname}` : "";
      message = t("notifications.cycle", { content: notification.content });
      timestamp = notification.timestamp;
      break;
    case "COURSE":
      icon = <FaBook className="notif-icon notif-icon-course" />;
      senderName = notification.sender ? `${notification.sender.name} ${notification.sender.surname}` : "";
      message = t("notifications.course", { content: notification.content });
      timestamp = notification.timestamp;
      break;
    default:
      icon = <UserIcon user={notification.sender} />;
      senderName = notification.sender ? `${notification.sender.name} ${notification.sender.surname}` : "";
      message = notification.content || t("Notification");
      timestamp = notification.timestamp;
  }

  return (
    <div
      className={`notification-item${notification.notificationIsSeen ? " notification-seen" : ""}`}
      key={notification.id}
      onClick={() => onClick(notification)}
      style={{ cursor: "pointer" }}
    >
      <div className="notification-icon-cell">
        <div className="message-user-icon">
          {icon}
        </div>
      </div>
      <div className="notification-text-cell">
        <div className="notification-sender">
          {senderName}
        </div>
        <div
          className={`notification-message ${!notification.notificationIsRead ? "unread" : ""}`}
        >
          {message}
        </div>
        <div className="notification-timestamp">
          {formatDate(timestamp)}
        </div>
      </div>
    </div>
  );
}
