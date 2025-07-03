import React, { useEffect, useRef, useState } from "react";
import { FaBars, FaRegComments, FaRegBell, FaUserCircle } from "react-icons/fa";
import "./Header.css";
import logo from "../../assets/logos/citrus_white.png";
import NotificationDropdown from "../dropdowns/NotificationDropdown";
import MessageDropdown from "../dropdowns/MessageDropdown";
import Menu from "../menu/Menu";
import useAuthStore from "../../stores/useAuthStore"; // <-- Importa o store
import UserIcon from "../userIcon/UserIcon";
export default function Header({
  unreadMessages = 0,
  unreadNotifications = 0,
  language,
  setLanguage,
}) {
  // Lê o user do store global
  const { user } = useAuthStore();

  // Protege contra user null
  const firstName = user?.name || "";
  const lastName = user?.surname || "";
  const userEmail = user?.email || "";
  const [showNotifications, setShowNotifications] = useState(false);
  const notifRef = useRef();
  const [showMessages, setShowMessages] = useState(false);
  const messagesRef = useRef();
  const [showMenu, setShowMenu] = useState(false);

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
      if (notifRef.current && !notifRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
      if (messagesRef.current && !messagesRef.current.contains(event.target)) {
        setShowMessages(false);
      }
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

  const notifications = [
    {
      id: 1,
      type: "appraisal",
      message: "Nova mensagem de João",
      time: "2 mins ago",
      read: false,
    },
    {
      id: 2,
      type: "appraisal",
      message: "Seu relatório foi aprovado",
      time: "1 hour ago",
      read: true,
    },
    {
      id: 3,
      type: "course",
      message: "Lembrete: Reunião às 15h",
      time: "3 hours ago",
      read: false,
    },
    {
      id: 4,
      type: "course",
      message: "Novo curso disponível: React Avançado",
      time: "Hoje 14:30",
      read: false,
    },
    {
      id: 5,
      type: "appraisal",
      message: "Avaliação anual disponível",
      time: "Ontem 17:00",
      read: false,
    },
    {
      id: 6,
      type: "course",
      message: "Curso de UX/UI atualizado",
      time: "Ontem 15:45",
      read: true,
    },
    {
      id: 7,
      type: "appraisal",
      message: "Feedback recebido do gestor",
      time: "10/06/2025 09:00",
      read: false,
    },
    {
      id: 8,
      type: "course",
      message: "Novo módulo: Design Systems",
      time: "09/06/2025 11:30",
      read: false,
    },
  ];

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
        <div className="header-icon-wrapper" ref={messagesRef}>
          <FaRegComments
            onClick={() => {
              setShowMessages((v) => !v);
              setShowNotifications(false); // Fecha notificações ao abrir mensagens
            }}
            style={{ cursor: "pointer" }}
          />
          {unreadMessages > 0 && (
            <span className="header-badge">{unreadMessages}</span>
          )}
          {showMessages && <MessageDropdown isVisible={showMessages} />}
        </div>
        <div className="header-icon-wrapper" ref={notifRef}>
          <FaRegBell
            onClick={() => {
              setShowNotifications((v) => !v);
              setShowMessages(false); // Fecha mensagens ao abrir notificações
            }}
            style={{ cursor: "pointer" }}
          />
          {unreadNotifications > 0 && (
            <span className="header-badge">{unreadNotifications}</span>
          )}
          {showNotifications && (
            <NotificationDropdown notifications={notifications} />
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
