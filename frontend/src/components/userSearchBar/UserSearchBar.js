import React, { useState, useEffect, useCallback } from "react";
import { FaSearch } from "react-icons/fa";
import { handleGetUsers } from "../../handles/handleGetUsers";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import UserIcon from "../userIcon/UserIcon";
import Spinner from "../spinner/spinner";
import "./UserSearchBar.css";

const UserSearchBar = ({
  selectedUser = null,
  onUserSelect,
  placeholder = "Search for users...",
  maxResults = 50,
  showUserInfo = true,
  compact = false,
  className = "",
  excludeUserIds = [],
  filterOptions = {},
}) => {
  const [users, setUsers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [userAvatars, setUserAvatars] = useState({});
  const [isOpen, setIsOpen] = useState(false);

  // ✅ DEBOUNCED SEARCH - busca após 300ms de pausa
  const debouncedSearch = useCallback(
    debounce((query) => {
      searchUsers(query);
    }, 300),
    [excludeUserIds, maxResults, filterOptions]
  );

  // ✅ BUSCAR USERS - MESMA LÓGICA que Users.js
  const searchUsers = async (query = "") => {
    setUsersLoading(true);

    try {
      const searchParams = {
        name: query,
        offset: 0,
        limit: maxResults,
        parameter: "name",
        order: "ascending",
        ...filterOptions,
      };

      const result = await handleGetUsers(searchParams);

      if (result && result.users) {
        const filteredUsers = result.users.filter(
          (user) => !excludeUserIds.includes(user.id)
        );

        setUsers(filteredUsers);
        loadUserAvatars(filteredUsers);
      } else {
        setUsers([]);
      }
    } catch (error) {
      setUsers([]);
    } finally {
      setUsersLoading(false);
    }
  };

  // ✅ CARREGAR AVATARS dos users (mantém igual)
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
    setIsOpen(query.length > 0);

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

  // ✅ HANDLER para blur
  const handleBlur = () => {
    setTimeout(() => {
      setIsOpen(false);
    }, 200);
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
                  onMouseDown={() => handleUserSelect(user)}
                  className={`user-search-item ${
                    selectedUser?.id === user.id ? "selected" : ""
                  }`}
                >
                  {/* ✅ AVATAR CORRIGIDO COM UserIcon */}
                  <div className="user-avatar">
                    <UserIcon
                      user={{
                        id: user.id,
                        name: user.name,
                        surname: user.surname,
                        hasAvatar: user.hasAvatar || user.avatar, // Compatibilidade
                        onlineStatus: user.onlineStatus || false,
                      }}
                      avatar={userAvatars[user.id]} // Manter para cache
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
