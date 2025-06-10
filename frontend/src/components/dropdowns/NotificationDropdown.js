import React from "react";
import { Link } from "react-router-dom";
import { FaFileAlt, FaCheckSquare } from "react-icons/fa";
import "./NotificationDropdown.css";

// Função para escolher ícone conforme tipo
function getIcon(type) {
  switch (type) {
    case "appraisal":
      return <FaFileAlt className="notif-icon" color="#B61B23" />;
    case "course":
      return <FaCheckSquare className="notif-icon" color="#818488" />;
    default:
      return <FaFileAlt className="notif-icon" color="#B61B23" />;
  }
}

export default function NotificationDropdown({ notifications = [] }) {
  // Mostra no máximo 6, mas só 3 visíveis sem scroll
  const visibleNotifications = notifications.slice(0, 6);

  return (
    <div className="notification-dropdown">
      <div className="notification-list">
        {visibleNotifications.length === 0 ? (
          <div className="notification-empty">No notifications</div>
        ) : (
          visibleNotifications.map((notif, idx) => (
            <div className="notification-item" key={idx}>
              <div className="notification-icon-cell">
                {getIcon(notif.type)}
              </div>
              <div className="notification-text-cell">
                <div className="notification-message">{notif.message}</div>
                <div className="notification-timestamp">{notif.timestamp}</div>
              </div>
            </div>
          ))
        )}
      </div>
      <Link to="/notifications" className="notification-center-btn">
        TO NOTIFICATION CENTER{" "}
        <span className="notification-center-arrow">↗</span>
      </Link>
    </div>
  );
}
