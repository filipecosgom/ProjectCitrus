import useAuthStore from "../stores/useAuthStore";

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
  t,
}) {
  setPageLoading(true);
  let appraisalStates = await handleGetAppraisalStates();
  // Ensure all states option is present and value is null or ""
  if (!appraisalStates.some((s) => s.value === "" || s.value === null)) {
    appraisalStates = [
      { label: t("appraisalStateAllStates"), value: "" },
      ...appraisalStates,
    ];
  }
  const initialSearch = {
    query: "",
    searchType: "appraisedUserName",
    limit: 10,
    filters: {},
  };
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

// Search types for appraisals (internationalized)
export const appraisalsSearchTypes = (t, isAdmin = false) => {
  const types = [
    { value: "appraisedUserName", label: t("appraisalsSearchTypeUserName") },
    { value: "appraisedUserEmail", label: t("appraisalsSearchTypeUserEmail") },
    {
      value: "appraisingUserName",
      label: t("appraisalsSearchTypeManagerName"),
    },
    {
      value: "appraisingUserEmail",
      label: t("appraisalsSearchTypeManagerEmail"),
    },
  ];
  if (isAdmin) return types;
  // Only allow user name/email for non-admins
  return types.filter(
    (type) =>
      type.value !== "appraisingUserName" &&
      type.value !== "appraisingUserEmail"
  );
};

// Build params for backend
const sortKeyMap = {
  user: "appraised_name",
  score: "score",
  manager: "manager_name",
  endDate: "endDate",
  state: "state",
};

export function buildAppraisalsSearchParams({
  query,
  searchType,
  limit,
  offset,
  state,
  score,
  sortBy,
  sortOrder,
  appraisingUserId,
  appraisedUserId,
}) {
  const params = {
    limit: limit ?? 10,
    offset: offset ?? 0,
    parameter: sortKeyMap[sortBy] || sortBy,
    order: sortOrder, // Rename if needed
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
  // Always include appraisingUserId if present (for non-admins)
  if (appraisingUserId) {
    params.appraisingUserId = appraisingUserId;
  }
  // Always include appraisedUserId if present
  if (appraisedUserId) {
    params.appraisedUserId = appraisedUserId;
  }
  return params;
}

// Externalized search filter config for appraisals (internationalized)
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
      ...stateOptions.map((s) => ({
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
  };
  return {
    filtersConfig: ["state"],
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

// Appraisals sort fields (internationalized)
export const appraisalsSortFields = (t) => [
  {
    id: "appraisalSortControlsUser",
    key: "user",
    label: (t) => t("appraisalSortControlsUser"),
  },
  {
    id: "appraisalSortControlsScore",
    key: "score",
    label: (t) => t("appraisalSortControlsScore"),
  },
  {
    id: "appraisalSortControlsManager",
    key: "manager",
    label: (t) => t("appraisalSortControlsManager"),
  },
  {
    id: "appraisalSortControlsEndDate",
    key: "endDate",
    label: (t) => t("appraisalSortControlsEndDate"),
  },
  {
    id: "appraisalSortControlsState",
    key: "state",
    label: (t) => t("appraisalSortControlsState"),
  },
];
