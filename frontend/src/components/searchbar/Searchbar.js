import React, { useState } from "react";
import "./Searchbar.css";
import { FiSearch, FiFilter, FiChevronDown } from "react-icons/fi";

const SearchBar = ({ onSearch, onFilterChange, onResultsPerPageChange, context }) => {
  const [searchQuery, setSearchQuery] = useState("");
  const [showFilters, setShowFilters] = useState(false);
  const [selectedFilter, setSelectedFilter] = useState("name");
  const [resultsPerPage, setResultsPerPage] = useState(5);

  const handleSearch = () => {
    if (searchQuery.trim()) {
      onSearch(searchQuery);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  const handleFilterSelect = (filter) => {
    console.log(filter);
    setSelectedFilter(filter);
    setShowFilters(false);
    onFilterChange(filter);
  };

  const handleResultsPerPageChange = (value) => {
    setResultsPerPage(value);
    onResultsPerPageChange(value);
  };

  return (
    <div className="search-container">
      <button className="search-button" onClick={handleSearch}>
        <FiSearch className="search-icon" />
      </button>

      <input
        type="text"
        className="search-input"
        placeholder={`Search by ${selectedFilter}...`}
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        onKeyPress={handleKeyPress}
      />

      <div className="filter-container">
        <button
          className="filter-button"
          onClick={() => setShowFilters(!showFilters)}
        >
          <FiFilter className="filter-icon" />
          <span className="filter-text">Filter by {selectedFilter}</span>{" "}
          {/* Show active filter */}
        </button>

        {showFilters && (
          <div className="filter-dropdown">
            <div
              className="filter-option"
              onClick={() => handleFilterSelect("name")}
            >
              Name
            </div>
            <div
              className="filter-option"
              onClick={() => handleFilterSelect("email")}
            >
              Email
            </div>
            <div
              className="filter-option"
              onClick={() => handleFilterSelect("role")}
            >
              Role
            </div>
            <div
              className="filter-option"
              onClick={() => handleFilterSelect("workplace")}
            >
              Workplace
            </div>
            <div
              className="filter-option"
              onClick={() => handleFilterSelect("manager")}
            >
              Manager
            </div>
          </div>
        )}
      </div>

      <div className="results-per-page">
        <select
          value={resultsPerPage}
          onChange={(e) => handleResultsPerPageChange(Number(e.target.value))}
          className="results-select"
        >
          <option value={5}>5 per page</option>
          <option value={10}>10 per page</option>
          <option value={20}>20 per page</option>
          <option value={50}>50 per page</option>
          <option value={100}>100 per page</option>
        </select>
        <FiChevronDown className="select-arrow" />
      </div>
    </div>
  );
};

export default SearchBar;
