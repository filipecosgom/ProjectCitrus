import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { FiSearch, FiChevronDown } from "react-icons/fi";
import "./Searchbar.css";
import FilterMenu from "../filterMenu/FilterMenu";
import { useTranslation } from "react-i18next";

// Now generic: accepts filtersConfig, filterOptions, defaultValues, limitOptions
const SearchBar = ({
  onSearch,
  filtersConfig = [],
  filterOptions = {},
  defaultValues = {},
  limitOptions = [5, 10, 20],
  tristateFilters = [],
  ...props
}) => {
  const { t } = useTranslation();
  const { register, handleSubmit, watch, setValue } = useForm({
    defaultValues: {
      query: "",
      searchType: "name",
      limit: 10,
      ...defaultValues,
    },
  });
  const [showResultsMenu, setShowResultsMenu] = useState(false);

  const onSubmit = (formData) => {
    const filters = {};
    filtersConfig.forEach((filter) => {
      filters[filter] = formData[filter];
    });
    // Add tristate filters
    tristateFilters.forEach(({ key }) => {
      filters[key] = formData[key];
    });
    onSearch(formData.query, formData.searchType, formData.limit, filters);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="searchBar-container">
      <div className="searchBar-wrapper">
        <button
          type="submit"
          className="searchBar-button"
          aria-label={t("searchBarSearchButton")}
        >
          <FiSearch className="searchBar-icon" />
        </button>
        <input
          {...register("query")}
          placeholder={t("searchBarPlaceholder", {
            type: t(
              `filterMenuOption${
                (watch("searchType") || "name").charAt(0).toUpperCase() +
                (watch("searchType") || "name").slice(1)
              }`
            ).toLowerCase(),
          })}
          className="searchBar-input"
        />
        {props.searchTypes && (
          <select {...register("searchType")} className="searchBar-select">
            {props.searchTypes.map((type) => (
              <option key={type.value} value={type.value}>
                {t(
                  `filterMenuOption${
                    type.value.charAt(0).toUpperCase() + type.value.slice(1)
                  }`
                )}
              </option>
            ))}
          </select>
        )}

        <FilterMenu
          watch={watch}
          setValue={setValue}
          handleSubmit={handleSubmit}
          onSubmit={onSubmit}
          filtersConfig={filtersConfig}
          filterOptions={filterOptions}
          tristateFilters={tristateFilters}
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
            {limitOptions.map((num) => (
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
