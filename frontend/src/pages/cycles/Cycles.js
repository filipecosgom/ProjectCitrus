import React, { useState, useEffect } from "react";
import { IoCalendar, IoAdd } from "react-icons/io5";
import { useTranslation } from "react-i18next";
import { fetchCycles, closeCycle } from "../../api/cyclesApi";
import CycleOffcanvas from "../../components/cycleOffcanvas/CycleOffcanvas";
import CycleCard from "../../components/cycleCard/cycleCard";
import "./Cycles.css";

const Cycles = () => {
  const { t } = useTranslation();
  const [cycles, setCycles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isOffcanvasOpen, setIsOffcanvasOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalCycles, setTotalCycles] = useState(0);
  const [filters, setFilters] = useState({
    state: null,
    adminId: null,
    startDateFrom: null,
    startDateTo: null,
  });

  const cyclesPerPage = 6;

  useEffect(() => {
    loadCycles();
  }, [currentPage, filters]);

  const loadCycles = async () => {
    try {
      setLoading(true);
      setError(null);

      const params = {
        limit: cyclesPerPage,
        offset: currentPage * cyclesPerPage,
        ...filters,
      };

      const response = await fetchCycles(params);

      if (response.success) {
        const cyclesData = response.data.data?.cycles || [];
        const totalCount = response.data.data?.totalCycles || 0;
        setCycles(cyclesData);
        setTotalCycles(totalCount);
      } else {
        setError(response.error?.message || t("cycles.errorLoadingCycles"));
      }
    } catch (err) {
      setError(t("cycles.errorLoadingCycles"));
      console.error("Error loading cycles:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleCloseCycle = async (cycleId) => {
    if (!window.confirm(t("cycles.confirmCloseCycle"))) {
      return;
    }

    try {
      const response = await closeCycle(cycleId);

      if (response.success) {
        loadCycles();
        alert(t("cycles.cycleClosedSuccess"));
      } else {
        alert(response.error?.message || t("cycles.errorClosingCycle"));
      }
    } catch (err) {
      alert(t("cycles.errorClosingCycle"));
      console.error("Error closing cycle:", err);
    }
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handleCycleCreated = () => {
    setCurrentPage(0);
    loadCycles();
  };

  const totalPages = Math.ceil(totalCycles / cyclesPerPage);

  if (loading) {
    return (
      <div className="cycles-page">
        <div className="cycles-header">
          <div className="cycles-title">
            <IoCalendar className="cycles-icon" />
            <h1>{t("cycles.title")}</h1>
          </div>
        </div>
        <div className="cycles-content">
          <div className="cycles-loading">{t("cycles.loading")}</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="cycles-page">
        <div className="cycles-header">
          <div className="cycles-title">
            <IoCalendar className="cycles-icon" />
            <h1>{t("cycles.title")}</h1>
          </div>
        </div>
        <div className="cycles-content">
          <div className="cycles-error">
            {error}
            <button onClick={loadCycles} className="cycles-retry-button">
              {t("cycles.tryAgain")}
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="cycles-page">
      <div className="cycles-header">
        <div className="cycles-title">
          <IoCalendar className="cycles-icon" />
          <h1>{t("cycles.title")}</h1>
        </div>
        <button
          className="btn-create-cycle"
          onClick={() => setIsOffcanvasOpen(true)}
        >
          <IoAdd /> {t("cycles.createNewCycle")}
        </button>
      </div>

      <div className="cycles-content">
        {cycles.length === 0 ? (
          <div className="cycles-empty">
            <p>{t("cycles.noCyclesFound")}</p>
          </div>
        ) : (
          <>
            <div className="cycles-grid">
              {cycles.map((cycle) => (
                <CycleCard
                  key={cycle.id}
                  cycle={cycle}
                  onCloseCycle={handleCloseCycle}
                />
              ))}
            </div>

            {totalPages > 1 && (
              <div className="cycles-pagination">
                <button
                  className="cycles-pagination-button"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 0}
                >
                  {t("cycles.previous")}
                </button>

                <div className="cycles-pagination-info">
                  {t("cycles.pageInfo", {
                    current: currentPage + 1,
                    total: totalPages,
                  })}
                </div>

                <button
                  className="cycles-pagination-button"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage >= totalPages - 1}
                >
                  {t("cycles.next")}
                </button>
              </div>
            )}
          </>
        )}
      </div>

      <CycleOffcanvas
        isOpen={isOffcanvasOpen}
        onClose={() => setIsOffcanvasOpen(false)}
        onCycleCreated={handleCycleCreated}
      />
    </div>
  );
};

export default Cycles;
