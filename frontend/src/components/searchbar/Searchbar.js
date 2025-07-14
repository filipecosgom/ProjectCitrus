/**
 * SearchBar.jsx
 *
 * Search bar component with filter menu, limit selector, export actions, and advanced options.
 * Integrates with react-hook-form for form state and supports custom actions and exports.
 *
 * @module SearchBar
 */
import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { FiSearch, FiChevronDown } from "react-icons/fi";
import "./Searchbar.css";
import FilterMenu from "../filterMenu/FilterMenu";
import { useTranslation } from "react-i18next";
import { GrDocumentPdf, GrDocumentCsv, GrDocumentExcel } from "react-icons/gr";
import { FaBookBookmark } from "react-icons/fa6";
/**
 * SearchBar component
 *
 * @param {Object} props - Component props
 * @param {Function} props.onSearch - Callback for search action
 * @param {Array<string>} [props.filtersConfig=[]] - List of filter keys for FilterMenu
 * @param {Object} [props.filterOptions={}] - Mapping of filter keys to their available options
 * @param {Object} [props.defaultValues={}] - Default values for form fields
 * @param {Array<number>} [props.limitOptions=[5,10,20]] - Options for result limit selector
 * @param {Array<{key: string, label: string}>} [props.tristateFilters=[]] - List of tristate filters
 * @param {React.ReactNode} [props.actions] - Custom action buttons or elements
 * @param {Function} [props.onExportPdf] - Callback for PDF export
 * @param {Function} [props.onExportCsv] - Callback for CSV export
 * @param {Function} [props.onExportXlsx] - Callback for XLSX export
 * @returns {JSX.Element} The rendered search bar
 */
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
      query: defaultValues.query ?? "",
      ...defaultValues,
      limit:
        defaultValues.limit !== undefined ? Number(defaultValues.limit) : 10,
    },
  });
  const [showResultsMenu, setShowResultsMenu] = useState(false);

  /**
   * Effect: Resets form and result menu when defaultValues change.
   */
  useEffect(() => {
    const limitValue =
      defaultValues.limit !== undefined ? Number(defaultValues.limit) : 10;
    reset({
      query: defaultValues.query ?? "",
      ...defaultValues,
      limit: limitValue,
    });
    setShowResultsMenu(false);
  }, [defaultValues, reset]);

  /**
   * Handles form submission and triggers search callback.
   *
   * @param {Object} formData - Form data from react-hook-form
   */
  const onSubmit = (formData) => {
    const filters = {};
    filtersConfig.forEach((filter) => {
      filters[filter] = formData[filter];
    });
    tristateFilters.forEach(({ key }) => {
      filters[key] = formData[key];
    });
    onSearch(formData.query, formData.searchType, formData.limit, filters);
  };

  /**
   * Clears all filters and resets the form to default state.
   */
  const handleClearFilters = () => {
    reset({
      query: "",
      limit: 10,
      searchType: props.searchTypes?.[0]?.value || "title",
      ...Object.fromEntries(filtersConfig.map((f) => [f, ""])),
      ...Object.fromEntries(tristateFilters.map((f) => [f.key, ""])),
    });
    handleSubmit(onSubmit)();
  };

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
    <>
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
            placeholder={
              props.placeholder !== undefined
                ? props.placeholder
                : t("searchBarPlaceholder", {
                    type:
                      (props.searchTypes &&
                        props.searchTypes.find(
                          (type) => type.value === watch("searchType")
                        )?.label) ||
                      t(
                        `filterMenuOption${
                          (watch("searchType") || "name")
                            .charAt(0)
                            .toLowerCase() +
                          (watch("searchType") || "name").slice(1)
                        }`
                      ),
                  })
            }
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
            register={register}
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
            <div
              className="searchBar-dropdownMenu"
              role="menu"
              aria-label={t("searchBarLimitMenu")}
            >
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
        {props.renderYearDropdown && (
          <div className="searchBar-yearDropdown">
            {props.renderYearDropdown()}
          </div>
        )}
        {/* AddCompletedCourseOffcanvas is now only controlled by the parent (TrainingTab) */}
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
        {props.onAddCourseToUser && (
          <button
            type="button"
            className="searchBar-addCourseButton"
            onClick={props.onAddCourseToUser}
            user={props.user}
          >
            <FaBookBookmark className="searchBar-addCourseIcon" />
            {t("courses.addCompletedCourse", "Add Completed Course")}
          </button>
        )}
        {actions && <div className="searchBar-actions">{actions}</div>}
      </form>
      {/* AddCompletedCourseOffcanvas is now only controlled by the parent (TrainingTab) */}
    </>
  );
};

export default SearchBar;
