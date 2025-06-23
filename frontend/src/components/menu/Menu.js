import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { TbLayoutDashboardFilled } from "react-icons/tb";
import { FaUserCircle, FaUsers, FaBook, FaAward } from "react-icons/fa";
import { IoCalendar, IoSettings } from "react-icons/io5";
import { MdDarkMode, MdLogout } from "react-icons/md";
import LanguageDropdown from "../languages/LanguageDropdown";
import "./Menu.css";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import useAuthStore from "../../stores/useAuthStore";

export default function Menu({
  onLogout,
  language = "en", // idioma atual (default: en)
  setLanguage = () => {}, // função para mudar idioma
  show = false, // se o menu está visível (mobile)
  onClose, // função para fechar menu (mobile)
}) {
  const userId = useAuthStore((state) => state.user?.id);
  // Lista de itens do menu, com ícones, cores, rotas e flags especiais
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
      route: userId ? `/profile?id=${userId}` : "/profile",
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
  const location = useLocation();
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false); // controla expansão em desktop/tablet
  const [darkMode, setDarkMode] = useState(false); // estado do toggle dark mode

  useEffect(() => {
    console.log("MENU");
    console.log(userId);
  }, []);

  // Lê isAdmin do sessionStorage (true/false)
  const isAdmin = sessionStorage.getItem("isAdmin") === "true";

  // Filtra itens do menu se não for admin (esconde cycles/settings)
  const filteredItems = isAdmin
    ? menuItems
    : menuItems.filter(
        (item) => item.key !== "cycles" && item.key !== "settings"
      );

  // Expande menu ao hover (apenas desktop/tablet)
  const handleMouseEnter = () => {
    if (window.innerWidth > 480) setExpanded(true);
  };
  const handleMouseLeave = () => {
    if (window.innerWidth > 480) setExpanded(false);
  };

  // Navegação ao clicar num item do menu
  const handleItemClick = (item) => {
    if (item.route) navigate(item.route); // navega para a rota
    // Em mobile, fecha o menu ao navegar
    if (window.innerWidth <= 480 && onClose) onClose();
  };

  // Fecha menu ao clicar fora (apenas mobile)
  React.useEffect(() => {
    if (window.innerWidth <= 1024 && show) {
      const handleClick = (e) => {
        if (!e.target.closest(".citrus-menu") && onClose) onClose();
      };
      document.addEventListener("mousedown", handleClick);
      return () => document.removeEventListener("mousedown", handleClick);
    }
  }, [show, onClose]);

  // Força menu expandido em mobile quando está visível
  const isMobile = window.innerWidth <= 480;
  const isTablet = window.innerWidth > 480 && window.innerWidth <= 1024;
  const forceExpanded = (isMobile || isTablet) && show;
  const menuClass = [
    "citrus-menu",
    expanded || forceExpanded ? "expanded" : "",
    show ? "show" : "",
  ].join(" ");

  return (
    <nav
      className={menuClass}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      style={isMobile ? { pointerEvents: show ? "auto" : "none" } : {}}
    >
      <div className="citrus-menu-inner">
        {filteredItems.map((item) => {
          if (item.isLanguage) {
            // Menu expandido: só LanguageDropdown
            if (expanded || forceExpanded) {
              return (
                <div
                  key={item.key}
                  className="menu-cell"
                  style={{
                    padding: 0,
                    justifyContent: "center",
                    alignItems: "center",
                  }}
                >
                  <LanguageDropdown
                    language={language}
                    setLanguage={setLanguage}
                    compact={false}
                  />
                </div>
              );
            }
            // Menu reduzido: só bandeira como ícone
            return (
              <div
                key={item.key}
                className="menu-cell"
                style={{
                  justifyContent: "center",
                  alignItems: "center",
                }}
              >
                <div className="menu-cell-icon">
                  <img
                    src={language === "pt" ? flagPt : flagEn}
                    alt={language === "pt" ? "Português" : "English"}
                    className="menu-language-flag"
                    width={28}
                    height={18}
                  />
                </div>
              </div>
            );
          }

          return (
            <div
              key={item.key}
              className={`menu-cell${
                item.route && location.pathname.startsWith(item.route)
                  ? " selected"
                  : ""
              }${item.isLogout ? " menu-cell-logout" : ""}`}
              onClick={() => {
                if (item.route) navigate(item.route);
                // Logout chama função onLogout, outros navegam
                if (item.isLogout && onLogout) onLogout();
                else if (!item.isToggle && !item.isLanguage)
                  handleItemClick(item);
              }}
              style={{
                pointerEvents: item.isLanguage ? "none" : "auto",
              }}
            >
              {/* Ícone do menu */}
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
              {/* Texto ou componente especial */}
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
                  expanded || forceExpanded ? (
                    <div className="menu-language-row">
                      <LanguageDropdown
                        language={language}
                        setLanguage={setLanguage}
                        compact={!(expanded || forceExpanded)}
                      />
                    </div>
                  ) : (
                    <img
                      src={language === "pt" ? flagPt : flagEn}
                      alt={language === "pt" ? "Português" : "English"}
                      className="menu-language-flag"
                      width={28}
                      height={18}
                      style={{ margin: "0 auto" }}
                    />
                  )
                ) : (
                  // Texto normal
                  item.label
                )}
              </div>
            </div>
          );
        })}
      </div>
    </nav>
  );
}
