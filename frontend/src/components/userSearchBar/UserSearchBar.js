import React, { useState, useEffect, useCallback } from "react";
import { FaSearch } from "react-icons/fa";
import { handleGetUsers } from "../../handles/handleGetUsers";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import UserIcon from "../userIcon/UserIcon";
import Spinner from "../spinner/spinner";
import "./UserSearchBar.css"; // ✅ CORRETO: próprio CSS

const UserSearchBar = ({
  selectedUser = null,
  onUserSelect,
  placeholder = "Search for users...",
  maxResults = 50,
  showUserInfo = true, // Mostrar role/office
  compact = false, // Versão compacta
  className = "",
  excludeUserIds = [], // Excluir IDs específicos
  filterOptions = {}, // Filtros adicionais (office, role, etc)
}) => {
  const [users, setUsers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [userAvatars, setUserAvatars] = useState({});
  const [isOpen, setIsOpen] = useState(false); // Controlar dropdown

  // ✅ DEBOUNCED SEARCH - busca após 300ms de pausa
  const debouncedSearch = useCallback(
    debounce((query) => {
      searchUsers(query);
    }, 300),
    [excludeUserIds, maxResults, filterOptions]
  );

  // ✅ BUSCAR USERS na API
  const searchUsers = async (query = "") => {
    console.log("🔍 UserSearchBar - Searching with query:", query);
    setUsersLoading(true);

    try {
      const searchParams = {
        name: query, // Buscar por nome
        limit: maxResults,
        offset: 0,
        ...filterOptions, // Aplicar filtros adicionais
      };

      const result = await handleGetUsers(searchParams);

      // ✅ Filtrar IDs excluídos
      const filteredUsers = (result.users || []).filter(
        (user) => !excludeUserIds.includes(user.id)
      );

      console.log("👥 UserSearchBar - Users found:", filteredUsers.length);
      setUsers(filteredUsers);

      // ✅ Carregar avatars
      loadUserAvatars(filteredUsers);
    } catch (error) {
      console.error("❌ UserSearchBar - Error fetching users:", error);
      setUsers([]);
    } finally {
      setUsersLoading(false);
    }
  };

  // ✅ CARREGAR AVATARS dos users
  const loadUserAvatars = async (usersList) => {
    const avatarPromises = usersList
      .filter((user) => user.hasAvatar)
      .map(async (user) => {
        try {
          const result = await handleGetUserAvatar(user.id);
          if (result.success && result.avatar) {
            return { id: user.id, avatar: result.avatar };
          }
        } catch (error) {
          console.error(
            `UserSearchBar - Error loading avatar for ${user.id}:`,
            error
          );
        }
        return { id: user.id, avatar: null };
      });

    const avatarResults = await Promise.all(avatarPromises);
    const avatarsMap = {};
    avatarResults.forEach(({ id, avatar }) => {
      avatarsMap[id] = avatar;
    });

    setUserAvatars((prev) => ({ ...prev, ...avatarsMap }));
  };

  // ✅ HANDLER para mudança na search box
  const handleSearchChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    setIsOpen(query.length > 0); // Abrir dropdown quando há query

    if (query.trim()) {
      debouncedSearch(query);
    } else {
      setUsers([]);
    }
  };

  // ✅ HANDLER para selecionar user
  const handleUserSelect = (user) => {
    setSearchQuery(`${user.name} ${user.surname}`);
    setIsOpen(false);
    onUserSelect(user);
    console.log("👤 UserSearchBar - User selected:", user);
  };

  // ✅ HANDLER para limpar
  const handleClear = () => {
    setSearchQuery("");
    setUsers([]);
    setIsOpen(false);
    onUserSelect(null);
  };

  // ✅ HANDLER para focus
  const handleFocus = () => {
    if (searchQuery && users.length > 0) {
      setIsOpen(true);
    }
  };

  // ✅ HANDLER para blur (com delay para permitir cliques)
  const handleBlur = () => {
    setTimeout(() => {
      setIsOpen(false);
    }, 200);
  };

  // ✅ DETERMINAR status icon para UserIcon
  const getUserStatus = (user) => {
    if (user.userIsAdmin) return "check";
    if (user.userIsDeleted) return "cross";
    if (user.accountState === "INCOMPLETE") return "stroke";
    return null;
  };

  return (
    <div className={`user-search-bar ${compact ? "compact" : ""} ${className}`}>
      {/* ✅ SEARCH INPUT */}
      <div className="user-search-input-wrapper">
        <FaSearch className="search-icon" />
        <input
          type="text"
          placeholder={placeholder}
          value={searchQuery}
          onChange={handleSearchChange}
          onFocus={handleFocus}
          onBlur={handleBlur}
          className="user-search-input"
        />
        {searchQuery && (
          <button
            onClick={handleClear}
            className="user-search-clear"
            title="Clear search"
          >
            ✕
          </button>
        )}
      </div>

      {/* ✅ DROPDOWN LIST */}
      {isOpen && (
        <div className="user-search-dropdown">
          {usersLoading ? (
            <div className="user-search-loading">
              <Spinner size="small" />
              <span>Searching users...</span>
            </div>
          ) : users.length === 0 ? (
            <div className="user-search-empty">
              <p>
                {searchQuery
                  ? `No users found for "${searchQuery}"`
                  : "No users available"}
              </p>
            </div>
          ) : (
            <div className="user-search-results">
              {users.map((user) => (
                <button
                  key={user.id}
                  onClick={() => handleUserSelect(user)}
                  className={`user-search-item ${
                    selectedUser?.id === user.id ? "selected" : ""
                  }`}
                >
                  <div className="user-avatar">
                    <UserIcon
                      avatar={userAvatars[user.id]}
                      status={getUserStatus(user)}
                    />
                  </div>
                  <div className="user-info">
                    <span className="user-name">
                      {user.name} {user.surname}
                    </span>
                    <span className="user-email">{user.email}</span>
                    {showUserInfo && (
                      <div className="user-details">
                        <span className="user-role">
                          {user.role?.replace(/_/g, " ") || "N/A"}
                        </span>
                        <span className="user-office">
                          {user.office?.replace(/_/g, " ") || "N/A"}
                        </span>
                      </div>
                    )}
                  </div>
                  <div className="user-badges">
                    {user.userIsAdmin && (
                      <span className="admin-badge">Admin</span>
                    )}
                    {user.isManager && (
                      <span className="manager-badge">Manager</span>
                    )}
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

// ✅ UTILITY: Debounce function
function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

export default UserSearchBar;
