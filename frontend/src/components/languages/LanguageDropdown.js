import React, { useState, useRef, useEffect } from "react";
import flagEn from "../../assets/flags/flag-en.png";
import flagPt from "../../assets/flags/flag-pt.png";
import "./LanguageDropdown.css";

const LANGUAGES = [
  { code: "en", label: "English", flag: flagEn },
  { code: "pt", label: "Portuguese", flag: flagPt },
];

export default function LanguageDropdown({ language, setLanguage }) {
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const selectedLang = LANGUAGES.find(l => l.code === language);

  return (
    <div className="language-dropdown" ref={dropdownRef}>
      <div
        className="language-selected"
        onClick={() => setShowDropdown(v => !v)}
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
        <span style={{ marginLeft: "auto", fontSize: 16 }}>▼</span>
      </div>
      {showDropdown && (
        <div className="language-options">
          {LANGUAGES.filter(l => l.code !== language).map(lang => (
            <div
              key={lang.code}
              className="language-option"
              onClick={() => {
                setLanguage(lang.code);
                setShowDropdown(false);
              }}
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