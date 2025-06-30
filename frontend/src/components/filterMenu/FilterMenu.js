// FilterMenu.jsx
import React, { useState } from "react";
import { FiChevronLeft } from "react-icons/fi";
import { GiSettingsKnobs } from "react-icons/gi";
import { useTranslation } from "react-i18next";
import "./FilterMenu.css";
import useAuthStore from "../../stores/useAuthStore";
import { FaCheck, FaTimes, FaMinus } from "react-icons/fa";

const FilterMenu = ({
  watch,
  setValue,
  handleSubmit,
  onSubmit,
  filtersConfig = [],
  filterOptions = {},
  tristateFilters = [],
}) => {
  const [activeCategory, setActiveCategory] = useState(null);
  const [menuOpen, setMenuOpen] = useState(false);
  const { t } = useTranslation();
  const isAdmin = useAuthStore.getState().user?.userIsAdmin;

  // Build categories dynamically from filtersConfig and filterOptions
  const categories = filtersConfig.map((key) => {
    const options = filterOptions[key] || [];
    return {
      id: `filterMenu${key.charAt(0).toUpperCase() + key.slice(1)}`,
      key,
      options,
    };
  });

  const getOptionLabel = (key, opt) => {
    if (key === "searchType") {
      const cap = typeof opt === "string" ? opt.charAt(0).toUpperCase() + opt.slice(1) : "";
      return t(`filterMenuOption${cap}`);
    }
    if (key === "office") {
      if (typeof opt === "object" && opt.label) return opt.label;
      return opt ? opt : t("filterMenuAllOffices");
    }
    if (typeof opt === "object" && opt.label) return opt.label;
    return typeof opt === "object" ? opt.label : opt;
  };

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
                {t(id)}
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
                          handleSubmit(onSubmit)();
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
                      current === null
                        ? true
                        : current === true
                        ? false
                        : null;
                    setValue(key, next);
                    handleSubmit(onSubmit)();
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
