import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaPhoneAlt, FaMapMarkerAlt, FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import "./AppraisalOffCanvas.css";
import { generateInitialsAvatar } from "../userOffcanvas/UserOffcanvas";
import AppraisalStateBadge from "../appraisalStateBadge/AppraisalStateBadge";
import { FaUserCircle, FaBook, FaAward } from "react-icons/fa";
import AppraisalScoreStarBadge from "../appraisalScoreStarBadge/AppraisalScoreStarBadge";
import AppraisalScoreVerbose from "../appraisalScoreVerbose/AppraisalScoreVerbose";
import handleNotification from "../../handles/handleNotification";
import { handleUpdateAppraisal } from "../../handles/handleUpdateAppraisal";
import useAuthStore from "../../stores/useAuthStore";

const AppraisalOffCanvas = ({ appraisal, isOpen, onClose, onSave }) => {
  const { t } = useTranslation();
  const user = appraisal?.appraisedUser || {};
  const isTheManager = appraisal?.appraisingUser.id === useAuthStore((state) => state.user?.id);
  const isAdmin = useAuthStore((state) => state.isUserAdmin());
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [avatarLoading, setAvatarLoading] = useState(false);
  const [shouldRender, setShouldRender] = useState(false); // ✅ NOVO estado
  const [isAnimating, setIsAnimating] = useState(false); // ✅ NOVO estado
  const [editMode, setEditMode] = useState(false);
  const [editedFeedback, setEditedFeedback] = useState("");
  const [editedScore, setEditedScore] = useState(appraisal?.score ?? 0);
  const [editedState, setEditedState] = useState(appraisal?.state || "IN_PROGRESS"); // Add state for editable appraisal state (for admin in edit mode)
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [showStateDropdown, setShowStateDropdown] = useState(false);
  // Only show validation errors after submit, not after save
  // error: holds the error message (validation or backend)
  // showValidationError: true only if last action was submit and error is a validation error
  const [showValidationError, setShowValidationError] = useState(false);
  const navigate = useNavigate();

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

  // When appraisal changes, reset edit state
  useEffect(() => {
    console.log(appraisal?.state);
    setEditMode(false);
    setEditedFeedback(appraisal?.feedback || "");
    setEditedScore(appraisal?.score ?? 0);
    setEditedState(appraisal?.state || "IN_PROGRESS");
    setError(null);
  }, [appraisal]);

  // Validation function for submit only
  const validateAppraisal = (data) => {
    if (!data.feedback || data.feedback.trim().length === 0) {
      return t("appraisal.validation.feedbackRequired");
    }
    if (!data.score || data.score < 1) {
      return t("appraisal.validation.scoreRequired");
    }
    return null;
  };

  // Helper: is the error a validation error?
  const isValidationError = (err) => {
    if (!err) return false;
    return (
      err === t("appraisal.validation.feedbackRequired") ||
      err === t("appraisal.validation.scoreRequired")
    );
  };

  // Save as draft: no validation, no validation errors shown
  const handleSave = async (appraisalData) => {
    setSaving(true);
    setError(null); // Clear error on save
    setShowValidationError(false); // Hide validation errors on save
    try {
      const updatePayload = {
        id: appraisal.id,
        state: editedState, // Use selected state from dropdown
        ...appraisalData,
      };
      const response = await handleUpdateAppraisal(updatePayload);
      if (response.success) {
        handleNotification("success", "appraisal.saved", { endDate: appraisal.endDate });
        const editedAppraisal = { ...appraisal, ...appraisalData, state: editedState }; // <-- FIXED
        if (onSave) onSave(editedAppraisal);
        Object.assign(appraisal, editedAppraisal);
        setEditMode(false);
        onClose();
      } else {
        setError(t("appraisal.saveError"));
      }
    } catch (e) {
      setError(t("appraisal.saveError"));
    } finally {
      setSaving(false);
    }
  };

  // Submit: validate, show validation errors if any
  const handleSubmit = async (appraisalData) => {
    const validationError = validateAppraisal(appraisalData);
    if (validationError) {
      setError(validationError);
      setShowValidationError(true); // Show validation error
      return;
    }
    setSaving(true);
    setError(null);
    setShowValidationError(false); // Hide validation error on successful submit
    try {
      const updatePayload = {
        id: appraisal.id,
        state: "COMPLETED", // Always set to COMPLETED on submit
        ...appraisalData,
      };
      const response = await handleUpdateAppraisal(updatePayload);
      if (response.success) {
        handleNotification("success", "appraisal.submitted", { endDate: appraisal.endDate });
        const editedAppraisal = { ...appraisal, ...appraisalData, state: "COMPLETED" };
        if (onSave) onSave(editedAppraisal);
        Object.assign(appraisal, editedAppraisal);
        setEditMode(false);
        onClose();
      } else {
        setError(t("appraisal.saveError"));
      }
    } catch (e) {
      setError(t("appraisal.saveError"));
    } finally {
      setSaving(false);
    }
  };

  // Cancel: reset error and validation error display
  const handleCancel = () => {
    setEditMode(false);
    setEditedFeedback(appraisal?.feedback || "");
    setEditedScore(appraisal?.score ?? 0);
    setError(null);
    setShowValidationError(false);
  };

  // Separate form submit handler for future extensibility
  const handleFormSubmit = async (e) => {
    e.preventDefault();
    await handleSave({ feedback: editedFeedback, score: editedScore });
  };

  // ✅ NÃO RENDERIZAR se shouldRender for false
  if (!appraisal || !shouldRender) return null;

  const profileItems = [
    {
      key: "profile",
      icon: <FaUserCircle />,
      color: "#9747FF",
      route: user.id ? `/profile?id=${user.id}` : "/profile",
    },
    {
      key: "training",
      icon: <FaBook />,
      color: "#FF5900",
      route: "/training",
    },
    {
      key: "appraisal",
      icon: <FaAward />,
      color: "#FDD835",
      route: "/appraisals",
    },
  ];

  // Star selector for score (edit mode)
  const StarSelector = ({ value, onChange, max = 4, disabled }) => {
    return (
      <div className={`appraisal-offcanvas-star-selector${disabled ? ' disabled' : ''}`}> 
        {[1,2,3,4].map((val) => (
          <span
            key={val}
            className={`star${value >= val ? ' selected' : ''}`}
            onClick={() => !disabled && onChange(val)}
            tabIndex={disabled ? -1 : 0}
            role="button"
            aria-label={`Set score to ${val}`}
            onKeyDown={e => {
              if (!disabled && (e.key === 'Enter' || e.key === ' ')) onChange(val);
            }}
          >
            <svg
              className="star-svg"
              width="20"
              height="20"
            >
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
            </svg>
          </span>
        ))}
      </div>
    );
  };

  // State for showing/hiding the custom badge dropdown
  
  // List of possible states
  const stateOptions = [
    { value: "IN_PROGRESS", label: t("appraisalStateInProgress") },
    { value: "COMPLETED", label: t("appraisalStateCompleted") },
    { value: "CLOSED", label: t("appraisalStateClosed") },
  ];

  return (
    <div
      className={`appraisal-offcanvas-backdrop ${isAnimating ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`appraisal-offcanvas ${isAnimating ? "open" : ""}`}>
        {/* Botão fechar X no canto superior direito */}
        <div className="appraisal-offcanvas-header">
          {/* State badge or dropdown for admin in edit mode */}
          <div className="appraisal-offcanvas-state-dropdown-wrapper">
            {editMode && isAdmin ? (
              <div className="appraisal-offcanvas-state-dropdown-container">
                <div
                  className="appraisal-offcanvas-state-dropdown-trigger"
                  onClick={() => setShowStateDropdown((v) => !v)}
                  tabIndex={0}
                  onBlur={() => setTimeout(() => setShowStateDropdown(false), 150)}
                >
                  <AppraisalStateBadge state={editedState} dropdownOption={true} />
                </div>
                {showStateDropdown && (
                  <div className="appraisal-offcanvas-state-dropdown-menu">
                    {stateOptions.map(opt => (
                      <div
                        key={opt.value}
                        className="appraisal-offcanvas-state-dropdown-option"
                        onClick={() => { setEditedState(opt.value); setShowStateDropdown(false); }}
                      >
                        <AppraisalStateBadge state={opt.value} dropdownOption={true} />
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ) : (
              <AppraisalStateBadge state={appraisal.state} />
            )}
          </div>
            <FaTimes className="appraisal-offcanvas-close" onClick={onClose}/>
        </div>
        {/* Conteúdo centrado */}
        <div className="appraisal-offcanvas-content">
          {/* Avatar 275px com especificações */}
          <div className="appraisal-offcanvas-avatar">
            {avatarLoading ? (
              <div className="avatar-loading">
                {t("appraisals.avatarLoading")}
              </div>
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
          <h1 className="appraisal-offcanvas-name">
            {user.name} {user.surname}
          </h1>

          {/* Cargo - font secundária, preto */}
          <p className="appraisal-offcanvas-role">{formatRole(user.role)}</p>

          {/* Email - font secundária, cinza */}
          <p className="appraisal-offcanvas-email">{user.email}</p>

          

          <div className="appraisal-offcanvas-profileIcons">
            {profileItems.map((item) => (
              <div
                key={item.key}
                className={`appraisal-offcanvas-menu-cell-icon`}
                onClick={async () => {
                  if (item.route) navigate(item.route);
                }}
                style={{
                  pointerEvents: "auto",
                  color: item.color,
                }}
              >
                {item.icon}
              </div>
            ))}
          </div>
          {/* Always show verbose text, even in edit mode */}
          <AppraisalScoreVerbose score={editMode ? editedScore : appraisal.score} />
          {/* Only show static stars when not editing */}
          {!editMode && <AppraisalScoreStarBadge score={appraisal.score}/>} 
          {/* In edit mode, only show the star selector */}
          {editMode && (
            <>
              <StarSelector value={editedScore} onChange={setEditedScore} disabled={saving} />
            </>
          )}
          <div className="appraisal-offcanvas-feedback-title">
              {t("appraisal.feedback")}
            </div>
          <div className="appraisal-offcanvas-feedbackContainer">
            {editMode ? (
              <form
                onSubmit={handleFormSubmit}
                style={{ display: 'flex', flexDirection: 'column', width: '100%', flex: 1 }}
              >
                <textarea
                  id="appraisal-feedback"
                  value={editedFeedback}
                  onChange={e => setEditedFeedback(e.target.value)}
                  rows={4}
                  className="appraisal-offcanvas-feedback-textarea"
                  disabled={saving}
                />
                {/* Show validation error only after submit, never after save */}
                <div className="appraisal-offcanvas-feedback-error">{showValidationError && error && isValidationError(error) ? error : "\u00A0"}</div>
                <div className="appraisal-offcanvas-feedback-buttons">
                  <button type="button" onClick={() => handleSubmit({ feedback: editedFeedback, score: editedScore })} disabled={saving} className="appraisal-offcanvas-btn appraisal-offcanvas-btn-submit">
                    {t("appraisal.submit")}
                  </button>
                  <button type="submit" disabled={saving} className="appraisal-offcanvas-btn appraisal-offcanvas-btn-save">
                    {t("appraisal.save")}
                  </button>
                  <button type="button" onClick={handleCancel} disabled={saving} className="appraisal-offcanvas-btn appraisal-offcanvas-btn-cancel">
                    {t("appraisal.cancel")}
                  </button>
                </div>
              </form>
            ) : (
              <>
                <div className="appraisal-offcanvas-feedback-box">
                  <p className="appraisal-offcanvas-feedback-text">
                    {appraisal.feedback || t("appraisal.noFeedback")}
                  </p>
                </div>
                <div className="appraisal-offcanvas-feedback-error">{error && !isValidationError(error) ? error : "\u00A0"}</div>
                <div className="appraisal-offcanvas-feedback-buttons appraisal-offcanvas-feedback-buttons-view">
                  {((isAdmin) || (isTheManager && (appraisal.state !== "CLOSED" && appraisal.state !== "COMPLETED"))) && 
                  <button onClick={() => { setEditMode(true); setError(null); setShowValidationError(false); }} className="appraisal-offcanvas-btn appraisal-offcanvas-btn-edit">
                    {t("appraisal.edit")}
                  </button>
                  }
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AppraisalOffCanvas;
