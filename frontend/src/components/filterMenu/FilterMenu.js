// FilterMenu.jsx
import React, { useState } from "react";
import { FiChevronLeft } from "react-icons/fi";
import { GiSettingsKnobs } from "react-icons/gi";
import { useIntl } from "react-intl";
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
  const intl = useIntl();

  const categories = [
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
      options: accountStates, // already [{label,value},…] from parent
    },
  ];

  const getOptionLabel = (key, opt) => {
    if (key === "searchType") {
      // e.g. "name" → filterMenuOptionName
      const cap = opt.charAt(0).toUpperCase() + opt.slice(1);
      return intl.formatMessage({ id: `filterMenuOption${cap}` });
    }
    if (key === "office") {
      return opt
        ? opt
        : intl.formatMessage({ id: "filterMenuAllOffices" });
    }
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
          title={intl.formatMessage({ id: "filterMenuSettings" })}
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
                {intl.formatMessage({ id })}
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
        </div>
      )}
    </div>
  );
};

export default FilterMenu;