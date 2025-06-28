import "./Users.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useRef } from "react";
import UserCard from "../../components/userCard/UserCard";
import { handleGetUsers } from "../../handles/handleGetUsers";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import { handleGetOffices } from "../../handles/handleGetEnums";
import UserSortControls from "../../components/userSortControls/UserSortControls";
import { useTranslation } from "react-i18next";
import {
  buildSearchParams,
  createPageChangeHandler,
  createSortHandler,
  fetchInitialUsers,
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
  const [searchParams, setSearchParams] = useState({
    query: "",
    searchType: "email",
    limit: 10,
    filters: {},
  });
  const [lastSearch, setLastSearch] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [sort, setSort] = useState({
    sortBy: "name",
    sortOrder: "ascending",
  });
  const lastSearchRef = useRef(lastSearch);
  useEffect(() => {
    lastSearchRef.current = lastSearch;
  }, [lastSearch]);

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
  }

  useEffect(() => {
    if (searchParams.query !== undefined) {
      fetchUsers(0, searchParams);
    }
  }, [searchParams]);

  useEffect(() => {
    if (lastSearch) {
      fetchUsers(pagination.offset, lastSearch);
    }
  }, [pagination.offset]);

  useEffect(() => {
    if (lastSearch) {
      fetchUsers(0, lastSearch);
      setPagination((prev) => ({ ...prev, offset: 0 }));
    }
  }, [sort]);

  // 📦 On mount: fetch offices and initial users
  useEffect(() => {
    fetchInitialUsers({
      setPageLoading,
      setOffices,
      setSearchParams,
      setLastSearch,
      setUsers,
      setPagination,
      sort,
      handleGetOffices,
      handleGetUsers,
    });
  }, []);

  if (pageLoading) return <Spinner />;

  return (
    <div className="users-container">
      <SearchBar onSearch={setSearchingParameters} offices={offices} />
      <UserSortControls
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
