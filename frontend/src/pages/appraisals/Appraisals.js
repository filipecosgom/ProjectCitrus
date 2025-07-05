import "./Appraisals.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useRef } from "react";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import AppraisalCard from "../../components/appraisalCard/AppraisalCard";
import SortControls from "../../components/sortControls/SortControls";
import { useTranslation } from "react-i18next";
import { handleGetAppraisals } from "../../handles/handleGetAppraisals";
import { handleGetAppraisalStates } from "../../handles/handleGetEnums";
import {
  appraisalsSearchTypes,
  appraisalSearchFilters,
  appraisalsSortFields,
  buildAppraisalsSearchParams,
} from "../../utils/appraisalsSearchUtils";
import AppraisalOffCanvas from "../../components/appraisalOffCanvas/AppraisalOffCanvas";
import useAuthStore from "../../stores/useAuthStore";
import handleNotification from "../../handles/handleNotification";
import { GrDocumentVerified } from "react-icons/gr";

export default function Appraisals() {
  const { t } = useTranslation();
  const [appraisals, setAppraisals] = useState([]);
  const [appraisalStates, setAppraisalStates] = useState([]);
  const user = useAuthStore((state) => state.user);
  const isAdmin = user?.userIsAdmin;
  const [resultsLoading, setResultsLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 10,
    total: 0,
  });
  // Separate state for search/filter, sort, and pagination
  const [searchParams, setSearchParams] = useState({
    query: "",
    searchType: "creationDate", // or whatever default
    limit: 10,
    state: "",
    score: "",
  });
  const [sort, setSort] = useState({
    sortBy: "creationDate",
    sortOrder: "DESCENDING",
  });
  const [selectedAppraisals, setSelectedAppraisals] = useState(new Set());
  const [completeAppraisalsOpen, setCompleteAppraisalsOpen] = useState(false);
  const lastSearchRef = useRef(searchParams);

  const handleOpenCompleteAppraisals = () => {
    if (selectedAppraisals.size === 0) {
      handleNotification("info", t("appraisal.noAppraisalsSelected"));
      return;
    }
    setCompleteAppraisalsOpen(true);
  };

  const handleCloseCompleteAppraisals = () => {
    setCompleteAppraisalsOpen(false);
  };

  // Estados para appraisal offcanvas
  const [selectedAppraisal, setSelectedAppraisal] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);

  useEffect(() => {
    lastSearchRef.current = searchParams;
  }, [searchParams]);

  // Fetch appraisals
  async function fetchAppraisals(
    offset = pagination.offset,
    overrideParams = null
  ) {
    // Always ensure appraisingUserId is present for non-admins
    let baseParams = overrideParams || searchParams;
    if (!isAdmin) {
      baseParams = { ...baseParams, appraisingUserId: user?.id };
    }
    const params = buildAppraisalsSearchParams({
      ...baseParams,
      offset,
      sortBy: sort.sortBy,
      sortOrder: sort.sortOrder,
    });
    console.log('Params sent to backend:', params); // Debug log
    setResultsLoading(true);
    const result = await handleGetAppraisals(params);
    setAppraisals(result.appraisals || []);
    setPagination((prev) => ({
      ...prev,
      offset,
      limit: result.pagination?.limit || 10,
      total: result.pagination?.totalAppraisals || 0,
    }));
    setResultsLoading(false);
  }

  // Only one effect for fetching appraisals
  useEffect(() => {
    if (searchParams) {
      fetchAppraisals(pagination.offset, searchParams);
    }
    // eslint-disable-next-line
  }, [searchParams, pagination.offset, sort]);

  // On mount: fetch appraisal states and initial appraisals
  useEffect(() => {
    async function fetchInitial() {
      setPageLoading(true);
      const appraisalStates = await handleGetAppraisalStates();
      setAppraisalStates(appraisalStates);
      // If not admin, set initial search to only appraisals where user is manager
      setSearchParams((prev) =>
        isAdmin ? { ...prev } : { ...prev, appraisingUserId: user?.id }
      );
      setPageLoading(false);
    }
    fetchInitial();
    // eslint-disable-next-line
  }, []);

  // Handlers
  const handleSearch = (query, searchType, limit, filters = {}) => {
    setSearchParams((prev) => {
      const nextParams = {
        ...prev,
        query,
        searchType,
        limit,
        ...filters,
      };
      // Always ensure appraisingUserId is present for non-admins
      return isAdmin ? nextParams : { ...nextParams, appraisingUserId: user?.id };
    });
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  // Handlers para appraisal off canvas
  const handleAppraisalClick = (appraisal) => {
    setSelectedAppraisal(appraisal);
    setOffcanvasOpen(true);
  };

  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);
    setTimeout(() => {
      setSelectedAppraisal(null);
    }, 300);
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  // Handler for selecting/deselecting appraisals
  const handleAppraisalSelection = (appraisalId, isSelected) => {
    const newSelected = new Set(selectedAppraisals);
    if (isSelected) {
      newSelected.add(appraisalId);
    } else {
      newSelected.delete(appraisalId);
    }
    setSelectedAppraisals(newSelected);
  };

  // Clear selection on page change
  useEffect(() => {
    setSelectedAppraisals(new Set());
  }, [pagination.offset]);

  if (pageLoading) return <Spinner />;

  const filtersConfig = appraisalSearchFilters(t, appraisalStates);

  return (
    <div className="appraisals-container">
      <div className="appraisals-searchBarAndButton">
        <SearchBar
          onSearch={handleSearch}
          searchTypes={appraisalsSearchTypes(t, isAdmin)}
          {...filtersConfig}
          actions={
            isAdmin && (
              <button
                className={`complete-appraisals-btn${
                  selectedAppraisals.size === 0 ? " disabled" : ""
                }`}
                onClick={handleOpenCompleteAppraisals}
                disabled={selectedAppraisals.size === 0}
              >
                <GrDocumentVerified className="complete-appraisals-icon" />
                <span className="complete-appraisals-text">
                  {t("appraisal.completeAppraisals")}
                </span>
                <span className="complete-appraisals-count">
                  {selectedAppraisals.size > 0
                    ? ` (${selectedAppraisals.size})`
                    : ""}
                </span>
              </button>
            )
          }
        />
      </div>
      <SortControls
        fields={appraisalsSortFields(t)}
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={handleSortChange}
      />

      {resultsLoading ? (
        <div className="appraisals-loading">
          <Spinner />
        </div>
      ) : appraisals.length === 0 ? (
        <div className="appraisals-empty">
          <p>{t("appraisalsNoResults")}</p>
        </div>
      ) : (
        <div>
          <div className="appraisals-grid">
            {appraisals.map((appraisal) => (
              <AppraisalCard
                key={appraisal.id}
                appraisal={appraisal}
                onClick={handleAppraisalClick}
                showCheckbox={isAdmin}
                isSelected={selectedAppraisals.has(appraisal.id)}
                onSelectionChange={handleAppraisalSelection}
                // Optionally, add onClick if you want card click
              />
            ))}
          </div>
        </div>
      )}
      <AppraisalOffCanvas
        appraisal={selectedAppraisal}
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas}
      />
      <Pagination
        offset={pagination.offset}
        limit={pagination.limit}
        total={pagination.total}
        onChange={handlePageChange}
      />
    </div>
  );
}
