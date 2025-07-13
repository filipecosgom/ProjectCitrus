import React, { useEffect, useRef, useState, useMemo } from "react";
import { FaBars, FaRegComments, FaRegBell, FaUserCircle } from "react-icons/fa";
import "./Header.css";
import logo from "../../assets/logos/citrus_white.png";
import NotificationDropdown from "../dropdowns/NotificationDropdown";
import MessageDropdown from "../dropdowns/MessageDropdown";
import { handleUpdateNotification } from "../../handles/handleNotificationApi";
import Menu from "../menu/Menu";
import useAuthStore from "../../stores/useAuthStore"; // <-- Importa o store
import UserIcon from "../userIcon/UserIcon";
// import useUnreadConversations from "../../hooks/useUnreadConversations";
// import { openNotificationWebSocket } from "../../websockets/useWebSocketNotifications";
import useNotificationStore from "../../stores/useNotificationStore";
import useWebSocketNotifications from "../../websockets/useWebSocketNotifications";

export default function Header({ language, setLanguage }) {
  useWebSocketNotifications();
  // Lê o user do store global
  const { user } = useAuthStore();

  // Protege contra user null
  const firstName = user?.name || "";
  const lastName = user?.surname || "";
  const userEmail = user?.email || "";
  const [showNotifications, setShowNotifications] = useState(false);
  const [showMessages, setShowMessages] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  // useWebSocketNotifications(); // TEMP: Disabled to test for infinite loop cause

  // Simple Zustand selectors for badge counts (no shallow needed)
  const unreadMessageCount = useNotificationStore(
    state => state.messageNotifications.reduce((acc, n) => acc + (!n.notificationIsRead ? 1 : 0), 0)
  );
  const unreadOtherCount = useNotificationStore(
    state => state.otherNotifications.reduce((acc, n) => acc + (!n.notificationIsRead ? 1 : 0), 0)
  );
  // Selector for notifications array
  const otherNotifications = useNotificationStore(state => state.otherNotifications);

  // Selector for messages array
  const messageNotifications = useNotificationStore(state => state.messageNotifications);

  // Handler: Mark all messages as read (local + backend)
  const handleMarkAllMessagesAsRead = async () => {
    useNotificationStore.getState().markAllMessagesAsRead();
    // Backend: mark all unread messages as read
    const unreadMessages = messageNotifications.filter(n => !n.notificationIsRead);
    for (const n of unreadMessages) {
      await handleUpdateNotification({ notificationId: n.id, notificationIsRead: true });
    }
  };

  // Handler: Mark all notifications as read (local + backend)
  const handleMarkAllNotificationsAsRead = async () => {
    useNotificationStore.getState().markAllOthersAsRead();
    // Backend: mark all unread notifications as read
    const unreadOthers = otherNotifications.filter(n => !n.notificationIsRead);
    for (const n of unreadOthers) {
      await handleUpdateNotification({ notificationId: n.id, notificationIsRead: true });
    }
  };

  // Fecha o menu se deixares de estar em mobile
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 480 && showMenu) setShowMenu(false);
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [showMenu]);

  useEffect(() => {
    function handleClickOutside(event) {
    }
    if (showNotifications) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    if (showMessages) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showNotifications, showMessages]);



  return (
    <header className="citrus-header">
      {/* Logotipo + CITRUS (desktop) ou burger menu (tablet/mobile) */}
      <div className="header-cell header-logo-cell">
        <img src={logo} alt="Citrus logo" className="header-logo" />
        <span className="header-title">CITRUS</span>
        <button
          className="header-burger"
          aria-label="Menu"
          onClick={() => setShowMenu((prev) => !prev)} // Toggle: abre/fecha
        >
          <FaBars />
        </button>
      </div>
      {/* Células vazias (desktop apenas) */}
      <div className="header-cell header-empty" />
      <div className="header-cell header-empty" />
      {/* Ícones */}
      <div className="header-cell header-icons">
        <div className="header-icon-wrapper">
          <FaRegComments
            style={{ cursor: "pointer" }}
            onClick={async () => {
              if (!showMessages && unreadMessageCount > 0) {
                await handleMarkAllMessagesAsRead();
              }
              setShowMessages((v) => !v);
              setShowNotifications(false);
            }}
          />
          {unreadMessageCount > 0 && (
            <span className="header-badge header-message-badge">
              {unreadMessageCount}
            </span>
          )}
          {showMessages && (
            <MessageDropdown
              isVisible={showMessages}
              onClose={() => setShowMessages(false)}
            />
          )}
        </div>
        <div className="header-icon-wrapper">
          <FaRegBell
            style={{ cursor: "pointer" }}
            onClick={async () => {
              if (!showNotifications && unreadOtherCount > 0) {
                await handleMarkAllNotificationsAsRead();
              }
              setShowNotifications((v) => !v);
              setShowMessages(false);
            }}
          />
          {unreadOtherCount > 0 && (
            <span className="header-badge">
              {unreadOtherCount}
            </span>
          )}
          {showNotifications && (
            <NotificationDropdown notifications={otherNotifications} />
          )}
        </div>
        {/* FIX: Pass the full user object to UserIcon, not just avatar */}
        <UserIcon user={user} status="check" />
      </div>
      {/* Nome e email do user (desktop apenas) */}
      <div className="header-cell header-user">
        <div className="header-user-name">
          {firstName} {lastName}
        </div>
        <div className="header-user-email">{userEmail}</div>
      </div>
      {/* Renderiza o Menu */}
      <Menu
        show={showMenu}
        onClose={() => setShowMenu(false)}
        language={language} // deve ser sempre definido
        setLanguage={setLanguage} // idem
      />
    </header>
  );
}
