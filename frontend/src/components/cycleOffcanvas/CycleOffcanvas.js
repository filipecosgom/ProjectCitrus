import React, { useState, useEffect } from "react";
import { FaTimes } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import Calendar from "react-calendar";
import { createCycle, fetchActiveUsersCount } from "../../api/cyclesApi";
import useLocaleStore from "../../stores/useLocaleStore";
import "react-calendar/dist/Calendar.css";
import "./CycleOffcanvas.css";

const CycleOffcanvas = ({ isOpen, onClose, onCycleCreated }) => {
  const { t } = useTranslation();
  const locale = useLocaleStore((state) => state.locale); // Adicionar esta linha
  const [selectedRange, setSelectedRange] = useState([new Date(), new Date()]);
  const [activeUsersCount, setActiveUsersCount] = useState(0);
  const [shouldRender, setShouldRender] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [error, setError] = useState(null);

  // Controlar renderização e animação
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

  // Buscar contagem de usuários ativos
  useEffect(() => {
    if (isOpen) {
      loadActiveUsersCount();
    }
  }, [isOpen]);

  // Controlar scroll da página
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

  // Fechar com ESC
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

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleDateChange = (value) => {
    if (Array.isArray(value)) {
      setSelectedRange(value);
    } else {
      setSelectedRange([value, value]);
    }
  };

  const calculateDays = () => {
    if (!selectedRange || selectedRange.length !== 2) return 0;
    const [start, end] = selectedRange;

    // Se for a mesma data, retorna 1 dia
    if (start.getTime() === end.getTime()) {
      return 1;
    }

    // Calcula a diferença em milissegundos
    const diffTime = end.getTime() - start.getTime();
    // Converte para dias e adiciona 1 para incluir o primeiro dia
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;

    return diffDays;
  };

  const formatDate = (date) => {
    // MUDANÇA: Usar o locale do store em vez de hardcoded "pt-PT"
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

      const response = await createCycle(cycleData);

      if (response.success) {
        onCycleCreated();
        onClose();
      } else {
        setError(response.error?.message || t("cycles.errorCreateCycle"));
      }
    } catch (error) {
      setError(t("cycles.errorCreateCycle"));
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
