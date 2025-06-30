import useAuthStore from "../stores/useAuthStore";

// Utility for building search params
export function buildSearchParams(query, searchType, limit, filters = {}) {
  return { query, searchType, limit, filters };
}

// Pagination handler factory
export function createPageChangeHandler(setPagination) {
  return (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };
}

// Sorting logic handler
export function createSortHandler(setSort, setPagination, lastSearchRef) {
  return (newSort) => {
    setSort(newSort);
    if (lastSearchRef.current) {
      setPagination((prev) => ({ ...prev, offset: 0 }));
    }
  };
}

// Initial data fetching logic
export async function fetchInitialAppraisals({
  setPageLoading,
  setAppraisalStates,
  setSearchParams,
  setLastSearch,
  setAppraisals,
  setPagination,
  sort,
  handleGetAppraisalStates,
  handleGetAppraisals,
}) {
  setPageLoading(true);
  let appraisalStates = await handleGetAppraisalStates();
  // Ensure all states option is present and value is null or ""
  if (!appraisalStates.some(s => s.value === "" || s.value === null)) {
    appraisalStates = [{ label: "All states", value: "" }, ...appraisalStates];
  }
  const initialSearch = { query: "", searchType: "appraisedUserName", limit: 10, filters: {} };
  setSearchParams(initialSearch);
  setLastSearch(initialSearch);
  const result = await handleGetAppraisals({
    [initialSearch.searchType]: initialSearch.query,
    offset: 0,
    limit: initialSearch.limit,
    ...initialSearch.filters,
    parameter: sort.sortBy,
    order: sort.sortOrder,
  });
  setAppraisals(result.appraisals);
  setPagination((prev) => ({
    ...prev,
    offset: 0,
    limit: result.pagination.limit,
    total: result.pagination.totalUsers,
  }));
  setAppraisalStates(appraisalStates);
  setPageLoading(false);
}

// Search types for appraisals
export const appraisalsSearchTypes = [
  { value: "appraisedUserName", label: "appraisalsSearchTypeUserName" },
  { value: "appraisedUserEmail", label: "appraisalsSearchTypeUserEmail" },
  { value: "managerName", label: "appraisalsSearchTypeManagerName" },
  { value: "managerEmail", label: "appraisalsSearchTypeManagerEmail" },
];

// Build params for backend
const sortKeyMap = {
  user: "appraised_name",
  score: "score",
  manager: "manager_name",
  endDate: "endDate",
  state: "state"
};

export function buildAppraisalsSearchParams({ query, searchType, limit, offset, state, score, sortBy, sortOrder }) {
  const params = {
    limit: limit ?? 10,
    offset: offset ?? 0,
    parameter: sortKeyMap[sortBy] || sortBy,
    order: sortOrder,  // Rename if needed
  };
  // Map frontend searchType to backend param names
  if (query && searchType) {
  params[searchType] = query;
}
  // Map state and score if needed
  if (state) {
    params.state = state;
  }
  if (score !== undefined && score !== "") {
    params.score = score;
  }
  return params;
}

// Externalized search filter config for appraisals
export const appraisalSearchFilters = (t, appraisalStates) => {
  let stateOptions = appraisalStates;
  if (!stateOptions || !Array.isArray(stateOptions) || !stateOptions.length) {
    stateOptions = [
      { value: "", label: t("appraisalStateAllStates") },
      { value: "IN_PROGRESS", label: t("appraisalStateInProgress") },
      { value: "COMPLETED", label: t("appraisalStateCompleted") },
      { value: "CLOSED", label: t("appraisalStateClosed") },
    ];
  }
  // If stateOptions are strings, map them to {label, value}
  if (typeof stateOptions[0] === "string") {
    stateOptions = [
      { value: "", label: t("appraisalStateAllStates") },
      ...stateOptions.map(s => ({
        value: s,
        label: t(
          s === "IN_PROGRESS"
            ? "appraisalStateInProgress"
            : s === "COMPLETED"
            ? "appraisalStateCompleted"
            : s === "CLOSED"
            ? "appraisalStateClosed"
            : s
        ),
      })),
    ];
  }
  const filterOptions = {
    state: stateOptions,
    score: [
      { value: "", label: t("filterMenuAllScores") },
      { value: 0, label: "0 ⭐" },
      { value: 1, label: "1 ⭐" },
      { value: 2, label: "2 ⭐" },
      { value: 3, label: "3 ⭐" },
      { value: 4, label: "4 ⭐" },
    ],
  };
  return {
    filtersConfig: ["state", "score"],
    filterOptions,
    defaultValues: {
      query: "",
      searchType: "appraisedUserName",
      state: "",
      score: "",
      limit: 10,
    },
  };
};

export const appraisalsSortFields = [
  { id: "appraisalSortControlsUser", key: "user" },
  { id: "appraisalSortControlsScore", key: "score" },
  { id: "appraisalSortControlsManager", key: "manager" },
  { id: "appraisalSortControlsEndDate", key: "endDate" },
  { id: "appraisalSortControlsState", key: "state" }
];
