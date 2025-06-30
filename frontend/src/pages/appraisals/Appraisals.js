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
  createPageChangeHandler,
  createSortHandler,
  fetchInitialAppraisals,
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
  // Use null as initial value for searchParams
  const [searchParams, setSearchParams] = useState(null);
  const [lastSearch, setLastSearch] = useState(null);
  const [sort, setSort] = useState({
    sortBy: "creationDate",
    sortOrder: "DESCENDING",
  });
  const lastSearchRef = useRef(lastSearch);
  useEffect(() => {
    lastSearchRef.current = lastSearch;
  }, [lastSearch]);

  // Externalized: set searching parameters
  const setSearchingParameters = (query, searchType, limit, filters = {}) => {
    const search = { query, searchType, limit, ...filters };
    setLastSearch(search);
    setSearchParams(search);
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  // Externalized: fetch appraisals
  async function fetchAppraisals(offset = 0, overrideParams = null) {
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
    setLastSearch(overrideParams || searchParams);
  }

  // Externalized: handle page change
  const handlePageChange = createPageChangeHandler(
    setPagination,
    fetchAppraisals,
    lastSearchRef
  );

  // Externalized: handle sort change
  const handleSortChange = createSortHandler(
    setSort,
    setPagination,
    fetchAppraisals,
    lastSearchRef
  );

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
      // Build initial search params
      const initialSearch = {
        query: "",
        searchType: "creatioDate",
        limit: 10,
        state: "",
      };
      setSearchParams(initialSearch);
      setLastSearch(initialSearch);
      // Fetch initial appraisals
      const params = buildAppraisalsSearchParams({
        ...initialSearch,
        offset: 0,
        sortBy: sort.sortBy,
        sortOrder: sort.sortOrder,
      });
      const result = await handleGetAppraisals(params);
      setAppraisals(result.appraisals || []);
      setPagination((prev) => ({
        ...prev,
        offset: 0,
        limit: result.pagination?.limit || 10,
        total: result.pagination?.totalAppraisals || 0,
      }));
      setPageLoading(false);
    }
    fetchInitial();
  }, []);

  if (pageLoading) return <Spinner />;

  const filtersConfig = appraisalSearchFilters(t, appraisalStates);

  return (
    <div className="appraisals-container">
      <SearchBar
        onSearch={setSearchingParameters}
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
