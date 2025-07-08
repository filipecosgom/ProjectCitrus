// Generic search/pagination/sort utilities for all entity types

// Utility for building search params
export function buildSearchParams(query, searchType, limit, filters = {}) {
  return { query, searchType, limit, filters };
}

// Pagination handler factory (with scroll-to-top)
export function createPageChangeHandler(setPagination) {
  return (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
    setTimeout(() => {
      setTimeout(() => {
        window.scrollTo({ top: 0, behavior: "smooth" });
      }, 250); // Wait for results to render
    }, 0);
  };
}

// Sorting logic handler (with scroll-to-top)
export function createSortHandler(setSort, setPagination, lastSearchRef) {
  return (newSort) => {
    setSort(newSort);
    if (lastSearchRef.current) {
      setPagination((prev) => ({ ...prev, offset: 0 }));
      setTimeout(() => {
        setTimeout(() => {
          window.scrollTo({ top: 0, behavior: "smooth" });
        }, 250); // Wait for results to render
      }, 0);
    }
  };
}
