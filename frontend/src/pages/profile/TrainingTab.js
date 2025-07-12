import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import SearchBar from "../../components/searchbar/Searchbar";
import CourseCard from "../../components/courseCard/CourseCard";
import CourseDetailsOffcanvas from "../../components/courseDetailsOffcanvas/CourseDetailsOffcanvas";
import AddCompletedCourseOffcanvas from "../../components/addCompletedCourseOffcanvas/AddCompletedCourseOffcanvas";
import { handleGetCourseAreas } from "../../handles/handleGetEnums";
import {
  courseSearchFilters,
  coursesSortFields,
} from "../../utils/coursesSearchUtils";
import Spinner from "../../components/spinner/spinner";
import SortControls from "../../components/sortControls/SortControls";
import Pagination from "../../components/pagination/Pagination";
import { handleAddCompletedCourseToUser } from "../../handles/handleAddCompletedCourseToUser";
import './TrainingTab.css';

export default function TrainingTab({ courses: initialCourses = [], isTheManagerOfUser, userId }) {
  const { t } = useTranslation();
  const [courses, setCourses] = useState(initialCourses);
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
  const [showAddOffcanvas, setShowAddOffcanvas] = useState(false);
  // Extract years from courses
  const years = Array.from(
    new Set((courses || [])
      .map((c) => Array.isArray(c.completionDate) ? c.completionDate[0] : null)
      .filter((y) => y))
  ).sort((a, b) => b - a);
  const yearOptions = [
    { value: '', label: t('courses.allYears') },
    ...years.map((y) => ({ value: y.toString(), label: y.toString() }))
  ];
  // Add year to searchParams
  const [searchParams, setSearchParams] = useState({
    query: '',
    searchType: 'title',
    limit: 10,
    area: '',
    year: '',
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
    if (searchParams.language) {
      data = data.filter((c) => c.language === searchParams.language);
    }
    if (searchParams.year) {
      data = data.filter((c) => Array.isArray(c.completionDate) && c.completionDate[0] === Number(searchParams.year));
    }
    // Sort
    if (sort.sortBy) {
      const sortProp = sort.sortBy;
      data.sort((a, b) => {
        if (sort.sortOrder === "ascending") {
          return (a[sortProp] || "").localeCompare(b[sortProp] || "");
        } else {
          return (b[sortProp] || "").localeCompare(a[sortProp] || "");
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
    console.log('handleSearch called with:', { query, searchType, limit, filters }); // DEBUG
    setSearchParams((prev) => ({
      ...prev,
      query,
      searchType,
      limit,
      ...filters,
    }));
    setPagination((prev) => ({ ...prev, offset: 0, limit })); // <-- update limit here
  };

  // Handler for year dropdown
  const handleYearChange = (e) => {
    setSearchParams((prev) => ({ ...prev, year: e.target.value }));
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

  // Handler for Add Completed Course button (to be used by SearchBar)
  const handleAddCourseToUser = () => {
    setShowAddOffcanvas(true);
  };

  // When the offcanvas submits, add the new courses to the local state
  const handleOffcanvasSubmit = async (userId, courseIds, courseObjs) => {
    const result = await handleAddCompletedCourseToUser(userId, courseIds);
    if (result.success && Array.isArray(courseObjs)) {
      setCourses(prev => {
        const existingIds = new Set(prev.map(c => c.id));
        const newOnes = courseObjs.filter(c => !existingIds.has(c.id));
        return [...prev, ...newOnes];
      });
    }
    setShowAddOffcanvas(false);
  };

  if (pageLoading) return <Spinner />;

  const coursesFilters = courseSearchFilters(t, areas);
  // Calculate total hours for selected year
  const totalHours = (courses || [])
    .filter((c) => !searchParams.year || (Array.isArray(c.completionDate) && c.completionDate[0] === Number(searchParams.year)))
    .reduce((sum, c) => sum + (c.duration || 0), 0);


  return (
    <div className="courses-page">
      <div className="courses-header">
        <div className="courses-header-row">
          <SearchBar
            onSearch={handleSearch}
            {...coursesFilters}
            renderYearDropdown={() => (
              <select
                className="years-filter-select"
                value={searchParams.year}
                onChange={handleYearChange}
              >
                {yearOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
              </select>
            )}
            userId={userId}
            {...(isTheManagerOfUser ? { onAddCourseToUser: handleAddCourseToUser } : {})}
          />
        </div>
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
            {filteredCourses.map((course) => (
              <CourseCard
                key={course.id}
                course={course}
                onViewDetails={handleViewDetails}
              />
            ))}
          </div>
        )}
      </div>
      <div className="courses-total-hours-container">
        <div className="courses-total-hours-label">
          {t('courses.totalHours', 'Total hours')}:
        </div>
        <div className="courses-total-hours-value">
          {totalHours}h
        </div>
      </div>
      

      {/* NOVO: Offcanvas */}
      <CourseDetailsOffcanvas
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas}
        course={selectedCourse}
      />
      {isTheManagerOfUser && (
        <AddCompletedCourseOffcanvas
          isOpen={showAddOffcanvas}
          onClose={() => setShowAddOffcanvas(false)}
          onAdd={async ({ userId, courseId, course }) => true}
          userId={userId}
          onSubmit={handleOffcanvasSubmit}
        />
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
