// Utility for building search params
export function buildSearchParams(query, searchType, limit, filters = {}) {
  return { query, searchType, limit, filters };
}

// Pagination handler factory
export function createPageChangeHandler(setPagination, fetchFn, lastSearchRef) {
  return (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
    if (lastSearchRef.current) {
      fetchFn(newOffset, lastSearchRef.current);
    }
  };
}

// Sorting logic handler
export function createSortHandler(setSort, setPagination, fetchFn, lastSearchRef) {
  return (newSort) => {
    setSort(newSort);
    if (lastSearchRef.current) {
      fetchFn(0, lastSearchRef.current);
      setPagination((prev) => ({ ...prev, offset: 0 }));
    }
  };
}

// Initial data fetching logic
export async function fetchInitialUsers({
  setPageLoading,
  setOffices,
  setSearchParams,
  setLastSearch,
  setUsers,
  setPagination,
  sort,
  handleGetOffices,
  handleGetUsers,
}) {
  setPageLoading(true);
  const offices = await handleGetOffices();
  const initialSearch = { query: "", searchType: "email", limit: 10, filters: {} };
  setSearchParams(initialSearch);
  setLastSearch(initialSearch);
  const result = await handleGetUsers({
    [initialSearch.searchType]: initialSearch.query,
    offset: 0,
    limit: initialSearch.limit,
    ...initialSearch.filters,
    parameter: sort.sortBy,
    order: sort.sortOrder,
  });
  setUsers(result.users);
  setPagination((prev) => ({
    ...prev,
    offset: 0,
    limit: result.pagination.limit,
    total: result.pagination.totalUsers,
  }));
  setOffices(offices);
  setPageLoading(false);
}
