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
  offices = [],
  accountStates = [],
}) => {
  const [activeCategory, setActiveCategory] = useState(null);
  const [menuOpen, setMenuOpen] = useState(false);
  const { t } = useTranslation();
  const isAdmin = useAuthStore.getState().user?.userIsAdmin;

  const rawCategories = [
    {
      id: "filterMenuSearchType",
      key: "searchType",
      options: ["name", "email", "role"],
    },
    {
      id: "filterMenuOffice",
      key: "office",
      options: ["", ...offices],
    },
    {
      id: "filterMenuAccountState",
      key: "accountState",
      options: accountStates,
    },
  ];

  const categories = isAdmin
    ? rawCategories
    : rawCategories.filter((cat) => cat.key !== "accountState");

  const getOptionLabel = (key, opt) => {
    if (key === "searchType") {
      // e.g. "name" → filterMenuOptionName
      const cap = opt.charAt(0).toUpperCase() + opt.slice(1);
      return t(`filterMenuOption${cap}`);
    }
    if (key === "office") {
      return opt ? opt : t("filterMenuAllOffices");
    }
    if (typeof opt === "object" && opt.label) return opt.label;
    // accountStates: each opt is { label, value }
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
            <div className="filterMenu-tristate">
              <button
                type="button"
                data-state={watch("isManager")}
                className="filterMenu-tristateButton"
                onClick={() => {
                  const current = watch("isManager");
                  const next =
                    current === null ? true : current === true ? false : null;
                  setValue("isManager", next);
                  handleSubmit(onSubmit)();
                }}
                title={
                  watch("isManager") === true
                    ? t("filterYes")
                    : watch("isManager") === false
                    ? t("filterNo")
                    : t("filterAny")
                }
              >
                {watch("isManager") === true ? (
                  <FaCheck className="filterMenu-icon"/>
                ) : watch("isManager") === false ? (
                  <FaTimes className="filterMenu-icon"/>
                ) : (
                  <FaMinus className="filterMenu-icon"/>
                )}
              </button>
              <span className="filterMenu-label-text">
                {t("filterMenuisManager")}
              </span>
              </div>

            <div className="filterMenu-tristate">
              <button
                type="button"
                data-state={watch("isAdmin")}
                className="filterMenu-tristateButton"
                onClick={() => {
                  const current = watch("isAdmin");
                  const next =
                    current === null ? true : current === true ? false : null;
                  setValue("isAdmin", next);
                  handleSubmit(onSubmit)();
                }}
                title={
                  watch("isAdmin") === true
                    ? t("filterYes")
                    : watch("isAdmin") === false
                    ? t("filterNo")
                    : t("filterAny")
                }
              >
                {watch("isAdmin") === true ? (
                  <FaCheck className="filterMenu-icon"/>
                ) : watch("isAdmin") === false ? (
                  <FaTimes className="filterMenu-icon"/>
                ) : (
                  <FaMinus className="filterMenu-icon"/>
                )}
              </button>
              <span className="filterMenu-label-text">
                {t("filterMenuisAdmin")}
              </span>
            </div>


            <div className="filterMenu-tristate">
              <button
                type="button"
                data-state={watch("isManaged")}
                className="filterMenu-tristateButton"
                onClick={() => {
                  const current = watch("isManaged");
                  const next =
                    current === null ? true : current === true ? false : null;
                  setValue("isManaged", next);
                  handleSubmit(onSubmit)();
                }}
                title={
                  watch("isManaged") === true
                    ? t("filterYes")
                    : watch("isManaged") === false
                    ? t("filterNo")
                    : t("filterAny")
                }
              >
                {watch("isManaged") === true ? (
                  <FaCheck className="filterMenu-icon"/>
                ) : watch("isManaged") === false ? (
                  <FaTimes className="filterMenu-icon"/>
                ) : (
                  <FaMinus className="filterMenu-icon"/>
                )}
              </button>
              <span className="filterMenu-label-text">
                {t("filterMenuisManaged")}
              </span>
            
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FilterMenu;
