import "./Users.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useCallback } from "react";
import "./Users.css";
import UserCard from "../../components/userCard/UserCard";
import { handleGetUsers } from "../../handles/handleGetUsers";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 5,
    total: 0,
  });
  const [searchQuery, setSearchQuery] = useState(""); // Input text
  const [searchFilter, setSearchFilter] = useState("name"); // Dropdown selection
  const [accountState, setAccountState] = useState(""); // Account status filter

  const fetchUsers = async () => {
    setLoading(true);
    setError(null);

    try {
      const params = {
        ...(searchQuery && { [searchFilter]: searchQuery }), // Only include if query exists
        ...(accountState && { accountState }), // Optional status filter
        offset: pagination.offset,
        limit: pagination.limit,
      };

      const result = await handleGetUsers(params);

      if (result.success) {
        setUsers(result.users);
        setPagination((prev) => ({
          ...prev,
          total: result.pagination.totalUsers,
        }));
      } else {
        setError(result.error || "Failed to load users");
      }
    } catch (err) {
      setError(err.message || "An unexpected error occurred");
    } finally {
      setLoading(false);
    }
  };

  // Fetch users ONLY when:
  // - searchQuery changes (user presses search button/Enter)
  // - accountState changes (optional status dropdown)
  // - pagination changes
  useEffect(() => {
    fetchUsers();
  }, [searchQuery, accountState, pagination.offset, pagination.limit]);
  // ✅ searchFilter NOT included here!

  // In Users.jsx
  const handleSearch = (query) => {
    setSearchQuery(query); // This triggers the fetch
    setPagination((prev) => ({ ...prev, offset: 0 })); // Reset to page 1
  };

  const handleFilterChange = (filter) => {
    setSearchFilter(filter); // ✅ Doesn’t trigger fetch
  };

  const handleResultsPerPageChange = (perPage) => {
    setPagination((prev) => ({ ...prev, limit: perPage, offset: 0 }));
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  return (
    <div className="users-container">
      <div className="users-header">
        <SearchBar
          onSearch={handleSearch}
          onFilterChange={handleFilterChange}
          onResultsPerPageChange={handleResultsPerPageChange}
        />
      </div>
      <div className="users-filters">
        <select
          value={accountState.accountState}
          onChange={(e) => handleFilterChange("accountState", e.target.value)}
        >
          <option value="">All states</option>
          <option value="COMPLETE">Complete</option>
          <option value="INCOMPLETE">Incomplete</option>
        </select>
      </div>
      {loading ? (
        <div className="users-loading">
          <Spinner />
        </div>
      ) : error ? (
        <div className="users-error">
          <p>Error: {error}</p>
          <button onClick={fetchUsers}>Retry</button>
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
