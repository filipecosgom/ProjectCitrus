import "./Users.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useRef } from "react";
import UserCard from "../../components/userCard/UserCard";
import { handleGetUsers } from "../../handles/handleGetUsers";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import { handleGetOffices } from "../../handles/handleGetEnums";
import SortControls from "../../components/sortControls/SortControls"; // ✅ CORRIGIDO
import { usersSortFields } from "../../utils/usersSearchUtils"; // ✅ CORRIGIDO
import { useTranslation } from "react-i18next";
import UserOffcanvas from "../../components/userOffcanvas/UserOffcanvas";
import { GrUserSettings } from "react-icons/gr";
import useAuthStore from "../../stores/useAuthStore";
import {
  buildSearchParams,
  createPageChangeHandler,
  createSortHandler,
  fetchInitialUsers,
  userSearchFilters,
} from "../../utils/usersSearchUtils"; // ✅ CORRIGIDO
import AssignManagerOffcanvas from "../../components/assignManagerOffcanvas/AssignManagerOffcanvas";
import { handleAssignManager } from "../../handles/handleAssignManager"; // ✅ IMPORT
import handleNotification from "../../handles/handleNotification";
import { handleGetUsersCSV, handleGetUsersXLSX } from "../../handles/handleGetUsers";

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

  // Estados para user offcanvas
  const [selectedUser, setSelectedUser] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);

  // ✅ NOVOS ESTADOS para Assign Manager
  const [selectedUsers, setSelectedUsers] = useState(new Set());
  const [assignManagerOpen, setAssignManagerOpen] = useState(false); // ✅ NOVO

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

  // ✅ VERIFICAR se user é admin
  const isAdmin = useAuthStore((state) => state.user?.userIsAdmin);

  // Handlers para user offcanvas
  const handleUserClick = (user) => {
    setSelectedUser(user);
    setOffcanvasOpen(true);
  };

  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);
    setTimeout(() => {
      setSelectedUser(null);
    }, 300);
  };

  // ✅ NOVOS HANDLERS para Assign Manager
  const handleOpenAssignManager = () => {
    if (selectedUsers.size === 0) {
      handleNotification("info", t("users.noUsersSelected"));
      return;
    }
    setAssignManagerOpen(true);
  };

  const handleCloseAssignManager = () => {
    setAssignManagerOpen(false);
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

  // ✅ LIMPAR seleções quando mudar de página
  useEffect(() => {
    setSelectedUsers(new Set());
  }, [pagination.offset]);

  // Externalized: set searching parameters
  const setSearchingParameters = async (
    query,
    searchType,
    limit,
    filters = {}
  ) => {
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

  // ✅ HANDLER para atribuir manager COM DEBUGGING
  const handleAssignManagerAction = async (assignments) => {
    console.log("🔧 Assigning manager:", assignments);
    try {
      setResultsLoading(true);

      const result = await handleAssignManager({
        newManagerId: assignments.newManagerId,
        userIds: assignments.userIds,
      });

      // ✅ VERIFICAR se result.data existe antes de acessar propriedades
      if (!result.data) {
        console.error("❌ Users.js - result.data is undefined!");
        alert(
          `❌ Error: Response structure is invalid. Check console for details.`
        );
        return;
      }

      if (result.success) {
        // ✅ FEEDBACK SUCCESS - com verificação segura
        const totalSuccessful = result.data.totalSuccessful || 0;
        handleNotification(
          "success",
          t("users.managerAssignSuccess", {
            managerName: assignments.newManagerName,
            total: totalSuccessful,
          })
        );
      } else {

        // ✅ FEEDBACK PARCIAL - com verificação segura
        const totalSuccessful = result.data.totalSuccessful || 0;
        const totalFailed = result.data.totalFailed || 0;

        if (totalSuccessful > 0) {
          alert(
            `⚠️ Partial Success!\n${assignments.newManagerName} assigned to ${totalSuccessful} user(s)\n${totalFailed} assignment(s) failed`
          );
        } else {
          alert(`❌ Error: ${result.message}`);
        }
      }

      // ✅ REFRESH da lista sempre (mesmo com falhas parciais)
      await fetchUsers(pagination.offset);
      setSelectedUsers(new Set());
    } catch (error) {
      console.error("❌ Users.js - Error in assign manager:", error);
      console.error("❌ Users.js - Error details:", {
        message: error.message,
        stack: error.stack,
        error: error,
      });
      alert(`❌ Unexpected error: ${error.message}`);
    } finally {
      setResultsLoading(false);
      handleCloseAssignManager();
    }
  };

  // CSV export handler
  const handleGetCSV = async () => {
    // Build params from current search and sort
    let params = {
      ...searchParams,
      parameter: sort.sortBy,
      order: sort.sortOrder,
      // Optionally, add language param here if needed (e.g., language: i18n.language)
    };
    // Remove pagination params
    delete params.limit;
    delete params.offset;
    const result = await handleGetUsersCSV(params);
    if (result.success) {
      const url = window.URL.createObjectURL(
        new Blob([result.blob], { type: result.contentType || "text/csv" })
      );
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "users.csv");
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } else {
      handleNotification("error", "usersCsvExportError");
    }
  };

  // XLSX export handler
  const handleGetXLSX = async () => {
    let params = {
      ...searchParams,
      parameter: sort.sortBy,
      order: sort.sortOrder,
    };
    delete params.limit;
    delete params.offset;
    const result = await handleGetUsersXLSX(params);
    if (result.success) {
      const url = window.URL.createObjectURL(
        new Blob([result.blob], { type: result.contentType || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
      );
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "users.xlsx");
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } else {
      handleNotification("error", "usersXlsxExportError");
    }
  };

  return (
    <div className="users-container">
      {/* ✅ NOVA ESTRUTURA com SearchBar e botão Assign Managers */}
      <div className="searchBar-containerAndButton">
        <SearchBar
          onSearch={setSearchingParameters}
          {...usersFilters}
          onExportCsv={handleGetCSV}
          onExportXlsx={handleGetXLSX}
          actions={
            isAdmin && (
              <button
                className={`assign-managers-btn ${
                  selectedUsers.size === 0 ? "disabled" : ""
                }`}
                onClick={handleOpenAssignManager}
                disabled={selectedUsers.size === 0}
              >
                <GrUserSettings className="assign-managers-icon" />
                <span className="assign-managers-text">
                  {t("users.assignManagers")}
                </span>
                <span className="assign-managers-count">
                  {selectedUsers.size > 0 && ` (${selectedUsers.size})`}
                </span>
              </button>
            )
          }
        />
      </div>

      <SortControls
        className="users-mode"
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
          <p>{t("users.noResults")}</p>
        </div>
      ) : (
        <div>
          <div className="users-grid">
            {users.map((user) => (
              <UserCard
                key={user.id}
                user={user}
                onClick={handleUserClick}
                showCheckbox={isAdmin} // ✅ NOVA PROP: sempre visível para admins
                isSelected={selectedUsers.has(user.id)}
                onSelectionChange={handleUserSelection}
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

      {/* ✅ USER OFFCANVAS */}
      <UserOffcanvas
        user={selectedUser}
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas}
      />

      {/* ✅ NOVO: ASSIGN MANAGER OFFCANVAS */}
      <AssignManagerOffcanvas
        selectedUserIds={Array.from(selectedUsers)}
        selectedUsers={users.filter((user) => selectedUsers.has(user.id))} // ✅ Users selecionados
        isOpen={assignManagerOpen}
        onClose={() => {
          handleCloseAssignManager();
        }}
        onAssign={handleAssignManagerAction} // ✅ FUNÇÃO REAL ao invés de log
      />
    </div>
  );
}
