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

export default function Appraisals() {
  const { t } = useTranslation();
  const [appraisals, setAppraisals] = useState([]);
  const [appraisalStates, setAppraisalStates] = useState([]);
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
  const lastSearchRef = useRef(searchParams);
  useEffect(() => {
    lastSearchRef.current = searchParams;
  }, [searchParams]);

  // Fetch appraisals
  async function fetchAppraisals(
    offset = pagination.offset,
    overrideParams = null
  ) {
    const params = buildAppraisalsSearchParams({
      ...(overrideParams || searchParams),
      offset,
      sortBy: sort.sortBy,
      sortOrder: sort.sortOrder,
    });
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
      setSearchParams((prev) => ({ ...prev })); // triggers initial fetch
      setPageLoading(false);
    }
    fetchInitial();
    // eslint-disable-next-line
  }, []);

  // Handlers
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

  if (pageLoading) return <Spinner />;

  const filtersConfig = appraisalSearchFilters(t, appraisalStates);

  return (
    <div className="appraisals-container">
      <SearchBar
        onSearch={handleSearch}
        searchTypes={appraisalsSearchTypes}
        {...filtersConfig}
      />
      <SortControls
        fields={appraisalsSortFields}
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
              />
            ))}
          </div>
        </div>
      )}
      <Pagination
        offset={pagination.offset}
        limit={pagination.limit}
        total={pagination.total}
        onChange={handlePageChange}
      />
    </div>
  );
}
