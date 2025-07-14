/**
 * @file UserSearchBar.js
 * @module UserSearchBar
 * @description Search bar component for searching and selecting users.
 * Supports dropdown results, avatars, admin/manager badges, and integration with external filtering.
 * @author Project Citrus Team
 */

import React, { useState, useEffect, useCallback } from "react";
import { FaSearch } from "react-icons/fa";
import { handleGetUsers } from "../../handles/handleGetUsers";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import UserIcon from "../userIcon/UserIcon";
import Spinner from "../spinner/Spinner";
import "./UserSearchBar.css";

/**
 * UserSearchBar component for searching and selecting users.
 * @param {Object} props - Component props
 * @param {Object|null} [props.selectedUser=null] - Currently selected user object
 * @param {Function} props.onUserSelect - Callback when a user is selected
 * @param {Function} [props.onSearch] - Callback for search input changes (for external filtering)
 * @param {string} [props.placeholder="Search for users..."] - Input placeholder text
 * @param {number} [props.maxResults=50] - Maximum number of results to show
 * @param {boolean} [props.showUserInfo=true] - Whether to show user role/office info
 * @param {boolean} [props.compact=false] - Compact mode for styling
 * @param {string} [props.className=""] - Additional CSS class
 * @param {Array<number>} [props.excludeUserIds=[]] - List of user IDs to exclude from results
 * @param {Object} [props.filterOptions={}] - Additional filter options for search
 * @param {boolean} [props.disableDropdown=false] - If true, disables dropdown and only triggers onSearch
 * @returns {JSX.Element} The rendered user search bar
 */
const UserSearchBar = ({
  selectedUser = null,
  onUserSelect,
  onSearch,
  placeholder = "Search for users...",
  maxResults = 50,
  showUserInfo = true,
  compact = false,
  className = "",
  excludeUserIds = [],
  filterOptions = {},
  disableDropdown = false,
}) => {
  const [users, setUsers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [userAvatars, setUserAvatars] = useState({});
  const [isOpen, setIsOpen] = useState(false);

  /**
   * Debounced search function to avoid excessive backend calls.
   * @type {Function}
   */
  const debouncedSearch = useCallback(
    debounce((query) => {
      searchUsers(query);
    }, 300),
    [excludeUserIds, maxResults, filterOptions]
  );

  /**
   * Fetches users from backend based on search query and filter options.
   * @param {string} [query=""] - Search query
   */
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

  /**
   * Loads avatars for users who have avatars.
   * @param {Array<Object>} usersList - List of user objects
   */
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

  /**
   * Handles changes in the search input field.
   * Triggers external onSearch if dropdown is disabled.
   * @param {React.ChangeEvent<HTMLInputElement>} e - Change event
   */
  const handleSearchChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);

    if (disableDropdown && onSearch) {
      onSearch(query);
      return;
    }

    setIsOpen(query.length > 0);

    if (query.trim()) {
      debouncedSearch(query);
    } else {
      setUsers([]);
    }
  };

  /**
   * Handles user selection from the dropdown.
   * @param {Object} user - Selected user object
   */
  const handleUserSelect = (user) => {
    setSearchQuery(`${user.name} ${user.surname}`);
    setIsOpen(false);
    onUserSelect(user);
  };

  /**
   * Clears the search input and results.
   */
  const handleClear = () => {
    setSearchQuery("");
    setUsers([]);
    setIsOpen(false);
    onUserSelect(null);
    if (disableDropdown && onSearch) {
      onSearch("");
    }
  };

  /**
   * Handles input focus to open dropdown if results exist.
   */
  const handleFocus = () => {
    if (searchQuery && users.length > 0) {
      setIsOpen(true);
    }
  };

  /**
   * Handles input blur to close dropdown after a short delay.
   */
  const handleBlur = () => {
    setTimeout(() => {
      setIsOpen(false);
    }, 200);
  };

  return (
    <div className={`user-search-bar ${compact ? "compact" : ""} ${className}`}>
      {/* Search Input */}
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

      {/* Dropdown List */}
      {isOpen && !disableDropdown && (
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
                  {/* Avatar */}
                  <div className="user-avatar">
                    <UserIcon
                      user={{
                        id: user.id,
                        name: user.name,
                        surname: user.surname,
                        hasAvatar: user.hasAvatar || user.avatar,
                        onlineStatus: user.onlineStatus || false,
                      }}
                      avatar={userAvatars[user.id]}
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

/**
 * Utility: Debounce function to delay execution.
 * @param {Function} func - Function to debounce
 * @param {number} wait - Delay in milliseconds
 * @returns {Function} Debounced function
 */
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
