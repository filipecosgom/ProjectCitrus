import "./Users.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useCallback } from "react";
import "./Users.css";
import UserCard from "../../components/userCard/UserCard";
import { handleGetUsers } from "../../handles/handleGetUsers";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import { handleGetOffices } from "../../handles/handleGetEnums";

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

  // 🧠 Track latest search context
  const [searchParams, setSearchParams] = useState({
    query: "",
    searchType: "email",
    resultsPerPage: 10,
    filters: {},
  });

  // 🔍 Triggered by the SearchBar
  const handleSearch = async (query, searchType, limit, filters = {}) => {
    setSearchParams({ query, searchType, resultsPerPage: limit, filters });
    setPagination((prev) => ({ ...prev, offset: 0 })); // reset to first page
  };

  // 🔁 On pagination offset change
  useEffect(() => {
    const fetchUsers = async () => {
      const { query, searchType, resultsPerPage, filters } = searchParams;

      setResultsLoading(true);
      const result = await handleGetUsers({
        [searchType]: query,
        offset: pagination.offset,
        limit: resultsPerPage,
        ...filters,
      });

      setUsers(result.users);
      setPagination((prev) => ({
        ...prev,
        total: result.pagination.totalUsers,
        limit: result.pagination.limit,
      }));
      setResultsLoading(false);
    };

    if (searchParams.query !== undefined) {
      fetchUsers();
    }
  }, [pagination.offset, searchParams]);

  // 📦 On mount: fetch offices and initial users
  useEffect(() => {
    const fetchInitial = async () => {
      setPageLoading(true);
      const offices = await handleGetOffices();
      const initialSearch = {
        query: "",
        searchType: "email",
        resultsPerPage: 10,
        filters: {}, // empty to fetch all
      };
      setSearchParams(initialSearch); // 💡 this enables pagination re-fetch later
      const result = await handleGetUsers({
        [initialSearch.searchType]: initialSearch.query,
        offset: 0,
        limit: initialSearch.resultsPerPage,
        ...initialSearch.filters,
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

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  if (pageLoading) return <Spinner />;

  return (
    <div className="users-container">
      <SearchBar onSearch={handleSearch} offices={offices} />
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

          <Pagination
            offset={pagination.offset}
            limit={pagination.limit}
            total={pagination.total}
            onChange={handlePageChange}
          />
        </div>
      )}
      ;
    </div>
  );
}
