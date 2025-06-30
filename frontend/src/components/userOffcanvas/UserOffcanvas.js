// src/components/userOffcanvas/UserOffcanvas.js
import React, { useEffect, useState } from "react";
import { FaPhoneAlt, FaMapMarkerAlt, FaTimes } from "react-icons/fa";
import useAuthStore from "../../stores/useAuthStore";
import "./UserOffcanvas.css";

const UserOffcanvas = ({ user, isOpen, onClose }) => {
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [avatarLoading, setAvatarLoading] = useState(false);

  // ✅ DEBUG: Log do user recebido
  useEffect(() => {
    console.log("🔍 OFFCANVAS - User data received:", user);
    if (user) {
      console.log("🔍 OFFCANVAS - User properties:");
      console.log("  - ID:", user.id);
      console.log("  - Name:", user.name, user.surname);
      console.log("  - FirstName/LastName:", user.firstName, user.lastName);
      console.log("  - Email:", user.email);
      console.log("  - Role (raw):", user.role);
      console.log("  - Workplace (raw):", user.workplace);
      console.log("  - Office (raw):", user.office);
      console.log("  - Phone:", user.phone);
      console.log("  - HasAvatar:", user.hasAvatar);
      console.log("  - All keys:", Object.keys(user));
    }
  }, [user]);

  // Função para buscar avatar
  const fetchUserAvatar = async (userId) => {
    if (!userId || avatarLoading) return;

    console.log("🔍 OFFCANVAS - Fetching avatar for user ID:", userId);
    setAvatarLoading(true);

    try {
      const possiblePaths = [
        `/api/users/${userId}/avatar`,
        `/users/${userId}/avatar`,
        `/api/avatar/${userId}`,
      ];

      for (const path of possiblePaths) {
        try {
          console.log("🔍 OFFCANVAS - Trying avatar path:", path);
          const response = await fetch(path, {
            method: "GET",
            credentials: "include",
          });

          console.log(
            "🔍 OFFCANVAS - Avatar response status:",
            response.status
          );

          if (response.ok) {
            const blob = await response.blob();
            const url = URL.createObjectURL(blob);
            console.log(
              "✅ OFFCANVAS - Avatar loaded successfully from:",
              path
            );
            setAvatarUrl(url);
            return;
          }
        } catch (err) {
          console.log("❌ OFFCANVAS - Failed to fetch from:", path, err);
        }
      }

      console.log("❌ OFFCANVAS - No avatar found, using default");
      setAvatarUrl(null);
    } catch (error) {
      console.error("❌ OFFCANVAS - Error fetching avatar:", error);
      setAvatarUrl(null);
    } finally {
      setAvatarLoading(false);
    }
  };

  // Carregar avatar quando user muda
  useEffect(() => {
    if (user?.id) {
      setAvatarUrl(null);
      fetchUserAvatar(user.id);
    }

    return () => {
      if (avatarUrl) {
        URL.revokeObjectURL(avatarUrl);
      }
    };
  }, [user?.id]);

  // Fechar ao pressionar ESC
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.keyCode === 27) onClose();
    };
    if (isOpen) {
      document.addEventListener("keydown", handleEsc);
      document.body.style.overflow = "hidden";
    }
    return () => {
      document.removeEventListener("keydown", handleEsc);
      document.body.style.overflow = "unset";
    };
  }, [isOpen, onClose]);

  // Fechar ao clicar no backdrop
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  // ✅ FUNÇÃO para formatar role
  const formatRole = (role) => {
    console.log("🔍 OFFCANVAS - Formatting role:", role);
    if (!role) {
      console.log("🔍 OFFCANVAS - Role is null/undefined, returning N/A");
      return "N/A";
    }
    const formatted = role.replace(/_/g, " ").toUpperCase();
    console.log("🔍 OFFCANVAS - Formatted role:", formatted);
    return formatted;
  };

  // ✅ FUNÇÃO para formatar office - VERSÃO INTELIGENTE
  const formatOffice = () => {
    console.log("🔍 OFFCANVAS - Debugging office properties:");
    console.log("  - user.workplace:", user.workplace);
    console.log("  - user.office:", user.office);

    // Tentar múltiplas propriedades possíveis
    const officeValue = user.workplace || user.office;

    console.log("🔍 OFFCANVAS - Final office value:", officeValue);

    if (!officeValue) {
      console.log("🔍 OFFCANVAS - Office is null/undefined, returning N/A");
      return "N/A";
    }

    const formatted = officeValue
      .replace(/_/g, " ")
      .toLowerCase()
      .replace(/\b\w/g, (l) => l.toUpperCase());
    console.log("🔍 OFFCANVAS - Formatted office:", formatted);
    return formatted;
  };

  if (!user) return null;

  // ✅ NOMES INTELIGENTES - usar o que estiver disponível
  const displayName =
    user.firstName && user.lastName
      ? `${user.firstName} ${user.lastName}`
      : `${user.name || ""} ${user.surname || ""}`.trim() || "N/A";

  return (
    <div
      className={`user-offcanvas-backdrop ${isOpen ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`user-offcanvas ${isOpen ? "open" : ""}`}>
        {/* Botão fechar */}
        <button className="user-offcanvas-close" onClick={onClose}>
          <FaTimes />
        </button>

        {/* Conteúdo centrado */}
        <div className="user-offcanvas-content">
          {/* Foto de perfil */}
          <div className="user-offcanvas-avatar">
            {avatarLoading ? (
              <div className="avatar-loading">Loading...</div>
            ) : (
              <img
                src={
                  avatarUrl ||
                  "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjc1IiBoZWlnaHQ9IjI3NSIgdmlld0JveD0iMCAwIDI3NSAyNzUiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMTM3LjUiIGN5PSIxMzcuNSIgcj0iMTM3LjUiIGZpbGw9IiNmMGYwZjAiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSI0OCIgZmlsbD0iIzk5OSI+VVNFUjwvdGV4dD48L3N2Zz4="
                }
                alt={displayName}
                onError={(e) => {
                  console.log(
                    "❌ OFFCANVAS - Image failed to load:",
                    e.target.src
                  );
                  e.target.src =
                    "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjc1IiBoZWlnaHQ9IjI3NSIgdmlld0JveD0iMCAwIDI3NSAyNzUiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMTM3LjUiIGN5PSIxMzcuNSIgcj0iMTM3LjUiIGZpbGw9IiNmMGYwZjAiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSI0OCIgZmlsbD0iIzk5OSI+VVNFUjwvdGV4dD48L3N2Zz4=";
                }}
              />
            )}
          </div>

          {/* Nome completo */}
          <h1 className="user-offcanvas-name">{displayName}</h1>

          {/* Role */}
          <p className="user-offcanvas-role">{formatRole(user.role)}</p>

          {/* Email */}
          <p className="user-offcanvas-email">{user.email}</p>

          {/* Informações em 2 colunas */}
          <div className="user-offcanvas-info">
            {/* Coluna 1: Telemóvel */}
            <div className="user-offcanvas-info-item">
              <FaPhoneAlt className="user-offcanvas-icon" />
              <span>{user.phone || "N/A"}</span>
            </div>

            {/* Coluna 2: Localização */}
            <div className="user-offcanvas-info-item">
              <FaMapMarkerAlt className="user-offcanvas-icon" />
              <span>{formatOffice()}</span>
            </div>
          </div>

          {/* Botão View Profile */}
          <button
            className="user-offcanvas-profile-btn"
            onClick={() => {
              console.log("🔍 OFFCANVAS - Navigating to profile:", user.id);
              window.location.href = `/profile?id=${user.id}`;
            }}
          >
            View Profile
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserOffcanvas;
