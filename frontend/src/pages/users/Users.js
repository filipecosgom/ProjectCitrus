import "./Users.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useCallback } from "react";
import "./Users.css";
import UserCard from "../../components/userCard/UserCard";
import { handleGetUsers } from "../../handles/handleGetUsers";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import { handleGetOffices } from "../../handles/handleGetEnums";
import UserSortControls from "../../components/userSortControls/UserSortControls";

export default function Users() {
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

  const setSearchingParameters = async (
    query,
    searchType,
    limit,
    filters = {}
  ) => {
    const search = { query, searchType, limit, filters };
    setLastSearch(search);
    setCurrentPage(1);
    setSearchParams({ query, searchType, limit, filters });
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
    if (lastSearch) {
      fetchUsers(newOffset, lastSearch);
    }
  };

  const fetchUsers = async (offset = 0, overrideParams = null) => {
    console.log("TRIGGERED");
    const { query, searchType, limit, filters } =
      overrideParams || searchParams;
    setResultsLoading(true);
    const result = await handleGetUsers({
      [searchType]: query,
      offset,
      limit,
      ...filters,
      parameter: sort.sortBy, // e.g. "name", "email", "manager.name"
      order: sort.sortOrder, // e.g. "ASCENDING" or "DESCENDING"
    });

    setUsers(result.users);
    setPagination((prev) => ({
      ...prev,
      offset,
      limit: result.pagination.limit,
      total: result.pagination.totalUsers,
    }));
    setResultsLoading(false);
  };

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
      fetchUsers(0, lastSearch); // restart at page 1
      setPagination((prev) => ({ ...prev, offset: 0 }));
    }
  }, [sort]);

  // 📦 On mount: fetch offices and initial users
  useEffect(() => {
    const fetchInitial = async () => {
      setPageLoading(true);
      const offices = await handleGetOffices();
      const initialSearch = {
        query: "",
        searchType: "email",
        limit: 10,
        filters: {}, // empty to fetch all
      };
      setSearchParams(initialSearch);
      setLastSearch(initialSearch);
      const result = await handleGetUsers({
        [initialSearch.searchType]: initialSearch.query,
        offset: 0,
        limit: initialSearch.limit,
        ...initialSearch.filters,
        parameter: sort.sortBy,
        order: sort.sortOrder,
      });
      setUsers(result.users);
      setPagination((prev) => ({
        ...prev,
        offset: 0,
        limit: result.pagination.limit,
        total: result.pagination.totalUsers,
      }));
      setOffices(offices);
      setPageLoading(false);
    };
    fetchInitial();
  }, []);

  if (pageLoading) return <Spinner />;

  return (
    <div className="users-container">
      <SearchBar onSearch={setSearchingParameters} offices={offices} />
      <UserSortControls
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={setSort}
      />

      {resultsLoading ? (
        <div className="users-loading">
          <Spinner />
        </div>
      ) : users.length === 0 ? (
        <div className="users-empty">
          <p>No users found matching your criteria</p>
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
