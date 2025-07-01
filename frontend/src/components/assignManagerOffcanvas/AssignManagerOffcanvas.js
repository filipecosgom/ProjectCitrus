import React, { useEffect, useState } from "react";
import { FaTimes, FaUser } from "react-icons/fa";
import "./AssignManagerOffcanvas.css";

const AssignManagerOffcanvas = ({
  selectedUserIds = [],
  selectedUsers = [],
  isOpen,
  onClose,
  onAssign,
}) => {
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);

  // ✅ ADICIONAR LOGS para debug
  console.log("🔍 AssignManager - Props received:", {
    selectedUserIds,
    selectedUsers: selectedUsers.length,
    isOpen,
    shouldRender,
    isAnimating,
  });

  // ✅ CONTROLAR renderização e animação
  useEffect(() => {
    console.log("🔍 AssignManager - isOpen changed:", isOpen);

    if (isOpen) {
      setShouldRender(true);
      const timer = setTimeout(() => {
        setIsAnimating(true);
      }, 10);
      return () => clearTimeout(timer);
    } else {
      setIsAnimating(false);
      const timer = setTimeout(() => {
        setShouldRender(false);
      }, 400);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

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

  // ✅ ADICIONAR LOG quando não renderizar
  if (!shouldRender) {
    console.log("🔍 AssignManager - Not rendering (shouldRender: false)");
    return null;
  }

  console.log("🔍 AssignManager - Rendering offcanvas!");

  return (
    <div
      className={`assign-manager-backdrop ${isAnimating ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`assign-manager-offcanvas ${isAnimating ? "open" : ""}`}>
        {/* ✅ Header com título e botão fechar */}
        <div className="assign-manager-header">
          <h2 className="assign-manager-title">
            Assign Managers ({selectedUsers.length} users selected)
          </h2>
          <button className="assign-manager-close" onClick={onClose}>
            <FaTimes />
          </button>
        </div>

        {/* ✅ Conteúdo do offcanvas */}
        <div className="assign-manager-content">
          {/* ✅ Lista de users selecionados */}
          <div className="selected-users-section">
            <h3 className="section-title">Selected Users:</h3>
            <div className="selected-users-list">
              {selectedUsers.map((user) => (
                <div key={user.id} className="selected-user-item">
                  <FaUser className="user-icon" />
                  <span className="user-name">
                    {user.name} {user.surname}
                  </span>
                  <span className="user-role">
                    {user.role?.replace(/_/g, " ") || "N/A"}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* ✅ Placeholder para managers search */}
          <div className="managers-section">
            <h3 className="section-title">Select Manager:</h3>
            <div className="managers-placeholder">
              <p>🔍 Manager search will be implemented next...</p>
              <p>📋 Selected users: {selectedUserIds.length}</p>
            </div>
          </div>

          {/* ✅ Footer com botões */}
          <div className="assign-manager-footer">
            <button className="cancel-btn" onClick={onClose}>
              Cancel
            </button>
            <button
              className="assign-btn"
              onClick={() => onAssign({})}
              disabled={true} // TODO: habilitar quando manager for selecionado
            >
              Assign Manager
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssignManagerOffcanvas;
