import React, { useEffect, useRef, useState } from "react";
import {
  FaBars,
  FaRegComments,
  FaRegBell,
  FaUserCircle,
  FaRegEnvelope,
} from "react-icons/fa";
import "./Header.css";
import logo from "../../assets/logos/citrus_white.png";
import NotificationDropdown from "../dropdowns/NotificationDropdown";
import MessageDropdown from "../dropdowns/MessageDropdown";
import Menu from "../menu/Menu"; // Importe o componente Menu

export default function Header({
  userName,
  userEmail,
  avatarUrl,
  unreadMessages = 0,
  unreadNotifications = 0,
  language, // Adicionei a prop language
  setLanguage, // Adicionei a prop setLanguage
}) {
  // Divide o nome do user
  const [firstName, ...rest] = userName.split(" ");
  const lastName = rest.join(" ");

  const [showNotifications, setShowNotifications] = useState(false);
  const notifRef = useRef();
  const [showMessages, setShowMessages] = useState(false);
  const messagesRef = useRef();
  const [showMenu, setShowMenu] = useState(false); // Estado para controlar o menu

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

  const messages = [
    {
      id: 1,
      message_content: "Olá! Como estás?",
      sent_date: "2025-06-10 10:00",
      is_read: false,
      sender_id: 2,
      receiver_id: 1,
    },
    // ...mais mensagens
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
          onClick={() => setShowMenu(true)} // Abre o menu ao clicar
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
          {showMessages && <MessageDropdown messages={messages} />}
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
