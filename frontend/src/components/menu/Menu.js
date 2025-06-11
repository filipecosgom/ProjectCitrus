import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { TbLayoutDashboardFilled } from "react-icons/tb";
import { FaUserCircle, FaUsers, FaBook, FaAward } from "react-icons/fa";
import { IoCalendar, IoSettings } from "react-icons/io5";
import { MdDarkMode, MdLogout } from "react-icons/md";
import LanguageDropdown from "../languages/LanguageDropdown";
import "./Menu.css";

const menuItems = [
  {
    key: "dashboard",
    label: "Dashboard",
    icon: <TbLayoutDashboardFilled />,
    color: "#B61B23",
    route: "/dashboard",
    admin: true,
  },
  {
    key: "profile",
    label: "Profile",
    icon: <FaUserCircle />,
    color: "#9747FF",
    route: "/profile",
    admin: true,
  },
  {
    key: "users",
    label: "Users",
    icon: <FaUsers />,
    color: "#3F861E",
    route: "/users",
    admin: true,
  },
  {
    key: "training",
    label: "Training",
    icon: <FaBook />,
    color: "#FF5900",
    route: "/training",
    admin: true,
  },
  {
    key: "appraisal",
    label: "Appraisal",
    icon: <FaAward />,
    color: "#FDD835",
    route: "/appraisal",
    admin: true,
  },
  {
    key: "cycles",
    label: "Cycles",
    icon: <IoCalendar />,
    color: "#00B9CD",
    route: "/cycles",
    admin: true, // só admin
  },
  {
    key: "settings",
    label: "Settings",
    icon: <IoSettings />,
    color: "#1976d2",
    route: "/settings",
    admin: true, // só admin
  },
  {
    key: "darkmode",
    label: "Dark Mode",
    icon: <MdDarkMode />,
    color: "#818488",
    route: null,
    admin: true,
    isToggle: true,
  },
  {
    key: "language",
    label: "Language",
    icon: null,
    color: null,
    route: null,
    admin: true,
    isLanguage: true,
  },
  {
    key: "logout",
    label: "Logout",
    icon: <MdLogout />,
    color: "#818488",
    route: null,
    admin: true,
    isLogout: true,
  },
];

export default function Menu({
  onLogout,
  language = "en", // valor default
  setLanguage = () => {},
  show = false,
  onClose,
}) {
  const location = useLocation();
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);
  const [darkMode, setDarkMode] = useState(false);

  // Lê isAdmin do sessionStorage
  const isAdmin = sessionStorage.getItem("isAdmin") === "true";

  // Filtra items se não for admin
  const filteredItems = isAdmin
    ? menuItems
    : menuItems.filter(
        (item) => item.key !== "cycles" && item.key !== "settings"
      );

  // Expande ao hover (apenas desktop/tablet)
  const handleMouseEnter = () => {
    if (window.innerWidth > 480) setExpanded(true);
  };
  const handleMouseLeave = () => {
    if (window.innerWidth > 480) setExpanded(false);
  };

  // Navegação ao clicar
  const handleItemClick = (item) => {
    if (item.route) navigate(item.route);
    if (window.innerWidth <= 480 && onClose) onClose(); // Fecha menu em mobile ao navegar
  };

  // Fecha ao clicar fora (mobile)
  React.useEffect(() => {
    if (window.innerWidth > 480 || !show) return;
    const handleClick = (e) => {
      if (!e.target.closest(".citrus-menu")) onClose && onClose();
    };
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, [show, onClose]);

  return (
    <nav
      className={`citrus-menu${expanded ? " expanded" : ""}${
        show ? " show" : ""
      }`}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      style={
        window.innerWidth <= 480
          ? { pointerEvents: show ? "auto" : "none" }
          : {}
      }
    >
      {/* Botão fechar só em mobile */}
      {window.innerWidth <= 480 && show && (
        <button className="menu-close-btn" onClick={onClose} aria-label="Close">
          &times;
        </button>
      )}
      <div className="citrus-menu-inner">
        {filteredItems.map((item, idx) => (
          <div
            key={item.key}
            className={`menu-cell${
              item.route && location.pathname.startsWith(item.route)
                ? " selected"
                : ""
            }${item.isLogout ? " menu-cell-logout" : ""}`}
            onClick={() => {
              if (item.isLogout && onLogout) onLogout();
              else if (!item.isToggle && !item.isLanguage)
                handleItemClick(item);
            }}
            style={{
              pointerEvents: item.isLanguage ? "none" : "auto",
            }}
          >
            <div
              className="menu-cell-icon"
              style={{
                color:
                  item.route && location.pathname.startsWith(item.route)
                    ? "#fff"
                    : item.color,
              }}
            >
              {item.icon}
            </div>
            <div className="menu-cell-label">
              {/* Dark Mode Toggle */}
              {item.isToggle ? (
                <div className="menu-darkmode-row">
                  <span>
                    Dark mode <span className="menu-darkmode-beta">Beta</span>
                  </span>
                  <label className="switch">
                    <input
                      type="checkbox"
                      checked={darkMode}
                      onChange={() => setDarkMode((v) => !v)}
                    />
                    <span className="slider round"></span>
                  </label>
                </div>
              ) : item.isLanguage ? (
                <div className="menu-language-row">
                  <LanguageDropdown
                    language={language}
                    setLanguage={setLanguage}
                    compact={!expanded}
                  />
                </div>
              ) : (
                item.label
              )}
            </div>
          </div>
        ))}
      </div>
    </nav>
  );
}
