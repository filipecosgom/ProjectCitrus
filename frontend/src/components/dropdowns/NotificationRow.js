import React from "react";
import "./NotificationRow.css";
import { useTranslation } from "react-i18next";
import UserIcon from "../userIcon/UserIcon";
import { GrCycle } from "react-icons/gr";
import { FaAward, FaBook } from "react-icons/fa";
import { formatMessageTimestamp } from "../../utils/utilityFunctions"; // Assuming you have a utility function for formatting timestamps

// NotificationRow styled like NotificationDropdown

export default function NotificationRow({ notification, onClick, onSelectionChange, isSelected }) {
  const { t } = useTranslation();
    let icon = null;
  let senderName = "";
  let message = "";
  let timestamp = null;

  const handleCheckboxChange = (e) => {
    e.stopPropagation(); // Evitar propagação para o card
    if (onSelectionChange) {
      onSelectionChange(notification.id, e.target.checked);
    }
  };

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
      className={`notificationRow-row${notification.notificationIsSeen ? " notification-seen" : ""}`}
      key={notification.id}
      onClick={() => onClick(notification)}
      style={{ cursor: "pointer" }}
    >
      <div className="notificationRow-checkbox">
      <input
            type="checkbox"
            checked={isSelected}
            onChange={handleCheckboxChange}
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      <div className="notificationRow-iconAndSender-cell">
      <div className="notificationRow-icon-cell">
          {icon}
      </div>
      <div className="notificationRow-sender">
          {senderName}
        </div>
        </div>
    <div
          className={`notificationRow-message ${!notification.notificationIsRead ? "unread" : ""}`}>
          {message}
        </div>
        <div className="notificationRow-timestamp">
          {formatMessageTimestamp(timestamp)}
        </div>
      </div>
  );
}
