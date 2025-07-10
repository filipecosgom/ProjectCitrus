import "./Users.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useRef } from "react";
import UserCard from "../../components/userCard/UserCard";
import { handleGetUsers } from "../../handles/handleGetUsers";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import { handleGetOffices } from "../../handles/handleGetEnums";
import SortControls from "../../components/sortControls/SortControls";
import { usersSortFields } from "../../utils/usersSearchUtils";
import { useTranslation } from "react-i18next";
import UserOffcanvas from "../../components/userOffcanvas/UserOffcanvas";
import { GrUserSettings } from "react-icons/gr";
import useAuthStore from "../../stores/useAuthStore";
import {
  buildSearchParams,
  createPageChangeHandler,
} from "../../utils/searchUtils";
import {
  fetchInitialUsers,
  userSearchFilters,
} from "../../utils/usersSearchUtils";
import AssignManagerOffcanvas from "../../components/assignManagerOffcanvas/AssignManagerOffcanvas";
import { handleAssignManager } from "../../handles/handleAssignManager";
import handleNotification from "../../handles/handleNotification";
import {
  handleGetUsersCSV,
  handleGetUsersXLSX,
} from "../../handles/handleGetUsers";
import { useSearchParams } from "react-router-dom";

export default function Users() {
  const { t } = useTranslation();
  const [urlSearchParams, setUrlSearchParams] = useSearchParams();
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

  // Estados para Assign Manager
  const [selectedUsers, setSelectedUsers] = useState(new Set());
  const [assignManagerOpen, setAssignManagerOpen] = useState(false);

  // Use null as initial value for searchParams
  const [searchParams, setSearchParams] = useState(null);
  const [lastSearch, setLastSearch] = useState(null);

  // ✅ ADICIONAR: Flag para evitar loops
  const [urlSyncEnabled, setUrlSyncEnabled] = useState(true);
  const [initialLoadComplete, setInitialLoadComplete] = useState(false);

  // ✅ CORRIGIDO: Ler sortBy e sortOrder da URL com valores corretos
  const [sort, setSort] = useState(() => {
    const sortBy = urlSearchParams.get("sortBy") || "name";
    const sortOrder = urlSearchParams.get("sortOrder") || "ASCENDING";
    return { sortBy, sortOrder };
  });

  const lastSearchRef = useRef(lastSearch);
  const usersFilters = userSearchFilters(t, offices);

  const isAdmin = useAuthStore((state) => state.user?.userIsAdmin);
  const containerRef = useRef();

  // ✅ CORRIGIDO: Handlers para user offcanvas
  const handleUserClick = (user) => {
    setSelectedUser(user);
    setOffcanvasOpen(true);

    const currentParams = new URLSearchParams(urlSearchParams);
    currentParams.set("user", user.id.toString());
    setUrlSearchParams(currentParams);
  };

  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);

    const currentParams = new URLSearchParams(urlSearchParams);
    currentParams.delete("user");
    setUrlSearchParams(currentParams);

    setTimeout(() => {
      setSelectedUser(null);
    }, 300);
  };

  // ✅ CORRIGIDO: useEffect APENAS para user ID (removido ordenação)
  useEffect(() => {
    // Ler user ID para offcanvas
    const userId = urlSearchParams.get("user");
    if (userId && users.length > 0) {
      const user = users.find((u) => u.id === parseInt(userId));
      if (user) {
        setSelectedUser(user);
        setOffcanvasOpen(true);
      }
    }
  }, [urlSearchParams, users]);

  // ✅ NOVO: useEffect para ler TODOS os parâmetros da URL APENAS na inicialização
  useEffect(() => {
    if (
      urlSearchParams.toString() &&
      !searchParams &&
      offices.length > 0 &&
      !initialLoadComplete
    ) {
      const query = urlSearchParams.get("query") || "";
      const searchType = urlSearchParams.get("searchType") || "email";
      const limit = parseInt(urlSearchParams.get("limit")) || 10;
      const accountState = urlSearchParams.get("accountState") || "";
      const office = urlSearchParams.get("office") || "";
      const isManager = urlSearchParams.get("isManager");
      const isAdmin = urlSearchParams.get("isAdmin");
      const isManaged = urlSearchParams.get("isManaged");

      // ✅ TAMBÉM ler ordenação na inicialização
      const sortBy = urlSearchParams.get("sortBy") || "name";
      const sortOrder = urlSearchParams.get("sortOrder") || "ASCENDING";

      const filters = {
        accountState,
        office,
        isManager:
          isManager === "true" ? true : isManager === "false" ? false : null,
        isAdmin: isAdmin === "true" ? true : isAdmin === "false" ? false : null,
        isManaged:
          isManaged === "true" ? true : isManaged === "false" ? false : null,
      };

      // ✅ DESABILITAR sincronização URL temporariamente
      setUrlSyncEnabled(false);

      // ✅ Atualizar sort se necessário
      setSort({ sortBy, sortOrder });

      const search = buildSearchParams(query, searchType, limit, filters);
      setSearchParams(search);
      setLastSearch(search);

      // ✅ Marcar carregamento inicial como completo
      setInitialLoadComplete(true);

      // ✅ REABILITAR sincronização URL
      setTimeout(() => setUrlSyncEnabled(true), 100);
    }
  }, [urlSearchParams, searchParams, offices, initialLoadComplete]);

  // ✅ ADICIONAR: useEffect que garante parâmetros padrão na URL
  useEffect(() => {
    if (offices.length > 0 && !initialLoadComplete) {
      const currentParams = new URLSearchParams(urlSearchParams);

      // Garantir que sortBy e sortOrder estejam sempre na URL
      if (!currentParams.has("sortBy")) {
        currentParams.set("sortBy", "name");
      }
      if (!currentParams.has("sortOrder")) {
        currentParams.set("sortOrder", "ASCENDING");
      }

      // Se algo foi adicionado, atualizar URL
      if (!urlSearchParams.has("sortBy") || !urlSearchParams.has("sortOrder")) {
        setUrlSearchParams(currentParams);
      }

      setInitialLoadComplete(true);
    }
  }, [offices, urlSearchParams, setUrlSearchParams, initialLoadComplete]);

  // Handlers para Assign Manager
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

  useEffect(() => {
    setSelectedUsers(new Set());
  }, [pagination.offset]);

  // ✅ CORRIGIDO: set searching parameters - só atualiza URL se habilitado
  const setSearchingParameters = async (
    query,
    searchType,
    limit,
    filters = {}
  ) => {
    const search = buildSearchParams(query, searchType, limit, filters);
    setLastSearch(search);
    setSearchParams(search);

    // ✅ SÓ ATUALIZAR URL se sincronização estiver habilitada
    if (urlSyncEnabled) {
      // Atualizar URL com parâmetros de pesquisa
      const currentParams = new URLSearchParams(urlSearchParams);

      // Limpar parâmetros de pesquisa antigos
      const searchKeys = [
        "query",
        "searchType",
        "limit",
        "accountState",
        "office",
        "isManager",
        "isAdmin",
        "isManaged",
      ];
      searchKeys.forEach((key) => currentParams.delete(key));

      // Adicionar novos parâmetros (apenas se não forem valores padrão)
      if (query && query.trim() !== "") {
        currentParams.set("query", query);
      }
      if (searchType && searchType !== "email") {
        currentParams.set("searchType", searchType);
      }
      if (limit && limit !== 10) {
        currentParams.set("limit", limit.toString());
      }

      // Adicionar filtros (apenas se não forem valores padrão)
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== "") {
          currentParams.set(key, value.toString());
        }
      });

      setUrlSearchParams(currentParams);
    }

    // Reset pagination quando filtros mudam
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  // Externalized: handle page change
  const handlePageChange = createPageChangeHandler(setPagination);

  // ✅ CORRIGIDO: handle sort change - não depende de useEffect
  const handleSortChange = ({ sortBy, sortOrder }) => {
    // ✅ Atualizar estado primeiro
    setSort({ sortBy, sortOrder });

    // ✅ Atualizar URL se habilitado
    if (urlSyncEnabled) {
      const currentParams = new URLSearchParams(urlSearchParams);
      currentParams.set("sortBy", sortBy);
      currentParams.set("sortOrder", sortOrder);
      setUrlSearchParams(currentParams);
    }

    // Reset pagination when sort changes
    setPagination((prev) => ({ ...prev, offset: 0 }));

    // ✅ IMPORTANTE: NÃO chamar fetchUsers aqui - deixa o useEffect fazer isso
  };

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

  useEffect(() => {
    fetchInitialUsers({
      setPageLoading,
      setOffices,
      setSearchParams,
      handleGetOffices,
    });
  }, []);

  if (pageLoading) return <Spinner />;

  // Handler para atribuir manager
  const handleAssignManagerAction = async (assignments) => {
    console.log("🔧 Assigning manager:", assignments);
    try {
      setResultsLoading(true);

      const result = await handleAssignManager({
        newManagerId: assignments.newManagerId,
        userIds: assignments.userIds,
      });

      if (!result.data) {
        console.error("❌ Users.js - result.data is undefined!");
        alert(
          `❌ Error: Response structure is invalid. Check console for details.`
        );
        return;
      }

      if (result.success) {
        const totalSuccessful = result.data.totalSuccessful || 0;
        handleNotification(
          "success",
          t("users.managerAssignSuccess", {
            managerName: assignments.newManagerName,
            total: totalSuccessful,
          })
        );
      } else {
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
    let params = {
      ...searchParams,
      parameter: sort.sortBy,
      order: sort.sortOrder,
    };
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
        new Blob([result.blob], {
          type:
            result.contentType ||
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        })
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
    <div className="users-container" ref={containerRef}>
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
                showCheckbox={isAdmin}
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

      <UserOffcanvas
        user={selectedUser}
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas}
      />

      <AssignManagerOffcanvas
        selectedUserIds={Array.from(selectedUsers)}
        selectedUsers={users.filter((user) => selectedUsers.has(user.id))}
        isOpen={assignManagerOpen}
        onClose={() => {
          handleCloseAssignManager();
        }}
        onAssign={handleAssignManagerAction}
      />
    </div>
  );
}
