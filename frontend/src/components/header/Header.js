import React, { useEffect, useRef, useState } from "react";
import { FaBars, FaRegComments, FaRegBell, FaUserCircle } from "react-icons/fa";
import "./Header.css";
import logo from "../../assets/logos/citrus_white.png";
import NotificationDropdown from "../dropdowns/NotificationDropdown";

export default function Header({
  userName,
  userEmail,
  avatarUrl,
  unreadMessages = 0,
  unreadNotifications = 0,
}) {
  // Divide o nome do user
  const [firstName, ...rest] = userName.split(" ");
  const lastName = rest.join(" ");

  const [showNotifications, setShowNotifications] = useState(false);
  const notifRef = useRef();

  useEffect(() => {
    function handleClickOutside(event) {
      if (notifRef.current && !notifRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
    }
    if (showNotifications) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showNotifications]);

  const notifications = [
    {
      id: 1,
      message: "Nova mensagem de João",
      time: "2 mins ago",
      read: false,
    },
    {
      id: 2,
      message: "Seu relatório foi aprovado",
      time: "1 hour ago",
      read: true,
    },
    {
      id: 3,
      message: "Lembrete: Reunião às 15h",
      time: "3 hours ago",
      read: false,
    },
  ];

  return (
    <header className="citrus-header">
      {/* Logotipo + CITRUS (desktop) ou burger menu (tablet/mobile) */}
      <div className="header-cell header-logo-cell">
        <img src={logo} alt="Citrus logo" className="header-logo" />
        <span className="header-title">CITRUS</span>
        <button className="header-burger" aria-label="Menu">
          <FaBars />
        </button>
      </div>
      {/* Células vazias (desktop apenas) */}
      <div className="header-cell header-empty" />
      <div className="header-cell header-empty" />
      {/* Ícones */}
      <div className="header-cell header-icons">
        <div className="header-icon-wrapper">
          <FaRegComments />
          {unreadMessages > 0 && (
            <span className="header-badge">{unreadMessages}</span>
          )}
        </div>
        <div className="header-icon-wrapper" ref={notifRef}>
          <FaRegBell
            onClick={() => setShowNotifications((v) => !v)}
            style={{ cursor: "pointer" }}
          />
          {unreadNotifications > 0 && (
            <span className="header-badge">{unreadNotifications}</span>
          )}
          {showNotifications && (
            <NotificationDropdown notifications={notifications} />
          )}
        </div>
        <div className="header-icon-wrapper">
          {avatarUrl ? (
            <img src={avatarUrl} alt="User avatar" className="header-avatar" />
          ) : (
            <FaUserCircle />
          )}
        </div>
      </div>
      {/* Nome e email do user (desktop apenas) */}
      <div className="header-cell header-user">
        <div className="header-user-name">
          {firstName} {lastName}
        </div>
        <div className="header-user-email">{userEmail}</div>
      </div>
    </header>
  );
}
