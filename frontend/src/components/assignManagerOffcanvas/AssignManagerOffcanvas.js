import React, { useEffect, useState } from "react";
import { FaTimes, FaUser } from "react-icons/fa";
import UserSearchBar from "../userSearchBar/UserSearchBar";
import Spinner from "../spinner/spinner"; // ✅ ADICIONAR IMPORT
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
  const [selectedNewManager, setSelectedNewManager] = useState(null);
  const [isAssigning, setIsAssigning] = useState(false); // ✅ ADICIONAR estado de loading

  // ✅ HANDLER para seleção de user
  const handleUserSelect = (user) => {
    setSelectedNewManager(user);
    console.log("👤 AssignManager - User selected for promotion:", user);
  };

  // ✅ ATUALIZAR handleAssignClick para mostrar loading
  const handleAssignClick = async () => {
    if (!selectedNewManager || isAssigning) {
      console.warn("❌ No user selected or already assigning");
      return;
    }

    setIsAssigning(true); // ✅ MOSTRAR LOADING

    const assignments = {
      newManagerId: selectedNewManager.id,
      newManagerName: `${selectedNewManager.name} ${selectedNewManager.surname}`,
      newManagerEmail: selectedNewManager.email,
      userIds: selectedUserIds,
      users: selectedUsers,
      action: "promoteAndAssign",
    };

    console.log("🎯 Promoting user to manager and assigning:", assignments);

    try {
      await onAssign(assignments); // ✅ AGUARDAR conclusão
    } catch (error) {
      console.error("❌ Error in assignment:", error);
    } finally {
      setIsAssigning(false); // ✅ REMOVER LOADING
    }
  };

  // ✅ LIMPAR estados quando fechar
  useEffect(() => {
    if (!isOpen) {
      setSelectedNewManager(null);
      setIsAssigning(false); // ✅ RESETAR loading
    }
  }, [isOpen]);

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
            Assign Manager ({selectedUsers.length} users selected)
          </h2>
          <button
            className="assign-manager-close"
            onClick={onClose}
            disabled={isAssigning}
          >
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

          {/* ✅ NOVA: User Search Section */}
          <div className="new-manager-section">
            <h3 className="section-title">
              Select User to Promote as Manager:
            </h3>
            <p className="section-description">
              Search for a user who will be promoted to manager and assigned to
              the selected users above.
            </p>

            {/* ✅ COMPONENTE REUTILIZÁVEL */}
            <UserSearchBar
              selectedUser={selectedNewManager}
              onUserSelect={handleUserSelect}
              placeholder="Search for user to promote as manager..."
              maxResults={30}
              showUserInfo={true}
              compact={true} // Versão compacta para offcanvas
              excludeUserIds={selectedUserIds} // Excluir users que estão sendo atribuídos
              className="assign-manager-search"
            />

            {/* ✅ FEEDBACK do user selecionado */}
            {selectedNewManager && (
              <div className="selected-new-manager-feedback">
                <div className="feedback-icon">🎯</div>
                <div className="feedback-text">
                  <div className="feedback-primary">
                    <strong>
                      {selectedNewManager.name} {selectedNewManager.surname}
                    </strong>{" "}
                    will be promoted to Manager
                  </div>
                  <div className="feedback-secondary">
                    {selectedNewManager.email} •{" "}
                    {selectedNewManager.role?.replace(/_/g, " ")}
                  </div>
                  <div className="feedback-action">
                    ↳ Will manage {selectedUsers.length} user
                    {selectedUsers.length !== 1 ? "s" : ""}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* ✅ Footer com loading state */}
        <div className="assign-manager-footer">
          <button
            className="cancel-btn"
            onClick={onClose}
            disabled={isAssigning}
          >
            Cancel
          </button>
          <button
            className="assign-btn"
            onClick={handleAssignClick}
            disabled={!selectedNewManager || isAssigning}
          >
            {isAssigning ? (
              <>
                <Spinner size="small" />
                Assigning...
              </>
            ) : selectedNewManager ? (
              <>
                Promote & Assign
                <span className="selected-manager-name">
                  → {selectedNewManager.name}
                </span>
              </>
            ) : (
              "Select User First"
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AssignManagerOffcanvas;
