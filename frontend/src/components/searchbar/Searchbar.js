import React, { useState, useRef } from "react";
import { useForm } from "react-hook-form";
import { FiSearch, FiChevronDown } from "react-icons/fi";
import "./Searchbar.css";

const SearchBar = ({ onSearch, offices = [] }) => {
  const { register, handleSubmit, watch, setValue } = useForm({
    defaultValues: {
      query: "",
      searchType: "email",
      accountState: "",
      office: "",
      limit: 10,
    },
  });

  const [showSearchTypeMenu, setShowSearchTypeMenu] = useState(false);
  const [showOfficeMenu, setShowOfficeMenu] = useState(false);
  const [showAccountMenu, setShowAccountMenu] = useState(false);
  const accountStates = [
    { label: "All States", value: "" },
    { label: "Complete", value: "COMPLETE" },
    { label: "Incomplete", value: "INCOMPLETE" },
  ];
  const [showResultsMenu, setShowResultsMenu] = useState(false);
  const limit = [5, 10, 20];

  const onSubmit = ({ query, searchType, limit, accountState, office }) => {
    const filters = {
      accountState,
      office,
    };
    console.log(limit);
    onSearch(query, searchType, limit, filters);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="searchBar-container">
      {/* Search Input + Button */}
      <div className="searchBar-wrapper">
        <button type="submit" className="searchBar-button">
          <FiSearch />
        </button>

        <input
          {...register("query")}
          placeholder={`Search by ${watch("searchType").replace(/_/g, " ")}...`}
          className="searchBar-input"
        />
      </div>

      {/* Search Type Dropdown */}
      <div className="searchBar-dropdown">
        <button
          type="button"
          className="searchBar-dropdownToggle"
          onClick={() => setShowSearchTypeMenu(!showSearchTypeMenu)}
        >
          {watch("searchType").replace(/_/g, " ")} <FiChevronDown />
        </button>

        {showSearchTypeMenu && (
          <div className="searchBar-dropdownMenu">
            <div
              className="searchBar-menuItem"
              onClick={() => {
                setValue("searchType", "email");
                setShowSearchTypeMenu(false);
              }}
            >
              email
            </div>
            <div
              className="searchBar-menuItem"
              onClick={() => {
                setValue("searchType", "name");
                setShowSearchTypeMenu(false);
              }}
            >
              name
            </div>
            <div
              className="searchBar-menuItem"
              onClick={() => {
                setValue("searchType", "role");
                setShowSearchTypeMenu(false);
              }}
            >
              role
            </div>
          </div>
        )}
      </div>

      {/* Office Dropdown */}
      <div className="searchBar-dropdown">
        <button
          type="button"
          className="searchBar-dropdownToggle"
          onClick={() => setShowOfficeMenu(!showOfficeMenu)}
        >
          {watch("office") || "Office"} <FiChevronDown />
        </button>

        {showOfficeMenu && (
          <div className="searchBar-dropdownMenu">
            <div
              className="searchBar-menuItem"
              onClick={() => {
                setValue("office", "");
                setShowOfficeMenu(false);
              }}
            >
              All Offices
            </div>
            {offices.map((place) => (
              <div
                key={place}
                className="searchBar-menuItem"
                onClick={() => {
                  setValue("office", place);
                  setShowOfficeMenu(false);
                }}
              >
                {place}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="searchBar-dropdown">
        <button
          type="button"
          className="searchBar-dropdownToggle"
          onClick={() => setShowAccountMenu(!showAccountMenu)}
          aria-expanded={showAccountMenu}
        >
          {accountStates.find((opt) => opt.value === watch("accountState"))
            ?.label || "Account State"}{" "}
          <FiChevronDown />
        </button>

        {showAccountMenu && (
          <div className="searchBar-dropdownMenu">
            {accountStates.map((opt) => (
              <div
                key={opt.value}
                className="searchBar-menuItem"
                onClick={() => {
                  setValue("accountState", opt.value);
                  setShowAccountMenu(false);
                }}
                data-active={watch("accountState") === opt.value}
              >
                {opt.label}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="searchBar-dropdown">
        <button
          type="button"
          className="searchBar-dropdownToggle"
          onClick={() => setShowResultsMenu(!showResultsMenu)}
          aria-expanded={showResultsMenu}
        >
          {watch("limit")}<FiChevronDown />
        </button>

        {showResultsMenu && (
          <div className="searchBar-dropdownMenu">
            {limit.map((num) => (
              <div
                key={num}
                className="searchBar-menuItem"
                onClick={() => {
                  setValue("limit", num);
                  setShowResultsMenu(false);
                  handleSubmit(onSubmit)(); // 🔄 re-run search
                }}
                data-active={watch("limit") === num}
              >
                {num}
              </div>
            ))}
          </div>
        )}
      </div>
    </form>
  );
};

export default SearchBar;
