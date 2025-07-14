/**
 * @file AdminPermissionsOffcanvas.js
 * @module AdminPermissionsOffcanvas
 * @description Offcanvas panel for managing admin permissions for users.
 * @author Project Citrus Team
 */

import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { fetchAllUsers, updateAdminPermission } from "../../api/userApi";
import useAuthStore from "../../stores/useAuthStore";
import UserSearchBar from "../userSearchBar/UserSearchBar";
import "./AdminPermissionsOffcanvas.css";

/**
 * AdminPermissionsOffcanvas component for managing admin permissions.
 * @param {Object} props - Component props
 * @param {boolean} props.show - Whether the offcanvas is visible
 * @param {Function} props.onClose - Callback to close the offcanvas
 * @returns {JSX.Element}
 */
const AdminPermissionsOffcanvas = ({ show, onClose }) => {
  const { t } = useTranslation();
  const [users, setUsers] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [pendingAdmin, setPendingAdmin] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const currentUser = useAuthStore((state) => state.user);

  /**
   * Fetches all users when the offcanvas is shown.
   */
  useEffect(() => {
    if (show) {
      setLoading(true);
      fetchAllUsers()
        .then((data) => setUsers(data))
        .catch(() => setError(t("errorLoadingUsers")))
        .finally(() => setLoading(false));
    }
  }, [show, t]);

  /**
   * Filters users based on the search term.
   * @type {Array<Object>}
   */
  const filteredUsers = users.filter((u) => {
    if (!searchTerm.trim()) return true;
    const term = searchTerm.trim().toLowerCase();
    return (
      u.name?.toLowerCase().includes(term) ||
      u.surname?.toLowerCase().includes(term) ||
      u.email?.toLowerCase().includes(term)
    );
  });

  /**
   * Handles search term change.
   * @param {string} term - The new search term
   */
  const handleSearchChange = (term) => {
    setSearchTerm(term);
  };

  /**
   * Handles toggling admin permission for a user.
   * Prevents self-removal of admin.
   * @param {Object} user - The user object
   */
  const handleToggle = (user) => {
    if (user.id === currentUser.id && user.userIsAdmin) {
      setError(t("cannotRemoveOwnAdmin"));
      return;
    }
    setSelectedUser(user);
    setPendingAdmin(!user.userIsAdmin);
    setConfirmOpen(true);
  };

  /**
   * Handles confirmation of admin permission change.
   * Updates the user's admin status.
   */
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
      {/* Offcanvas backdrop */}
      <div
        className={`admin-permissions-backdrop${show ? " open" : ""}`}
        onClick={onClose}
      />
      {/* Offcanvas panel */}
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

          {/* User search bar */}
          <div style={{ marginBottom: "16px" }}>
            <UserSearchBar
              placeholder={t("searchBarPlaceholder", {
                type: t("searchByName"),
              })}
              maxResults={30}
              showUserInfo={true}
              compact={true}
              className="admin-permissions-search"
              onSearch={handleSearchChange}
              onUserSelect={() => {}}
              disableDropdown={true}
            />
          </div>

          {/* User list */}
          <ul className="admin-permissions-list">
            {filteredUsers.map((user) => (
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
        {/* Confirmation modal */}
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
                  {t("cancel").charAt(0).toUpperCase() + t("cancel").slice(1)}
                </button>
                <button
                  className="admin-permissions-modal-btn confirm"
                  onClick={handleConfirm}
                >
                  {t("confirm").charAt(0).toUpperCase() + t("confirm").slice(1)}
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
