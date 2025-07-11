import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useSearchParams } from "react-router-dom";
import SearchBar from "../../components/searchbar/Searchbar";
import CourseCard from "../../components/courseCard/CourseCard";
import CourseDetailsOffcanvas from "../../components/courseDetailsOffcanvas/CourseDetailsOffcanvas";
import CourseNewOffCanvas from "../../components/courseNewOffCanvas/CourseNewOffCanvas";
import { handleGetCourseAreas } from "../../handles/handleGetEnums";
import {
  fetchInitialCourses,
  courseSearchFilters,
  coursesSortFields,
} from "../../utils/coursesSearchUtils";
import "./Courses.css";
import { handleGetCourses } from "../../handles/handleGetCourses";
import Spinner from "../../components/spinner/spinner";
import SortControls from "../../components/sortControls/SortControls";
import Pagination from "../../components/pagination/Pagination";
import useAuthStore from "../../stores/useAuthStore";
import { FaBookMedical } from "react-icons/fa";

const Courses = () => {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams(); // Use URL for search params
  const [courses, setCourses] = useState([]);
  const [areas, setAreas] = useState([]);
  const [resultsLoading, setResultsLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);

  const [selectedCourse, setSelectedCourse] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);
  const [newCourseOffcanvasOpen, setNewCourseOffcanvasOpen] = useState(false);

  const [searchParamsState, setSearchParamsState] = useState(null);
  const [lastSearch, setLastSearch] = useState(null);
  const lastSearchRef = useRef(lastSearch);
  const coursesFilters = courseSearchFilters(t, areas);
  const isAdmin = useAuthStore((state) => state.user?.userIsAdmin);

  // NOVO: Função para abrir offcanvas
  const handleViewDetails = (course) => {
    setSelectedCourse(course);
    setOffcanvasOpen(true);
  };

  // NOVO: Função para fechar offcanvas
  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);
    setTimeout(() => {
      setSelectedCourse(null);
    }, 300);
  };

  const handleOpenNewCourseOffCanvas = () => {
    setNewCourseOffcanvasOpen(true);
  };

  const handleCloseNewCourseOffCanvas = () => {
    setNewCourseOffcanvasOpen(false);
  };

  // Helper: parse query params to state (for initial load)
  const parseParams = () => {
    const paramsObj = Object.fromEntries([...searchParams.entries()]);
    // Tristate logic for courseIsActive
    if (paramsObj.courseIsActive === undefined) paramsObj.courseIsActive = null;
    else if (paramsObj.courseIsActive === "true") paramsObj.courseIsActive = true;
    else if (paramsObj.courseIsActive === "false") paramsObj.courseIsActive = false;
    if (paramsObj.limit) paramsObj.limit = Number(paramsObj.limit);
    if (paramsObj.offset) paramsObj.offset = Number(paramsObj.offset);
    // Default searchType to 'title' if not present
    if (!paramsObj.searchType) paramsObj.searchType = "title";
    return { ...coursesFilters.defaultValues, ...paramsObj };
  };

  // Always derive filter state from URL
  const urlState = parseParams();
  const [sort, setSort] = useState({
    sortBy: urlState.sortBy,
    sortOrder: urlState.sortOrder,
  });
  const [pagination, setPagination] = useState({
    offset: urlState.offset,
    limit: urlState.limit,
    total: 0,
  });

  // When filters/search/sort/page change, update URL
  const updateUrlParams = (params) => {
    setSearchParams(params, { replace: true });
  };

  // Update setSearchingParameters to default searchType to 'title' if not provided
  const setSearchingParameters = async (
    query,
    searchType,
    limit,
    filters = {}
  ) => {
    const params = {
      query,
      searchType: searchType || "title",
      limit,
      ...filters,
    };
    // Only include courseIsActive in the URL if not null
    const urlParams = {};
    Object.entries(params).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== "") {
        urlParams[key] = typeof value === "boolean" ? String(value) : value;
      }
    });
    setSearchParams(urlParams);
    setPagination((prev) => ({ ...prev, offset: 0, limit }));
  };

  // Externalized: handle page change
  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  // Externalized: handle sort change
  const handleSortChange = ({ sortBy, sortOrder }) => {
    setSort({ sortBy, sortOrder });
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  // Fetch courses when searchParams, pagination, or sort changes
  useEffect(() => {
    if (!searchParams) return;
    // Parse params from URL and ensure correct types
    const paramsObj = Object.fromEntries([...searchParams.entries()]);
    if (paramsObj.courseIsActive === "true") paramsObj.courseIsActive = true;
    else if (paramsObj.courseIsActive === "false") paramsObj.courseIsActive = false;
    if (paramsObj.limit) paramsObj.limit = Number(paramsObj.limit);
    if (paramsObj.offset) paramsObj.offset = Number(paramsObj.offset);
    // Remove any 'filters' key if present (defensive)
    if (paramsObj.filters) delete paramsObj.filters;
    // Always pass searchType and query explicitly
    fetchCourses(pagination.offset, {
      ...paramsObj,
      searchType: paramsObj.searchType,
      query: paramsObj.query,
    });
  }, [searchParams, pagination.offset, sort]);

  // Sync searchParamsState with URL state
  useEffect(() => {
    setSearchParamsState(parseParams());
  }, [searchParams]);

  async function fetchCourses(offset = 0, overrideParams = null) {
    const params = { ...(overrideParams || searchParamsState) };
    params.offset = offset;
    params.parameter = sort.sortBy;
    params.order = sort.sortOrder;
    setResultsLoading(true);
    const result = await handleGetCourses(params);
    console.log("Fetched courses:", result);
    setCourses(result.courses);
    setPagination((prev) => ({
      ...prev,
      offset,
      limit: result.pagination.limit,
      total: result.pagination.totalCourses,
    }));
    setResultsLoading(false);
    setLastSearch(params);
  }

  useEffect(() => {
    fetchInitialCourses({
      setPageLoading,
      setAreas,
      setSearchParams,
      handleGetCourseAreas,
    });
  }, []);

  const handleNewCourseCreated = (newCourse) => {
    if (newCourse && newCourse.id) {
      setCourses((prev) => [newCourse, ...prev]);
    }
  };

  // NOVO: Função para atualizar curso na lista após status mudar
  const handleCourseStatusChange = (updatedCourse) => {
    setCourses((prevCourses) =>
      prevCourses.map((c) => (c.id === updatedCourse.id ? { ...c, ...updatedCourse } : c))
    );
  };

  if (pageLoading) return <Spinner />;

  return (
    <div className="courses-page">
      <div className="courses-header">
        <SearchBar
          onSearch={setSearchingParameters}
          {...coursesFilters}
          defaultValues={searchParamsState || coursesFilters.defaultValues}
          actions={
            isAdmin && (
              <button
                className="courses-newCourse-btn"
                onClick={handleOpenNewCourseOffCanvas}
              >
                <FaBookMedical className="courses-newCourse-icon" />
                <span className="courses-newCourse-text">
                  {t("courses.addNewCourse")}
                </span>
              </button>
            )
          }
        />
      </div>
      <SortControls
        fields={coursesSortFields}
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={handleSortChange}
        isCardMode={true}
      />
      <div className="courses-content">
        {resultsLoading ? (
          <div className="courses-loading">
            <p>{t("courses.loading")}</p>
          </div>
        ) : courses.length === 0 ? (
          <div className="courses-no-results">
            <p>{t("courses.noResults")}</p>
          </div>
        ) : (
          <div className="courses-grid">
            {courses.map((course) => (
              <CourseCard
                key={course.id}
                course={course}
                onViewDetails={handleViewDetails}
              />
            ))}
          </div>
        )}
      </div>

      {/* NOVO: Offcanvas */}
      <CourseDetailsOffcanvas
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas}
        course={selectedCourse}
        onSubmit={handleNewCourseCreated}
        onCourseStatusChange={handleCourseStatusChange}
      />
      <CourseNewOffCanvas
        isOpen={newCourseOffcanvasOpen}
        onClose={handleCloseNewCourseOffCanvas}
        onSubmit={handleNewCourseCreated}
      />
      <Pagination
        offset={pagination.offset}
        limit={pagination.limit}
        total={pagination.total}
        onChange={handlePageChange}
      />
    </div>
  );
};

export default Courses;
