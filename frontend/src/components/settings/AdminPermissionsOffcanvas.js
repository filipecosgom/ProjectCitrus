import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { getAllUsers, updateAdminPermission } from "../../api/user";
import useAuthStore from "../../stores/useAuthStore";
import "./AdminPermissionsOffcanvas.css";

const AdminPermissionsOffcanvas = ({ show, onClose }) => {
  const { t } = useTranslation();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [pendingAdmin, setPendingAdmin] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const currentUser = useAuthStore((state) => state.user);

  useEffect(() => {
    if (show) {
      setLoading(true);
      getAllUsers()
        .then((data) => setUsers(data))
        .catch(() => setError(t("errorLoadingUsers")))
        .finally(() => setLoading(false));
    }
  }, [show, t]);

  const handleToggle = (user) => {
    // Prevent self-removal of admin
    if (user.id === currentUser.id && user.userIsAdmin) {
      setError(t("cannotRemoveOwnAdmin"));
      return;
    }
    setSelectedUser(user);
    setPendingAdmin(!user.userIsAdmin);
    setConfirmOpen(true);
  };

  const handleConfirm = async () => {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      await updateAdminPermission(selectedUser.id, pendingAdmin);
      setUsers((prev) =>
        prev.map((u) =>
          u.id === selectedUser.id ? { ...u, userIsAdmin: pendingAdmin } : u
        )
      );
      setSuccess(
        pendingAdmin
          ? t("adminGrantedSuccess", { name: selectedUser.name })
          : t("adminRevokedSuccess", { name: selectedUser.name })
      );
    } catch (e) {
      setError(t("errorUpdatingAdmin"));
    } finally {
      setLoading(false);
      setConfirmOpen(false);
      setSelectedUser(null);
    }
  };

  return (
    <>
      <div
        className={`admin-permissions-backdrop${show ? " open" : ""}`}
        onClick={onClose}
      />
      <aside
        className={`admin-permissions-offcanvas${show ? " open" : ""}`}
        tabIndex="-1"
        aria-modal="true"
        role="dialog"
      >
        <div className="admin-permissions-header">
          <span className="admin-permissions-title">
            {t("manageAdminPermissions")}
          </span>
          <button
            className="admin-permissions-close"
            onClick={onClose}
            aria-label={t("close")}
          >
            &times;
          </button>
        </div>
        <div className="admin-permissions-body">
          {loading && (
            <div className="admin-permissions-alert">{t("loading")}</div>
          )}
          {error && (
            <div className="admin-permissions-alert error">{error}</div>
          )}
          {success && (
            <div className="admin-permissions-alert success">{success}</div>
          )}
          <ul className="admin-permissions-list">
            {users.map((user) => (
              <li key={user.id} className="admin-permissions-user">
                <div className="admin-permissions-user-info">
                  <span className="admin-permissions-user-name">
                    {user.name} {user.surname}
                  </span>
                  <span className="admin-permissions-user-email">
                    {user.email}
                  </span>
                  <span
                    className={
                      "admin-permissions-badge" +
                      (user.userIsAdmin ? "" : " user")
                    }
                  >
                    {user.userIsAdmin ? t("admin") : t("user")}
                  </span>
                </div>
                <label className="admin-permissions-toggle">
                  <input
                    type="checkbox"
                    checked={user.userIsAdmin}
                    disabled={user.id === currentUser.id && user.userIsAdmin}
                    onChange={() => handleToggle(user)}
                    title={
                      user.id === currentUser.id
                        ? t("cannotRemoveOwnAdmin")
                        : ""
                    }
                  />
                  <span className="admin-permissions-slider"></span>
                </label>
              </li>
            ))}
          </ul>
        </div>
        <div className="admin-permissions-footer">
          <span>
            {t("activeAdmins")}: {users.filter((u) => u.userIsAdmin).length}
          </span>
          <button
            className="admin-permissions-close"
            onClick={onClose}
            style={{ fontSize: "1rem", padding: "6px 18px" }}
          >
            {t("close")}
          </button>
        </div>
        {confirmOpen && (
          <div className="admin-permissions-modal-backdrop">
            <div className="admin-permissions-modal">
              <div className="admin-permissions-modal-title">
                {t("confirmAction")}
              </div>
              <div>
                {pendingAdmin
                  ? t("confirmGrantAdmin", { name: selectedUser.name })
                  : t("confirmRevokeAdmin", { name: selectedUser.name })}
              </div>
              <div className="admin-permissions-modal-actions">
                <button
                  className="admin-permissions-modal-btn cancel"
                  onClick={() => setConfirmOpen(false)}
                >
                  {t("cancel")}
                </button>
                <button
                  className="admin-permissions-modal-btn confirm"
                  onClick={handleConfirm}
                >
                  {t("confirm")}
                </button>
              </div>
            </div>
          </div>
        )}
      </aside>
    </>
  );
};

export default AdminPermissionsOffcanvas;
