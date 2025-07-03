import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import { fetchConversationPreviews } from "../../api/messagesApi";
import "./NotificationDropdown.css";

export default function MessageDropdown({ isVisible }) {
  const [conversations, setConversations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isVisible) {
      loadConversations();

      // Auto-refresh a cada 30 segundos
      const interval = setInterval(() => {
        loadConversations();
      }, 30000);

      return () => clearInterval(interval);
    }
  }, [isVisible]);

  const loadConversations = async () => {
    try {
      setLoading(true);
      setError(null);

      const result = await fetchConversationPreviews();

      if (result.success && result.data.success) {
        setConversations(result.data.data || []);
      } else {
        setError(result.error?.message || "Failed to load conversations");
      }
    } catch (err) {
      setError("Failed to load conversations");
      console.error("Error loading conversations:", err);
    } finally {
      setLoading(false);
    }
  };

  // Função para formatar a data do array para string legível
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

  // Limitar a 6 conversas
  const visibleConversations = conversations.slice(0, 6);

  return (
    <div className="notification-dropdown">
      <div className="notification-list">
        {loading ? (
          <div className="notification-empty">Loading conversations...</div>
        ) : error ? (
          <div className="notification-empty">Error: {error}</div>
        ) : visibleConversations.length === 0 ? (
          <div className="notification-empty">No conversations found</div>
        ) : (
          visibleConversations.map((conversation, idx) => (
            <div className="notification-item" key={conversation.userId || idx}>
              <div className="notification-icon-cell">
                {/* Avatar do user */}
                <FaUserCircle className="notif-icon" color="#818488" />
                {/* Badge de unread count */}
                {conversation.unreadCount > 0 && (
                  <span className="message-unread-badge">
                    {conversation.unreadCount}
                  </span>
                )}
              </div>
              <div className="notification-text-cell">
                {/* Nome do utilizador */}
                <div className="notification-sender">
                  {conversation.userName} {conversation.userSurname}
                </div>
                {/* Última mensagem */}
                <div
                  className={`notification-message ${
                    !conversation.isLastMessageRead ? "unread" : ""
                  }`}
                >
                  {conversation.isLastMessageFromMe ? "You: " : ""}
                  {conversation.lastMessage}
                </div>
                {/* Timestamp */}
                <div className="notification-timestamp">
                  {formatDate(conversation.lastMessageDate)}
                </div>
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
