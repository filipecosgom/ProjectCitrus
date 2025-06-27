import React, { useState, useRef } from "react";
import { useForm } from "react-hook-form";
import { FiSearch, FiChevronDown } from "react-icons/fi";
import "./Searchbar.css";
import FilterMenu from "../filterMenu/FilterMenu";
import { useIntl } from "react-intl";


const SearchBar = ({ onSearch, offices = [] }) => {
  const { register, handleSubmit, watch, setValue } = useForm({
    defaultValues: {
      query: "",
      searchType: "name",
      accountState: "",
      office: "",
      limit: 10,
    },
  });
  const [showResultsMenu, setShowResultsMenu] = useState(false);
  const limit = [5, 10, 20];

  const intl = useIntl();

  // pre-built list of accountStates with translated labels
  const accountStates = [
    { label: intl.formatMessage({ id: "searchBarAllStates" }), value: "" },
    { label: intl.formatMessage({ id: "searchBarComplete" }), value: "COMPLETE" },
    { label: intl.formatMessage({ id: "searchBarIncomplete" }), value: "INCOMPLETE" },
  ];


  const onSubmit = ({ query, searchType, limit, accountState, office }) => {
    const filters = {
      accountState,
      office,
    };
    onSearch(query, searchType, limit, filters);
  };

 return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="searchBar-container"
    >
      <div className="searchBar-wrapper">
        <button
          type="submit"
          className="searchBar-button"
          aria-label={intl.formatMessage({ id: "searchBarSearchButton" })}
        >
          <FiSearch className="search-icon" />
        </button>

        <input
          {...register("query")}
          placeholder={intl.formatMessage(
            { id: "searchBarPlaceholder" },
            {
              type: watch("searchType")
                .replace(/_/g, " ")
                .toLowerCase(), // if you need lowercase
            }
          )}
          className="searchBar-input"
        />

        <FilterMenu
          watch={watch}
          setValue={setValue}
          handleSubmit={handleSubmit}
          onSubmit={onSubmit}
          offices={offices}
          accountStates={accountStates}
        />
      </div>

      <div className="searchBar-dropdown">
        <button
          type="button"
          className="searchBar-dropdownToggle toggle-limit"
          onClick={() => setShowResultsMenu((prev) => !prev)}
          aria-expanded={showResultsMenu}
          aria-label={intl.formatMessage({ id: "searchBarLimitToggle" })}
        >
          {watch("limit")} <FiChevronDown />
        </button>

        {showResultsMenu && (
          <div className="searchBar-dropdownMenu">
            {limit.map((num) => (
              <div
                key={num}
                className="searchBar-menuItem"
                data-active={watch("limit") === num}
                onClick={() => {
                  setValue("limit", num);
                  setShowResultsMenu(false);
                  handleSubmit(onSubmit)();
                }}
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