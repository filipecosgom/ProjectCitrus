import React, { useState, useRef, useEffect } from "react";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import "./LanguageDropdown.css";
import useLocaleStore from "../../stores/useLocaleStore";

const LANGUAGES = [
  { code: "en", label: "English", flag: flagEn },
  { code: "pt", label: "Portuguese", flag: flagPt },
];

export default function LanguageDropdown() {
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);
  
  // Get both locale and setLocale directly from the store
  const locale = useLocaleStore((state) => state.locale);
  const setLocale = useLocaleStore((state) => state.setLocale);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Find the selected language based on the store's locale
  const selectedLang = LANGUAGES.find((lang) => lang.code === locale) || LANGUAGES[0];

  const handleChangeLanguage = (newLocale) => {
    setLocale(newLocale); // Update the store directly
    setShowDropdown(false);
  };

  return (
    <div className="language-dropdown" ref={dropdownRef}>
      <div
        className="language-selected"
        onClick={() => setShowDropdown((v) => !v)}
      >
        <img
          src={selectedLang.flag}
          alt={selectedLang.label}
          className="language-flag"
          width={32}
          height={18}
        />
        <span className="language-label" style={{ marginLeft: 8 }}>
          {selectedLang.label}
        </span>
        <span
          className={`dropdown-arrow${showDropdown ? " open" : ""}`}
          style={{ marginLeft: "auto", fontSize: 16 }}
        >
          ▼
        </span>
      </div>
      {showDropdown && (
        <div className="language-options">
          {LANGUAGES.filter((l) => l.code !== locale).map((lang) => (
            <div
              key={lang.code}
              className="language-option"
              onClick={() => handleChangeLanguage(lang.code)}
            >
              <img
                src={lang.flag}
                alt={lang.label}
                className="language-flag"
                width={32}
                height={18}
              />
              <span className="language-label" style={{ marginLeft: 8 }}>
                {lang.label}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
