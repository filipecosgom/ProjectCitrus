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
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 5,
    total: 0,
  });
  const [pageLoading, setPageLoading] = useState(true);
  const [resultsLoading, setResultsLoading] = useState(false);
  const [offices, setOffices] = useState([]);

  const handleSearch = async (query, filter, limit) => {
    console.log(query);
    console.log(filter);
    console.log(limit);
    setResultsLoading(true);
    const result = await handleGetUsers({
      [filter]: query,
      offset: 0, // Reset to page 1
      limit: limit || pagination.limit,
    });

    setUsers(result.users);
    setPagination({
      offset: 0,
      limit: limit || pagination.limit,
      total: result.pagination.totalUsers,
    });
    setResultsLoading(false);
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  // Carrega enums
  useEffect(() => {
    const fetchEnums = async () => {
      const offices = await handleGetOffices();
      setOffices(offices)
    };
    setPageLoading(true);
    fetchEnums();
    setPageLoading(false);
  }, []);

  if (pageLoading) return <Spinner />;

  return (
    <div className="users-container">
      <div className="users-header">
        <SearchBar
        onSearch={handleSearch}
        offices={offices} />
      </div>
      {pageLoading ? (
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
