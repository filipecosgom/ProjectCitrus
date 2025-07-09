import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import SearchBar from "../../components/searchbar/Searchbar";
import CourseCard from "../../components/courseCard/CourseCard";
import CourseDetailsOffcanvas from "../../components/courseDetailsOffcanvas/CourseDetailsOffcanvas";
import { handleGetCourseAreas } from "../../handles/handleGetEnums";
import {
  courseSearchFilters,
  coursesSortFields,
  mapFinishedCourseToCourseCard,
} from "../../utils/coursesSearchUtils";
import Spinner from "../../components/spinner/spinner";
import SortControls from "../../components/sortControls/SortControls";
import Pagination from "../../components/pagination/Pagination";

export default function TrainingTab({ user }) {
  const { t } = useTranslation();
  const [courses, setCourses] = useState(user.completedCourses || []);
  const [filteredCourses, setFilteredCourses] = useState([]);
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
  const [searchParams, setSearchParams] = useState({
    query: "",
    searchType: "title",
    limit: 10,
    area: "",
  });
  const [sort, setSort] = useState({
    sortBy: "title",
    sortOrder: "ascending",
  });

  // Fetch course areas only (not courses)
  useEffect(() => {
    async function fetchAreas() {
      setPageLoading(true);
      const result = await handleGetCourseAreas();
      setAreas(result || []);
      setPageLoading(false);
    }
    fetchAreas();
  }, []);

  // Keep courses in sync with user
  useEffect(() => {
    setCourses(user.completedCourses || []);
    setPagination((prev) => ({
      ...prev,
      offset: 0,
      total: (user.completedCourses || []).length,
    }));
  }, [user]);

  // In-memory search, filter, sort, pagination
  useEffect(() => {
    let data = [...courses];
    // Search/filter
    if (searchParams.query) {
      data = data.filter((c) =>
        (c[searchParams.searchType] || "")
          .toLowerCase()
          .includes(searchParams.query.toLowerCase())
      );
    }
    if (searchParams.area) {
      data = data.filter((c) => c.area === searchParams.area);
    }
    // Sort
    if (sort.sortBy) {
      data.sort((a, b) => {
        if (sort.sortOrder === "ascending") {
          return (a[sort.sortBy] || "").localeCompare(b[sort.sortBy] || "");
        } else {
          return (b[sort.sortBy] || "").localeCompare(a[sort.sortBy] || "");
        }
      });
    }
    // Pagination
    const start = pagination.offset;
    const end = start + pagination.limit;
    setFilteredCourses(data.slice(start, end));
    setPagination((prev) => ({
      ...prev,
      total: data.length,
    }));
  }, [courses, searchParams, sort, pagination.offset, pagination.limit]);

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

  // Offcanvas handlers
  const handleViewDetails = (course) => {
    setSelectedCourse(course);
    setOffcanvasOpen(true);
  };
  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);
    setTimeout(() => setSelectedCourse(null), 300);
  };

  if (pageLoading) return <Spinner />;

  const coursesFilters = courseSearchFilters(t, areas);

  return (
    <div className="courses-page">
      <div className="courses-header">
        <SearchBar onSearch={handleSearch} {...coursesFilters} />
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
        ) : filteredCourses.length === 0 ? (
          <div className="courses-no-results">
            <p>{t("courses.noResults")}</p>
          </div>
        ) : (
          <div className="courses-grid">
            {filteredCourses.map((course) => {
              const mappedCourse = mapFinishedCourseToCourseCard(course);
              return (
                <CourseCard
                  key={mappedCourse.id}
                  course={mappedCourse}
                  onViewDetails={handleViewDetails}
                />
              );
            })}
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
}
