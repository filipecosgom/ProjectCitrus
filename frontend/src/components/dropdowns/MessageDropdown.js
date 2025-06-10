import React from "react";
import { Link } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import "./NotificationDropdown.css"; // Reutiliza o CSS do NotificationDropdown

export default function MessageDropdown({ messages = [] }) {
  // Mostra no máximo 6, mas só 3 visíveis sem scroll
  const visibleMessages = messages.slice(0, 6);

  return (
    <div className="notification-dropdown">
      <div className="notification-list">
        {visibleMessages.length === 0 ? (
          <div className="notification-empty">No new messages</div>
        ) : (
          visibleMessages.map((msg, idx) => (
            <div className="notification-item" key={msg.id || idx}>
              <div className="notification-icon-cell">
                {/* Avatar do user (futuro: imagem, agora ícone) */}
                <FaUserCircle className="notif-icon" color="#818488" />
              </div>
              <div className="notification-text-cell">
                <div className="notification-message">
                  {msg.message_content}
                </div>
                <div className="notification-timestamp">{msg.sent_date}</div>
              </div>
            </div>
          ))
        )}
      </div>
      <Link to="/messages" className="notification-center-btn">
        TO MESSAGES CENTER <span className="notification-center-arrow">↗</span>
      </Link>
    </div>
  );
}
