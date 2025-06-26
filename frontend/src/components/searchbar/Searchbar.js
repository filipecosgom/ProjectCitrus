import React, { useState, useRef } from "react";
import { useForm } from "react-hook-form";
import { FiSearch, FiChevronDown } from "react-icons/fi";
import "./Searchbar.css";
import FilterMenu from "../filterMenu/FilterMenu";

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
    onSearch(query, searchType, limit, filters);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="searchBar-container">
      {/* Search Input + Button */}
      <div className="searchBar-wrapper">
        <button type="submit" className="searchBar-button">
          <FiSearch type="submit" className="search-icon" />
        </button>

        <input
          {...register("query")}
          placeholder={`Search by ${watch("searchType").replace(/_/g, " ")}...`}
          className="searchBar-input"
        />
        <FilterMenu
        watch={watch}
        setValue={setValue}
        handleSubmit={handleSubmit}
        onSubmit={onSubmit}
        offices={offices}
        accountStates={[
          { label: "All States", value: "" },
          { label: "Complete", value: "COMPLETE" },
          { label: "Incomplete", value: "INCOMPLETE" },
        ]}
      />
      </div>  

      <div className="searchBar-dropdown">
        <button
          type="button"
          className="searchBar-dropdownToggle toggle-limit"
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
