import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { FiSearch, FiChevronDown } from "react-icons/fi";
import "./Searchbar.css";
import FilterMenu from "../filterMenu/FilterMenu";
import { useTranslation } from "react-i18next";
import { GrDocumentPdf, GrDocumentCsv, GrDocumentExcel } from "react-icons/gr";

// Now generic: accepts filtersConfig, filterOptions, defaultValues, limitOptions
const SearchBar = ({
  onSearch,
  filtersConfig = [],
  filterOptions = {},
  defaultValues = {},
  limitOptions = [5, 10, 20],
  tristateFilters = [],
  actions,
  onExportPdf,
  onExportCsv,
  onExportXlsx,
  ...props
}) => {
  const { t } = useTranslation();
  const { register, handleSubmit, watch, setValue, reset } = useForm({
    defaultValues: {
      query: "",
      limit: 10,
      ...defaultValues,
    },
  });

  // Sync form with new defaultValues (especially limit) when they change
  useEffect(() => {
    // Always use a number for limit
    const limitValue = defaultValues.limit !== undefined ? Number(defaultValues.limit) : 10;
      console.log("SearchBar useEffect: defaultValues", defaultValues, "limitValue", limitValue);

    reset({
      query: defaultValues.query ?? "",
      ...defaultValues,
      limit: limitValue, // override in case ...defaultValues has string
    });
    setShowResultsMenu(false); // Close dropdown on reset
  }, [defaultValues, reset]);

  const [showResultsMenu, setShowResultsMenu] = useState(false);

  const onSubmit = (formData) => {
    console.log("Search submitted with data:", formData);
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

  // Add a function to clear all filters and search fields
  const handleClearFilters = () => {
    // Always default to 10 for Courses page
    reset({
      query: "",
      limit: 10,
      searchType: props.searchTypes?.[0]?.value || "title",
      ...Object.fromEntries(filtersConfig.map((f) => [f, ""])),
      ...Object.fromEntries(tristateFilters.map((f) => [f.key, ""])),
    });
    // Also trigger a search with cleared values
    handleSubmit(onSubmit)();
  };

  // If area filter exists, ensure it has an 'All areas' option
  if (
    filterOptions &&
    filterOptions.area &&
    !filterOptions.area.some((opt) => opt.value === "")
  ) {
    filterOptions.area = [
      { value: "", label: t("allAreas", "All areas") },
      ...filterOptions.area,
    ];
  }

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
            type:
              (props.searchTypes &&
                props.searchTypes.find(
                  (type) => type.value === watch("searchType")
                )?.label) ||
              t(
                `filterMenuOption${
                  (watch("searchType") || "name").charAt(0).toLowerCase() +
                  (watch("searchType") || "name").slice(1)
                }`
              ),
          })}
          className="searchBar-input"
        />
        {props.searchTypes && (
          <select {...register("searchType")} className="searchBar-select">
            {props.searchTypes.map((type) => (
              <option key={type.value} value={type.value}>
                {type.label
                  ? type.label
                  : t(`filterMenuOption${type.value.toLowerCase()}`)}
              </option>
            ))}
          </select>
        )}

        <FilterMenu
          watch={watch}
          setValue={setValue}
          handleSubmit={handleSubmit}
          filtersConfig={filtersConfig}
          filterOptions={filterOptions}
          tristateFilters={tristateFilters}
          register={register} // Pass register to FilterMenu
        />
        <button
          type="button"
          className="searchBar-clearButton"
          onClick={handleClearFilters}
          style={{ marginLeft: 8 }}
        >
          {t("courses.clearFilters")}
        </button>
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
          <div className="searchBar-dropdownMenu" role="menu" aria-label={t("searchBarLimitMenu")}> {/* Added accessibility */}
            {limitOptions.map((num) => (
              <div
                key={num}
                className="searchBar-menuItem"
                data-active={String(watch("limit")) === String(num)}
                role="menuitem"
                tabIndex={0}
                onClick={() => {
                  setValue("limit", num);
                  setShowResultsMenu(false);
                  handleSubmit(onSubmit)();
                }}
                onKeyDown={(e) => {
                  if (e.key === "Enter" || e.key === " ") {
                    setValue("limit", num);
                    setShowResultsMenu(false);
                    handleSubmit(onSubmit)();
                  }
                }}
              >
                {num}
              </div>
            ))}
          </div>
        )}
      </div>
      
      {/* PDF Export Button */}
      {onExportPdf && (
        <button
          type="button"
          className="searchBar-pdfButton"
          onClick={onExportPdf}
          title={t("exportPdf")}
        >
          <GrDocumentPdf className="searchBar-pdfIcon" />
        </button>
      )}
      {/* CSV Export Button */}
      {onExportCsv && (
        <button
          type="button"
          className="searchBar-csvButton"
          onClick={onExportCsv}
          title={t("exportCsv")}
        >
          <GrDocumentCsv className="searchBar-csvIcon" />
        </button>
      )}
      {/* XLSX Export Button */}
      {onExportXlsx && (
        <button
          type="button"
          className="searchBar-xlsxButton"
          onClick={onExportXlsx}
          title={t("exportXlsx")}
        >
          <GrDocumentExcel className="searchBar-xlsxIcon" />
        </button>
      )}
      {/* Keep actions for backward compatibility */}
      {actions && <div className="searchBar-actions">{actions}</div>}
    </form>
  );
};

export default SearchBar;
