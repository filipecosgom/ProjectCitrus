// Course search/filter/sort utility functions for frontend

// Initial data fetching logic for courses
export async function fetchInitialCourses({
  setPageLoading,
  setAreas,
  setSearchParams,
  handleGetCourseAreas,
}) {
  setPageLoading(true);
  let areas = await handleGetCourseAreas();
  // Add "All areas" option at the top
  areas = [{ label: "All areas", value: "" }, ...areas];
  const initialSearch = { query: "", searchType: "title", limit: 10, area: "", filters: {} };
  setSearchParams(initialSearch);
  setAreas(areas);
  setPageLoading(false);
}

// Externalized search types for courses
export const courseSearchTypes = (t) => [
  { value: "title", label: t("courses.searchByTitle") },
  { value: "description", label: t("courses.searchByDescription") },
  { value: "area", label: t("courses.searchByArea") },
];

// Externalized filter config for courses
export const courseSearchFilters = (t, areas) => {
  const filtersConfig = ["area", "language"];
  const filterOptions = {
    area: areas,
    language: [
      { label: t("courses.filterAllLanguages"), value: "" },
      { label: t("courses.filterPortuguese"), value: "pt" },
      { label: t("courses.filterEnglish"), value: "en" },
    ],
  };
  return {
    filtersConfig,
    filterOptions,
    defaultValues: {
      query: "",
      searchType: "title",
      area: "",
      language: "",
      limit: 10,
    },
    searchTypes: courseSearchTypes(t),
  };
};

export const coursesSortFields = [
  { id: "courseSortControlsTitle", key: "title", label: (t) => t("courses.sortTitle") },
  { id: "courseSortControlsArea", key: "area", label: (t) => t("courses.sortArea") },
  { id: "courseSortControlsDuration", key: "duration", label: (t) => t("courses.sortDuration") },
  { id: "courseSortControlsLanguage", key: "language", label: (t) => t("courses.sortLanguage") }
];
