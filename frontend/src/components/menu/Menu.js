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
import { useTranslation } from "react-i18next";
import handleLogout from "../../handles/handleLogout";

export default function Menu({
  onLogout,
  language = "en",
  setLanguage = () => {},
  show = false,
  onClose,
}) {
  const { t } = useTranslation();
  const userId = useAuthStore((state) => state.user?.id);
  const isUserAdmin = useAuthStore((state) => state.isUserAdmin());
  const isUserManager = useAuthStore((state) => state.isUserManager());

  // Internationalized menu labels
  const menuDashboard = t("menuDashboard");
  const menuProfile = t("menuProfile");
  const menuUsers = t("menuUsers");
  const menuTraining = t("menuTraining");
  const menuAppraisal = t("menuAppraisal");
  const menuCycles = t("menuCycles");
  const menuSettings = t("menuSettings");
  const menuDarkMode = t("menuDarkMode");
  const menuDarkModeBeta = t("menuDarkModeBeta");
  const menuLanguage = t("menuLanguage");
  const menuLogout = t("menuLogout");

  const location = useLocation();
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);
  const [darkMode, setDarkMode] = useState(false);

  // Lista de itens do menu, com ícones, cores, rotas e flags especiais
  const menuItems = [
    {
      key: "dashboard",
      label: menuDashboard,
      icon: <TbLayoutDashboardFilled />,
      color: "#B61B23",
      route: "/dashboard",
      adminOnly: false,
      managerOnly: false,
    },
    {
      key: "profile",
      label: menuProfile,
      icon: <FaUserCircle />,
      color: "#9747FF",
      route: userId ? `/profile?id=${userId}` : "/profile",
      adminOnly: false,
      managerOnly: false,
    },
    {
      key: "users",
      label: menuUsers,
      icon: <FaUsers />,
      color: "#3F861E",
      route: "/users",
      adminOnly: false,
      managerOnly: false,
    },
    {
      key: "training",
      label: menuTraining,
      icon: <FaBook />,
      color: "#FF5900",
      route: "/courses",
      adminOnly: true,
      managerOnly: false,
    },
    {
      key: "appraisal",
      label: menuAppraisal,
      icon: <FaAward />,
      color: "#FDD835",
      route: "/appraisals",
      adminOnly: false,
      managerOnly: true,
    },
    {
      key: "cycles",
      label: menuCycles,
      icon: <IoCalendar />,
      color: "#00B9CD",
      route: "/cycles",
      adminOnly: true,
      managerOnly: false,
    },
    {
      key: "settings",
      label: menuSettings,
      icon: <IoSettings />,
      color: "#1976d2",
      route: "/settings",
      adminOnly: true,
      managerOnly: false,
    },
    {
      key: "darkmode",
      label: menuDarkMode,
      icon: <MdDarkMode />,
      color: "#818488",
      route: null,
      adminOnly: false,
      managerOnly: false,
      isToggle: true,
    },
    {
      key: "language",
      label: menuLanguage,
      icon: null,
      color: null,
      route: null,
      adminOnly: false,
      managerOnly: false,
      isLanguage: true,
    },
    {
      key: "logout",
      label: menuLogout,
      icon: <MdLogout />,
      color: "#818488",
      route: null,
      adminOnly: false,
      managerOnly: false,
      isLogout: true,
    },
  ];

  const filteredItems = menuItems.filter((item) => {
    if(item.managerOnly && !(isUserManager || isUserAdmin)) {
      return false;
    }
    if (item.adminOnly && !isUserAdmin) {
      return false;
    }
    return true;
  });

  const handleMouseEnter = () => {
    if (window.innerWidth > 480) setExpanded(true);
  };
  const handleMouseLeave = () => {
    if (window.innerWidth > 480) setExpanded(false);
  };

  const handleItemClick = (item) => {
    if (item.route) navigate(item.route);
    if (window.innerWidth <= 480 && onClose) onClose();
  };

  const handleMenuLogout = async () => {
    await handleLogout(navigate);
    if (onLogout) onLogout(); // Optionally call parent callback
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
              onClick={async () => {
                if (item.route) navigate(item.route);
                // Logout chama função handleMenuLogout, outros navegam
                if (item.isLogout) await handleMenuLogout();
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
                      {menuDarkMode}{" "}
                      <span className="menu-darkmode-beta">
                        {menuDarkModeBeta}
                      </span>
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
