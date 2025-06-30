import "./Users.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useRef } from "react";
import UserCard from "../../components/userCard/UserCard";
import { handleGetUsers } from "../../handles/handleGetUsers";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import { handleGetOffices } from "../../handles/handleGetEnums";
import SortControls from "../../components/sortControls/SortControls";
import { useTranslation } from "react-i18next";
import UserOffcanvas from "../../components/userOffcanvas/UserOffcanvas";
import { GrUserSettings } from "react-icons/gr"; // ✅ ADICIONAR IMPORT
import useAuthStore from "../../stores/useAuthStore"; // ✅ ADICIONAR IMPORT
import {
  buildSearchParams,
  createPageChangeHandler,
  createSortHandler,
  fetchInitialUsers,
  userSearchFilters,
  usersSortFields as sortFields,
} from "../../utils/usersSearchUtils";

export default function Users() {
  const { t } = useTranslation();
  const [users, setUsers] = useState([]);
  const [offices, setOffices] = useState([]);
  const [resultsLoading, setResultsLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 10,
    total: 0,
  });

  // Estados para offcanvas
  const [selectedUser, setSelectedUser] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);

  // ✅ NOVOS ESTADOS para Assign Managers
  const [isSelectionMode, setIsSelectionMode] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState(new Set());

  // Use null as initial value for searchParams
  const [searchParams, setSearchParams] = useState(null);
  const [lastSearch, setLastSearch] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [sort, setSort] = useState({
    sortBy: "name",
    sortOrder: "ascending",
  });
  const lastSearchRef = useRef(lastSearch);
  const usersFilters = userSearchFilters(t, offices);
  const usersSortFields = sortFields;

  // ✅ VERIFICAR se user é admin
  const isAdmin = useAuthStore((state) => state.user?.userIsAdmin);

  // Handlers para offcanvas
  const handleUserClick = (user) => {
    // ✅ Se está em selection mode, não abrir offcanvas
    if (isSelectionMode) return;

    console.log("🔍 USERS - User clicked:", user);
    setSelectedUser(user);
    setOffcanvasOpen(true);
  };

  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);
    setTimeout(() => {
      setSelectedUser(null);
    }, 300);
  };

  // ✅ NOVOS HANDLERS para Selection Mode
  const handleToggleSelectionMode = () => {
    setIsSelectionMode(!isSelectionMode);
    if (isSelectionMode) {
      // Se está desativando, limpar seleções
      setSelectedUsers(new Set());
    }
  };

  const handleUserSelection = (userId, isSelected) => {
    const newSelectedUsers = new Set(selectedUsers);
    if (isSelected) {
      newSelectedUsers.add(userId);
    } else {
      newSelectedUsers.delete(userId);
    }
    setSelectedUsers(newSelectedUsers);
  };

  // Externalized: set searching parameters
  const setSearchingParameters = async (
    query,
    searchType,
    limit,
    filters = {}
  ) => {
    // Validate inputs
    const search = buildSearchParams(query, searchType, limit, filters);
    setLastSearch(search);
    setCurrentPage(1);
    setSearchParams(search);
  };

  // Externalized: handle page change
  const handlePageChange = createPageChangeHandler(
    setPagination,
    fetchUsers,
    lastSearchRef
  );

  // Externalized: handle sort change
  const handleSortChange = createSortHandler(
    setSort,
    setPagination,
    fetchUsers,
    lastSearchRef
  );

  // Externalized: fetch users
  async function fetchUsers(offset = 0, overrideParams = null) {
    const { query, searchType, limit, filters } =
      overrideParams || searchParams;
    setResultsLoading(true);
    const result = await handleGetUsers({
      [searchType]: query,
      offset,
      limit,
      ...filters,
      parameter: sort.sortBy,
      order: sort.sortOrder,
    });
    setUsers(result.users);
    setPagination((prev) => ({
      ...prev,
      offset,
      limit: result.pagination.limit,
      total: result.pagination.totalUsers,
    }));
    setResultsLoading(false);
    setLastSearch(overrideParams || searchParams);
  }

  // Only one effect for fetching users
  useEffect(() => {
    if (searchParams) {
      fetchUsers(pagination.offset, searchParams);
    }
    // eslint-disable-next-line
  }, [searchParams, pagination.offset, sort]);

  // 📦 On mount: fetch offices and initial users
  useEffect(() => {
    fetchInitialUsers({
      setPageLoading,
      setOffices,
      setSearchParams,
      handleGetOffices,
    });
  }, []);

  if (pageLoading) return <Spinner />;

  return (
    <div className="users-container">
      {/* ✅ NOVA ESTRUTURA com SearchBar e botão Assign Managers */}
      <div className="searchBar-container">
        <div
          className={`searchBar-wrapper ${isAdmin ? "with-assign-button" : ""}`}
        >
          <SearchBar onSearch={setSearchingParameters} {...usersFilters} />
        </div>

        {/* ✅ BOTÃO Assign Managers - apenas para admins */}
        {isAdmin && (
          <button
            className={`assign-managers-btn ${isSelectionMode ? "active" : ""}`}
            onClick={handleToggleSelectionMode}
          >
            <GrUserSettings className="assign-managers-icon" />
            <span className="assign-managers-text">
              {isSelectionMode ? "Cancel Selection" : "Assign Managers"}
            </span>
          </button>
        )}
      </div>

      <SortControls
        fields={usersSortFields}
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={handleSortChange}
      />
      {resultsLoading ? (
        <div className="users-loading">
          <Spinner />
        </div>
      ) : users.length === 0 ? (
        <div className="users-empty">
          <p>{t("usersNoResults")}</p>
        </div>
      ) : (
        <div>
          <div className="users-grid">
            {users.map((user) => (
              <UserCard
                key={user.id}
                user={user}
                onClick={handleUserClick}
                isSelectionMode={isSelectionMode} // ✅ NOVA PROP
                isSelected={selectedUsers.has(user.id)} // ✅ NOVA PROP
                onSelectionChange={handleUserSelection} // ✅ NOVA PROP
              />
            ))}
          </div>
        </div>
      )}
      <Pagination
        offset={pagination.offset}
        limit={pagination.limit}
        total={pagination.total}
        onChange={handlePageChange}
      />

      <UserOffcanvas
        user={selectedUser}
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas}
      />
    </div>
  );
}
