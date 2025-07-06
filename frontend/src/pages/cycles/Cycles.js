import React, { useState, useEffect } from "react";
import { IoCalendar, IoAdd } from "react-icons/io5";
import { useTranslation } from "react-i18next";
import { fetchCycles, closeCycle } from "../../api/cyclesApi";
import CycleOffcanvas from "../../components/cycleOffcanvas/CycleOffcanvas";
import CycleDetailsOffcanvas from "../../components/cycleDetailsOffcanvas/CycleDetailsOffcanvas";
import CycleCard from "../../components/cycleCard/CycleCard";
import ConfirmModal from "../../components/confirmModal/ConfirmModal";
import {
  showSuccessToast,
  showErrorToast,
} from "../../utils/toastConfig/toastConfig";
import "./Cycles.css";

const Cycles = () => {
  const { t } = useTranslation();
  const [cycles, setCycles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isOffcanvasOpen, setIsOffcanvasOpen] = useState(false);
  const [isDetailsOffcanvasOpen, setIsDetailsOffcanvasOpen] = useState(false);
  const [selectedCycle, setSelectedCycle] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalCycles, setTotalCycles] = useState(0);
  const [filters, setFilters] = useState({
    state: null,
    adminId: null,
    startDateFrom: null,
    startDateTo: null,
  });

  // Estados para o modal de confirmação
  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);
  const [cycleToClose, setCycleToClose] = useState(null);

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

  const handleCloseCycle = (cycleId) => {
    setCycleToClose(cycleId);
    setIsConfirmModalOpen(true);
  };

  const handleCardClick = (cycle) => {
    setSelectedCycle(cycle);
    setIsDetailsOffcanvasOpen(true);
  };

  const confirmCloseCycle = async () => {
    if (!cycleToClose) return;

    const cycleData = cycles.find((cycle) => cycle.id === cycleToClose);

    try {
      const response = await closeCycle(cycleToClose);

      if (response.success) {
        loadCycles();
        showSuccessToast(
          t("cycles.cycleClosedSuccessToast", {
            id: cycleData?.id || cycleToClose,
            startDate: cycleData?.startDate
              ? new Date(cycleData.startDate).toLocaleDateString("pt-PT")
              : "",
            endDate: cycleData?.endDate
              ? new Date(cycleData.endDate).toLocaleDateString("pt-PT")
              : "",
          })
        );
      } else {
        showErrorToast(
          response.error?.message || t("cycles.errorClosingCycle")
        );
      }
    } catch (err) {
      showErrorToast(t("cycles.errorClosingCycle"));
      console.error("Error closing cycle:", err);
    } finally {
      setCycleToClose(null);
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
                  onCardClick={handleCardClick}
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

      <CycleDetailsOffcanvas
        isOpen={isDetailsOffcanvasOpen}
        onClose={() => {
          setIsDetailsOffcanvasOpen(false);
          setSelectedCycle(null);
        }}
        cycle={selectedCycle}
      />

      <ConfirmModal
        isOpen={isConfirmModalOpen}
        onClose={() => {
          setIsConfirmModalOpen(false);
          setCycleToClose(null);
        }}
        onConfirm={confirmCloseCycle}
        title={t("cycles.confirmCloseCycleTitle")}
        message={t("cycles.confirmCloseCycle")}
        confirmText={t("cycles.closeCycle")}
        cancelText={t("common.cancel")}
        variant="danger"
      />
    </div>
  );
};

export default Cycles;
