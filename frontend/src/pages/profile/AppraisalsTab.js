import React, { useState, useEffect, useRef } from "react";
import useUserProfile from "../../hooks/useUserProfile";
import "./AppraisalsTab.css";
import { useLocation } from "react-router-dom";
import Spinner from "../../components/spinner/spinner";
import AppraisalCard from "../../components/appraisalCard/AppraisalCard";
import SortControls from "../../components/sortControls/SortControls";
import Pagination from "../../components/pagination/Pagination";
import SearchBar from "../../components/searchbar/Searchbar";
import { useTranslation } from "react-i18next";
import { handleGetAppraisals } from "../../handles/handleGetAppraisals";
import { handleGetAppraisalStates } from "../../handles/handleGetEnums";
import {
  appraisalSearchFilters,
  appraisalsSortFields,
  buildAppraisalsSearchParams,
} from "../../utils/appraisalsSearchUtils";
import AppraisalOffCanvas from "../../components/appraisalOffCanvas/AppraisalOffCanvas";
import handleNotification from "../../handles/handleNotification";

// ...existing imports...

function AppraisalsTab({ userId: userIdProp }) {
  const { t } = useTranslation();
  // Get userId from prop or from query string (?id=...)
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const userIdQuery = queryParams.get("id");
  const userId = userIdProp || userIdQuery;
  const { user, loading } = useUserProfile(userId);
  const [appraisals, setAppraisals] = useState([]);
  const [filteredAppraisals, setFilteredAppraisals] = useState([]);
  const [appraisalStates, setAppraisalStates] = useState([]);
  const [resultsLoading, setResultsLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 10,
    total: 0,
  });
  const [searchParams, setSearchParams] = useState({
    query: "",
    searchType: "creationDate",
    limit: 10,
    state: "CLOSED",
    score: "",
    appraisedUserId: user?.id,
  });
  const [sort, setSort] = useState({
    sortBy: "creationDate",
    sortOrder: "DESCENDING",
  });
  const lastSearchRef = useRef(searchParams);
  // Estados para appraisal offcanvas
  const [selectedAppraisal, setSelectedAppraisal] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);

  useEffect(() => {
    lastSearchRef.current = searchParams;
  }, [searchParams]);

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

  // Apply search, filter, sort, and pagination whenever dependencies change
  useEffect(() => {
    let data = [...appraisals];
    // Search/filter logic
    if (searchParams.query) {
      data = data.filter((a) =>
        (a[searchParams.searchType] || "")
          .toLowerCase()
          .includes(searchParams.query.toLowerCase())
      );
    }
    if (searchParams.state) {
      data = data.filter((a) => a.state === searchParams.state);
    }
    if (searchParams.score) {
      data = data.filter((a) => String(a.score) === String(searchParams.score));
    }

    // Sort logic
    if (sort.sortBy) {
      data.sort((a, b) => {
        const aValue = a[sort.sortBy];
        const bValue = b[sort.sortBy];
        // Convert both values to strings for localeCompare, fallback to empty string
        const aStr =
          aValue === undefined || aValue === null ? "" : String(aValue);
        const bStr =
          bValue === undefined || bValue === null ? "" : String(bValue);
        if (sort.sortOrder === "DESCENDING") {
          return bStr.localeCompare(aStr);
        } else {
          return aStr.localeCompare(bStr);
        }
      });
    }

    // Pagination logic
    const start = pagination.offset;
    const end = start + pagination.limit;
    setFilteredAppraisals(data.slice(start, end));
    setPagination((prev) => ({
      ...prev,
      total: data.length,
    }));
  }, [appraisals, searchParams, sort, pagination.offset, pagination.limit]);

  async function fetchAppraisalsStates() {
    const appraisalStates = await handleGetAppraisalStates();
    setAppraisalStates(appraisalStates);
  }

  useEffect(() => {
    if (user?.evaluationsReceived) {
      setPageLoading(true);
      fetchAppraisalsStates();
      setAppraisals(user.evaluationsReceived);
      setPagination((prev) => ({
        ...prev,
        offset: 0,
        total: user.evaluationsReceived.length,
      }));
      setPageLoading(false);
    } else {
      setAppraisals([]);
      setFilteredAppraisals([]);
      setPagination((prev) => ({ ...prev, offset: 0, total: 0 }));
    }
  }, [user]);

  const handleSearch = (query, searchType, limit, filters = {}) => {
    setSearchParams((prev) => ({
      ...prev,
      query,
      searchType,
      limit,
      ...filters,
    }));
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  // PDF export handler
  const handleGetPdf = async () => {
    let params = buildAppraisalsSearchParams({
      ...searchParams,
      sortBy: sort.sortBy,
      sortOrder: sort.sortOrder,
      appraisedUserId: user?.id, // always lock to profile user
    });
    // Remove pagination params
    delete params.limit;
    delete params.offset;
    const { handleGeneratePdfOfAppraisals } = await import(
      "../../handles/handleGeneratePdfOfAppraisals"
    );
    const result = await handleGeneratePdfOfAppraisals(params);
    if (result.success) {
      const url = window.URL.createObjectURL(
        new Blob([result.blob], { type: "application/pdf" })
      );
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "appraisals.pdf");
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } else {
      handleNotification("error", "appraisalsPdfExportError");
    }
  };

  if (pageLoading || loading) return <Spinner />;

  const filtersConfig = appraisalSearchFilters(t, appraisalStates);

  return (
    <div className="appraisalsTab-container">
      <div className="appraisalsTab-searchBarAndButton">
        {/* No actions button for profile tab */}
        <SearchBar
          onSearch={handleSearch}
          searchTypes={[
            {
              value: "appraisedUserName",
              label: t("appraisalsSearchTypeUserName"),
            },
            {
              value: "appraisedUserEmail",
              label: t("appraisalsSearchTypeUserEmail"),
            },
          ]}
          {...filtersConfig}
          onExportPdf={handleGetPdf}
        />
      </div>
      <SortControls
        fields={appraisalsSortFields(t)}
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={handleSortChange}
      />
      {resultsLoading ? (
        <div className="appraisalsTab-loading">
          <Spinner />
        </div>
      ) : filteredAppraisals.length === 0 ? (
        <div className="appraisalsTab-empty">
          <p>{t("appraisalsNoResults")}</p>
        </div>
      ) : (
        <div>
          <div className="appraisalsTab-grid">
            {filteredAppraisals.map((appraisal) => (
              <AppraisalCard
                key={appraisal.id}
                appraisal={appraisal}
                onClick={handleAppraisalClick}
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

export default AppraisalsTab;
