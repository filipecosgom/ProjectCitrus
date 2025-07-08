import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import SearchBar from "../../components/searchbar/Searchbar";
import CourseCard from "../../components/courseCard/CourseCard";
import CourseDetailsOffcanvas from "../../components/courseDetailsOffcanvas/CourseDetailsOffcanvas";
import { handleGetCourseAreas } from "../../handles/handleGetEnums";
import { buildSearchParams, createPageChangeHandler, createSortHandler } from "../../utils/searchUtils";
import {
  fetchInitialCourses,
  courseSearchFilters,
  coursesSortFields
} from "../../utils/coursesSearchUtils";
import "./Courses.css";
import { handleGetCourses } from "../../handles/handleGetCourses";
import Spinner from "../../components/spinner/spinner";
import SortControls from "../../components/sortControls/SortControls";
import Pagination from "../../components/pagination/Pagination";


const Courses = () => {
  const { t } = useTranslation();
  const [courses, setCourses] = useState([]);
  const [areas, setAreas] = useState([]);
  const [resultsLoading, setResultsLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 10,
    total: 0,
  });

  const [selectedCourse, setSelectedCourse] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);

  const [searchParams, setSearchParams] = useState(null);
  const [lastSearch, setLastSearch] = useState(null);
  const [sort, setSort] = useState({
    sortBy: "title",
    sortOrder: "ascending",
  });
  const lastSearchRef = useRef(lastSearch);
  const coursesFilters = courseSearchFilters(t, areas);

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

  // Externalized: set searching parameters
    const setSearchingParameters = async (
      query,
      searchType,
      limit,
      filters = {}
    ) => {
      const search = buildSearchParams(query, searchType, limit, filters);
      setLastSearch(search);
      setSearchParams(search);
    };

    const handlePageChange = createPageChangeHandler(
        setPagination,
        fetchCourses,
        lastSearchRef
      );

      const handleSortChange = createSortHandler(
          setSort,
          setPagination,
          fetchCourses,
          lastSearchRef
        );



      async function fetchCourses(offset = 0, overrideParams = null) {
          const { query, searchType, limit, filters } =
            overrideParams || searchParams;
          setResultsLoading(true);
          const result = await handleGetCourses({
            [searchType]: query,
            offset,
            limit,
            ...filters,
            parameter: sort.sortBy,
            order: sort.sortOrder,
          });
          console.log("Fetched courses:", result.courses);
          setCourses(result.courses);
          setPagination((prev) => ({
            ...prev,
            offset,
            limit: result.pagination.limit,
            total: result.pagination.totalCourses,
          }));
          setResultsLoading(false);
          setLastSearch(overrideParams || searchParams);
        }

        useEffect(() => {
            if (searchParams) {
              fetchCourses(pagination.offset, searchParams);
            }
            // eslint-disable-next-line
          }, [searchParams, pagination.offset, sort]);



  useEffect(() => {
    fetchInitialCourses({
      setPageLoading,
      setAreas,
      setSearchParams,
      handleGetCourseAreas,
    });
    console.log("Initial courses fetched successfully", courses);
  }, []);

  useEffect(() => {
    console.log("Courses updated:", courses);
    console.log(courses)
  }, [courses]);

  if (pageLoading) return <Spinner />;


  return (
    <div className="courses-page">
      <div className="courses-header">

        <SearchBar
          onSearch={setSearchingParameters}
          {...coursesFilters}
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
