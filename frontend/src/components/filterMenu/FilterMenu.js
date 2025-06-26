import React, { useState } from "react";
import { FiChevronLeft } from "react-icons/fi";
import { GiSettingsKnobs } from "react-icons/gi";
import "./FilterMenu.css";

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

  const categories = [
    {
      label: "Search Type",
      key: "searchType",
      options: ["email", "name", "role"],
    },
    {
      label: "Office",
      key: "office",
      options: ["", ...offices],
    },
    {
      label: "Account State",
      key: "accountState",
      options: accountStates.map(({ label, value }) => ({ label, value })),
    }
  ];

  const getOptionLabel = (key, option) => {
    if (key === "accountState" && typeof option === "object")
      return option.label;
    if (key === "accountState") {
      const found = accountStates.find((s) => s.value === option);
      return found?.label || option;
    }
    if (key === "office") return option || "All Offices";
    return option;
  };

  const getOptionValue = (key, option) =>
    typeof option === "object" ? option.value : option;

  return (
    <div className="filterMenu">
      <div
        className="filterMenu-button"
        onClick={() => setMenuOpen((prev) => !prev)}
      >
        <GiSettingsKnobs className="filterMenu-icon" size={28}/>

      </div>
      {menuOpen && (
        <div className="filterMenu-panel">
          {categories.map(({ label, key, options }) => (
            <div
              className="filterMenu-category"
              key={key}
              onMouseEnter={() => setActiveCategory(key)}
              onMouseLeave={() => setActiveCategory(null)}
            >
              <div className="filterMenu-label">
                 <FiChevronLeft />{label}
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
                        onClick={() => {
                          setValue(key, value);
                          handleSubmit(onSubmit)();
                          setMenuOpen(false);
                        }}
                        data-active={watch(key) === value}
                      >
                        {label}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FilterMenu;