/**
 * @file CycleOffcanvas.js
 * @module CycleOffcanvas
 * @description Offcanvas panel for creating a new cycle.
 * @author Project Citrus Team
 */

import React, { useState, useEffect } from "react";
import { FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import Calendar from "react-calendar";
import { toast } from "react-toastify"; // ✅ ADICIONAR
import { createCycle, fetchActiveUsersCount } from "../../api/cyclesApi";
import useLocaleStore from "../../stores/useLocaleStore";
import "react-calendar/dist/Calendar.css";
import "./CycleOffcanvas.css";

/**
 * CycleOffcanvas component for creating a new cycle.
 * @param {Object} props - Component props
 * @param {boolean} props.isOpen - Whether the offcanvas is open
 * @param {Function} props.onClose - Callback to close the offcanvas
 * @param {Function} props.onCycleCreated - Callback when cycle is successfully created
 * @returns {JSX.Element|null}
 */
const CycleOffcanvas = ({ isOpen, onClose, onCycleCreated }) => {
  const { t } = useTranslation();
  const locale = useLocaleStore((state) => state.locale);
  const [selectedRange, setSelectedRange] = useState([new Date(), new Date()]);
  const [activeUsersCount, setActiveUsersCount] = useState(0);
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Handles open/close animation for offcanvas.
   */
  useEffect(() => {
    if (isOpen) {
      setShouldRender(true);
      const timer = setTimeout(() => {
        setIsAnimating(true);
      }, 10);
      return () => clearTimeout(timer);
    } else {
      setIsAnimating(false);
      const timer = setTimeout(() => {
        setShouldRender(false);
      }, 400);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  /**
   * Loads active users count when offcanvas opens.
   */
  useEffect(() => {
    if (isOpen) {
      loadActiveUsersCount();
    }
  }, [isOpen]);

  /**
   * Locks body scroll when offcanvas is open.
   */
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  /**
   * Handles Escape key to close offcanvas.
   */
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") {
        onClose();
      }
    };
    if (isOpen) {
      document.addEventListener("keydown", handleEsc);
    }
    return () => {
      document.removeEventListener("keydown", handleEsc);
    };
  }, [isOpen, onClose]);

  /**
   * Loads the count of active users for the cycle.
   */
  const loadActiveUsersCount = async () => {
    try {
      const response = await fetchActiveUsersCount();
      if (response.success) {
        setActiveUsersCount(response.data.data?.totalUsers || 0);
      }
    } catch (error) {
      console.error("Error loading active users count:", error);
    }
  };

  /**
   * Handles click on backdrop to close offcanvas.
   * @param {React.MouseEvent} e - Mouse event
   */
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  /**
   * Handles change in calendar date selection.
   * @param {Date|Array<Date>} value - Selected date(s)
   */
  const handleDateChange = (value) => {
    if (Array.isArray(value)) {
      setSelectedRange(value);
    } else {
      setSelectedRange([value, value]);
    }
  };

  /**
   * Calculates the number of days in the selected cycle range.
   * @returns {number} Number of days
   */
  const calculateDays = () => {
    if (!selectedRange || selectedRange.length !== 2) return 0;
    const [start, end] = selectedRange;
    if (start.getTime() === end.getTime()) {
      return 1;
    }
    const diffTime = end.getTime() - start.getTime();
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;
    return diffDays;
  };

  /**
   * Formats a date according to locale.
   * @param {Date} date - Date object
   * @returns {string} Formatted date
   */
  const formatDate = (date) => {
    const localeMap = {
      pt: "pt-PT",
      en: "en-US",
    };
    return date.toLocaleDateString(localeMap[locale] || "pt-PT", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  /**
   * Handles creation of a new cycle.
   */
  const handleCreateCycle = async () => {
    if (!selectedRange || selectedRange.length !== 2) {
      setError(t("cycles.errorInvalidDateRange"));
      return;
    }
    setIsCreating(true);
    setError(null);
    try {
      const [startDate, endDate] = selectedRange;
      const cycleData = {
        startDate: startDate.toISOString().split("T")[0],
        endDate: endDate.toISOString().split("T")[0],
        state: "OPEN",
      };
      const response = await createCycle(cycleData, locale);
      if (response.success) {
        onCycleCreated();
        toast.success(t("cycles.cycleCreated"), {
          position: "top-right",
          autoClose: 3000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
        });
        if (response.data && response.data.emailWarning) {
          toast.warning(t("cycles.cycleCreatedEmailWarning"), {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
          });
        }
        onClose();
      } else {
        const errorMessage =
          response.error?.message || t("cycles.errorCreateCycle");
        setError(errorMessage);
        toast.error(errorMessage, {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
        });
      }
    } catch (error) {
      const errorMessage = t("cycles.errorCreateCycle");
      setError(errorMessage);
      toast.error(errorMessage, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
      });
      console.error("Error creating cycle:", error);
    } finally {
      setIsCreating(false);
    }
  };

  if (!shouldRender) return null;

  return (
    <div
      className={`cycle-offcanvas-backdrop ${isAnimating ? "open" : ""}`}
      onClick={handleBackdropClick}
    >
      <div className={`cycle-offcanvas ${isAnimating ? "open" : ""}`}>
        <button className="cycle-offcanvas-close" onClick={onClose}>
          <FaTimes />
        </button>

        <div className="cycle-offcanvas-content">
          <h2 className="cycle-offcanvas-title">
            {t("cycles.createNewCycle")}
          </h2>

          <div className="cycle-calendar-container">
            <Calendar
              onChange={handleDateChange}
              value={selectedRange}
              selectRange={true}
              className="cycle-calendar"
              minDate={new Date()}
              locale={locale === "en" ? "en-US" : "pt-PT"} // MUDANÇA: Usar o locale dinamicamente
            />
          </div>

          <div className="cycle-date-info">
            <div className="cycle-date-item">
              <strong>{t("cycles.startDate")}:</strong>{" "}
              {formatDate(selectedRange[0])}
            </div>
            <div className="cycle-date-item">
              <strong>{t("cycles.endDate")}:</strong>{" "}
              {formatDate(selectedRange[1])}
            </div>
          </div>

          <div className="cycle-summary">
            <div className="cycle-summary-left">
              {t("cycles.appraisalsToComplete", { count: activeUsersCount })}
            </div>
            <div className="cycle-summary-right">
              {t("cycles.daysDuration", { days: calculateDays() })}
            </div>
          </div>

          {error && <div className="cycle-error-message">{error}</div>}

          <button
            className="cycle-create-button"
            onClick={handleCreateCycle}
            disabled={isCreating}
          >
            {isCreating ? t("cycles.creating") : t("cycles.createCycle")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default CycleOffcanvas;
