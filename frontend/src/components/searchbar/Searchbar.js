import React, { useState, useRef } from "react";
import { useForm } from "react-hook-form";
import { FiSearch, FiChevronDown } from "react-icons/fi";
import "./Searchbar.css";
import FilterMenu from "../filterMenu/FilterMenu";
import { useTranslation } from "react-i18next";

const SearchBar = ({ onSearch, offices = [] }) => {
  const { t } = useTranslation();
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

  // pre-built list of accountStates with translated labels
  const accountStates = [
    { label: t("searchBarAllStates"), value: "" },
    { label: t("searchBarComplete"), value: "COMPLETE" },
    { label: t("searchBarIncomplete"), value: "INCOMPLETE" },
  ];

  const onSubmit = ({ query, searchType, limit, accountState, office }) => {
    const filters = {
      accountState,
      office,
    };
    onSearch(query, searchType, limit, filters);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="searchBar-container">
      <div className="searchBar-wrapper">
        <button
          type="submit"
          className="searchBar-button"
          aria-label={t("searchBarSearchButton")}
        >
          <FiSearch className="search-icon" />
        </button>

        <input
          {...register("query")}
          placeholder={t("searchBarPlaceholder", {
            type: watch("searchType")
              .replace(/_/g, " ")
              .toLowerCase(),
          })}
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
          aria-label={t("searchBarLimitToggle")}
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