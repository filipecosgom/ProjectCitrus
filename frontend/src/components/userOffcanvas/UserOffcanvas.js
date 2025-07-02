import React, { useEffect, useState } from "react";
import { FaPhoneAlt, FaMapMarkerAlt, FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import "./UserOffcanvas.css";

const UserOffcanvas = ({ user, isOpen, onClose }) => {
  const { t } = useTranslation();
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [avatarLoading, setAvatarLoading] = useState(false);
  const [shouldRender, setShouldRender] = useState(false); // ✅ NOVO estado
  const [isAnimating, setIsAnimating] = useState(false); // ✅ NOVO estado

  // ✅ CONTROLAR renderização e animação
  useEffect(() => {
    if (isOpen) {
      // Mostrar o componente primeiro
      setShouldRender(true);
      // Delay micro para trigger da animação de entrada
      const timer = setTimeout(() => {
        setIsAnimating(true);
      }, 10); // ✅ 10ms delay para garantir o DOM update

      return () => clearTimeout(timer);
    } else {
      // Iniciar animação de saída
      setIsAnimating(false);
      // Aguardar animação terminar antes de esconder
      const timer = setTimeout(() => {
        setShouldRender(false);
      }, 400); // ✅ Mesma duração da transição CSS (0.4s)

      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  // Buscar avatar quando user muda
  useEffect(() => {
    if (!user?.id) {
      setAvatarUrl(null);
      return;
    }

    let isCancelled = false;

    const fetchAvatar = async () => {
      if (!user.hasAvatar) {
        setAvatarUrl(null);
        return;
      }

      setAvatarLoading(true);
      try {
        const result = await handleGetUserAvatar(user.id);
        if (!isCancelled) {
          if (result.success && result.avatar) {
            setAvatarUrl(result.avatar);
          } else {
            setAvatarUrl(null);
          }
        }
      } catch (error) {
        if (!isCancelled) {
          console.error("Error fetching avatar:", error);
          setAvatarUrl(null);
        }
      } finally {
        if (!isCancelled) {
          setAvatarLoading(false);
        }
      }
    };

    fetchAvatar();

    return () => {
      isCancelled = true;
      if (avatarUrl?.startsWith("blob:")) {
        URL.revokeObjectURL(avatarUrl);
      }
    };
  }, [user?.id, user?.hasAvatar]);

  // Controlar scroll da página
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }

    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  // Fechar com ESC
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener("keydown", handleEsc);
    }

    return () => {
      document.removeEventListener("keydown", handleEsc);
    };
  }, [isOpen, onClose]);

  // Fechar ao clicar no backdrop
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  // FUNÇÃO para formatar role - IGUAL ao UserCard
  const formatRole = (role) => {
    if (!role) return t("users.na");
    return role.replace(/_/g, " ");
  };

  // FUNÇÃO para formatar office - IGUAL ao UserCard
  const formatOffice = (office) => {
    if (!office) return t("users.na");
    return office.replace(/_/g, " ");
  };

  // FUNÇÃO para account state
  const getAccountStateInfo = (accountState) => {
    if (accountState === "COMPLETE") {
      return {
        text: t("users.accountStateComplete"),
        className: "account-state-complete",
      };
    }
    return {
      text: t("users.accountStateIncomplete"),
      className: "account-state-incomplete",
    };
  };

  // NAVEGAÇÃO para profile
  const handleViewProfile = () => {
    window.location.href = `/profile?id=${user.id}`;
  };

  // ✅ NÃO RENDERIZAR se shouldRender for false
  if (!user || !shouldRender) return null;

  const accountStateInfo = getAccountStateInfo(user.accountState);

  return (
    <div
      className={`user-offcanvas-backdrop ${isAnimating ? "open" : ""}`} // ✅ USAR isAnimating
      onClick={handleBackdropClick}
    >
      <div className={`user-offcanvas ${isAnimating ? "open" : ""}`}>
        {" "}
        {/* ✅ USAR isAnimating */}
        {/* Botão fechar X no canto superior direito */}
        <button className="user-offcanvas-close" onClick={onClose}>
          <FaTimes />
        </button>
        {/* Conteúdo centrado */}
        <div className="user-offcanvas-content">
          {/* Avatar 275px com especificações */}
          <div className="user-offcanvas-avatar">
            {avatarLoading ? (
              <div className="avatar-loading">{t("users.avatarLoading")}</div>
            ) : (
              <img
                src={
                  avatarUrl || generateInitialsAvatar(user.name, user.surname)
                }
                alt={`${user.name} ${user.surname}`}
                onError={(e) => {
                  e.target.src = generateInitialsAvatar(
                    user.name,
                    user.surname
                  );
                }}
              />
            )}
          </div>

          {/* Nome completo - font título */}
          <h1 className="user-offcanvas-name">
            {user.name} {user.surname}
          </h1>

          {/* Cargo - font secundária, preto */}
          <p className="user-offcanvas-role">{formatRole(user.role)}</p>

          {/* Email - font secundária, cinza */}
          <p className="user-offcanvas-email">{user.email}</p>

          {/* 2 colunas: Phone + Office */}
          <div className="user-offcanvas-info">
            <div className="user-offcanvas-info-item">
              <FaPhoneAlt className="user-offcanvas-icon" />
              <span>{user.phone || t("users.na")}</span>
            </div>

            <div className="user-offcanvas-info-item">
              <FaMapMarkerAlt className="user-offcanvas-icon" />
              <span>{formatOffice(user.office)}</span>
            </div>
          </div>

          {/* Account State - Verde/Vermelho conforme especificado */}
          <div
            className={`user-offcanvas-account-state ${accountStateInfo.className}`}
          >
            {accountStateInfo.text}
          </div>

          {/* Botão View Profile - estilização da app */}
          <button
            className="main-button user-offcanvas-profile-btn"
            onClick={handleViewProfile}
          >
            {t("users.viewProfile")}
          </button>
        </div>
      </div>
    </div>
  );
};

// FUNÇÃO para gerar avatar com iniciais
export const generateInitialsAvatar = (name, surname) => {
  console.log("🔍 DEBUG - Generating initials avatar for:", { name, surname });
  const initials = `${name?.[0] || ""}${surname?.[0] || ""}`.toUpperCase();
  return `data:image/svg+xml;base64,${btoa(`
    <svg width="275" height="275" viewBox="0 0 275 275" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="137.5" cy="137.5" r="137.5" fill="#f0f0f0"/>
      <text x="50%" y="50%" text-anchor="middle" dy=".3em" font-family="Arial" font-size="80" fill="#666">
        ${initials}
      </text>
    </svg>
  `)}`;
};

export default UserOffcanvas;
