// FilterMenu.jsx
import React, { useState } from "react";
import { FiChevronLeft } from "react-icons/fi";
import { GiSettingsKnobs } from "react-icons/gi";
import { useTranslation } from "react-i18next";
import "./FilterMenu.css";
/**
 * FilterMenu.jsx
 *
 * A dynamic filter menu component for selecting filter categories and tristate options.
 * Supports configurable categories, options, and tristate filters.
 *
 * @module FilterMenu
 */
import { FaCheck, FaTimes, FaMinus } from "react-icons/fa";

const FilterMenu = ({
  watch,
  setValue,
  filtersConfig = [],
  filterOptions = {},
  tristateFilters = [],
  /**
   * FilterMenu component
   *
   * @param {Object} props - Component props
   * @param {Function} props.watch - Function to get the current value of a filter key
   * @param {Function} props.setValue - Function to set the value of a filter key
   * @param {Array<string>} [props.filtersConfig=[]] - List of filter keys to display as categories
   * @param {Object} [props.filterOptions={}] - Mapping of filter keys to their available options
   * @param {Array<{key: string, label: string}>} [props.tristateFilters=[]] - List of tristate filters (key and label)
   * @returns {JSX.Element} The rendered filter menu
   */
}) => {
  const [activeCategory, setActiveCategory] = useState(null);
  const [menuOpen, setMenuOpen] = useState(false);
  const { t } = useTranslation();

  // Build categories dynamically from filtersConfig and filterOptions
  const categories = filtersConfig.map((key) => {
    const options = filterOptions[key] || [];
    return {
      /**
       * Build categories dynamically from filtersConfig and filterOptions
       * @type {Array<{id: string, key: string, options: Array}>}
       */
      id: `filterMenu${key.charAt(0).toUpperCase() + key.slice(1)}`,
      key,
      options,
    };
  });

  const getOptionLabel = (key, opt) => {
    if (typeof opt === "object" && opt.label) return opt.label;
    if (key === "area") {
      // For area options, just return the value (area name) as label
      return typeof opt === "object" ? opt.value : opt;
    }
    if (key === "searchType") {
      const cap =
        typeof opt === "string"
          ? opt.charAt(0).toUpperCase() + opt.slice(1)
          : "";
      return t(`filterMenuOption${cap}`);
    }
    if (key === "office") {
      /**
       * Get the display label for a filter option.
       *
       * @param {string} key - The filter key/category
       * @param {any} opt - The option value or object
       * @returns {string} The display label for the option
       */
      if (typeof opt === "object" && opt.label) return opt.label;
      return opt ? opt : t("filterMenuAllOffices");
    }
    /**
     * Get the value for a filter option.
     *
     * @param {string} key - The filter key/category
     * @param {any} opt - The option value or object
     * @returns {any} The value for the option
     */
    return typeof opt === "object" ? opt.label : opt;
  };
  /**
   * Export the FilterMenu component
   */

  const getOptionValue = (key, opt) =>
    typeof opt === "object" ? opt.value : opt;

  return (
    <div className="filterMenu">
      <div
        className="filterMenu-button"
        onClick={() => setMenuOpen((prev) => !prev)}
      >
        <GiSettingsKnobs
          className="filterMenu-icon"
          size={28}
          title={t("filterMenuSettings")}
        />
      </div>

      {menuOpen && (
        <div className="filterMenu-panel">
          {categories.map(({ id, key, options }) => (
            <div
              className="filterMenu-category"
              key={key}
              onMouseEnter={() => setActiveCategory(key)}
              onMouseLeave={() => setActiveCategory(null)}
            >
              <div className="filterMenu-label">
                <FiChevronLeft aria-hidden="true" />
                {key === "area" ? t("courses.sortArea") : t(id)}
              </div>

              {activeCategory === key && (
                <div className="filterMenu-submenu">
                  {options.map((opt) => {
                    const value = getOptionValue(key, opt);
                    const label = getOptionLabel(key, opt);

                    return (
                      <div
                        key={value}
                        className="filterMenu-item"
                        data-active={watch(key) === value}
                        onClick={() => {
                          setValue(key, value);
                          setMenuOpen(false);
                        }}
                      >
                        {label}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          ))}
          <div className="filterMenu-tristateGroup">
            {tristateFilters.map(({ key, label }) => (
              <div className="filterMenu-tristate" key={key}>
                <button
                  type="button"
                  data-state={watch(key)}
                  className="filterMenu-tristateButton"
                  onClick={() => {
                    const current = watch(key);
                    const next =
                      current === null ? true : current === true ? false : null;
                    setValue(key, next);
                  }}
                  title={
                    watch(key) === true
                      ? t("filterYes")
                      : watch(key) === false
                      ? t("filterNo")
                      : t("filterAny")
                  }
                >
                  {watch(key) === true ? (
                    <FaCheck className="filterMenu-icon" />
                  ) : watch(key) === false ? (
                    <FaTimes className="filterMenu-icon" />
                  ) : (
                    <FaMinus className="filterMenu-icon" />
                  )}
                </button>
                <span className="filterMenu-label-text">{label}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default FilterMenu;
