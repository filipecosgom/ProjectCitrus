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
      <SearchBar
        onSearch={setSearchingParameters}
        {...usersFilters}
      />
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
              <UserCard key={user.id} user={user} />
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
    </div>
  );
}
