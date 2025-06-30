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
export async function fetchInitialUsers({
  setPageLoading,
  setOffices,
  setSearchParams,
  handleGetOffices,
}) {
  setPageLoading(true);
  let offices = await handleGetOffices();
  // Add "All offices" option at the top
  offices = [{ label: "All offices", value: "" }, ...offices];
  const initialSearch = { query: "", searchType: "email", limit: 10, office: "", filters: {} };
  setSearchParams(initialSearch);
  setOffices(offices);
  setPageLoading(false);
}

// Externalized search filter config for users
export const userSearchTypes = (t) => [
  { value: "email", label: t("searchByEmail") },
  { value: "name", label: t("searchByName") },
  { value: "role", label: t("searchByRole") },
];

export const userSearchFilters = (t, offices) => {
  const isAdmin = useAuthStore.getState().user?.userIsAdmin;
  const filtersConfig = isAdmin ? ["accountState", "office"] : ["office"];
  const filterOptions = {
    office: offices,
  };
  if (isAdmin) {
    filterOptions.accountState = [
      { label: t("searchBarAllStates"), value: "" },
      { label: t("searchBarComplete"), value: "COMPLETE" },
      { label: t("searchBarIncomplete"), value: "INCOMPLETE" },
    ];
  }
  return {
    filtersConfig,
    filterOptions,
    tristateFilters: [
      { key: "isManager", label: t("filterMenuisManager") },
      { key: "isAdmin", label: t("filterMenuisAdmin") },
      { key: "isManaged", label: t("filterMenuisManaged") },
    ],
    defaultValues: {
      query: "",
      searchType: "email",
      accountState: "",
      office: "",
      limit: 10,
      isManager: null,
      isAdmin: null,
      isManaged: null,
    },
    searchTypes: userSearchTypes(t),
  };
};

export const usersSortFields = [
  { id: "userSortControlsName", key: "name" },
  { id: "userSortControlsRole", key: "role" },
  { id: "userSortControlsOffice", key: "office" },
  { id: "userSortControlsManager", key: "manager" },
];
