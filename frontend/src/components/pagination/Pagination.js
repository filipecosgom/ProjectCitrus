/**
 * Pagination.jsx
 *
 * Pagination component for navigating paged data sets. Supports page numbers, arrows, and ellipsis for large sets.
 *
 * @module Pagination
 */
// Pagination.jsx
import React from "react";
import PropTypes from "prop-types";
import { IoIosArrowBack, IoIosArrowForward } from "react-icons/io";
import "./Pagination.css";

/**
 * Pagination component
 *
 * @param {Object} props - Component props
 * @param {number} props.offset - Current offset (start index)
 * @param {number} props.limit - Items per page
 * @param {number} props.total - Total number of items
 * @param {Function} props.onChange - Callback for page change (receives new offset)
 * @returns {JSX.Element|null} The rendered pagination controls
 */
const Pagination = ({ offset, limit, total, onChange }) => {
  const totalPages = Math.max(1, Math.ceil(total / limit));
  const currentPage = Math.floor(offset / limit) + 1;
  const maxVisiblePages = 5; // Number of page buttons to show

  /**
   * Handle page change (validates offset and calls onChange).
   *
   * @param {number} newOffset - The new offset to set
   */
  const handlePageChange = (newOffset) => {
    if (newOffset >= 0 && newOffset < total) {
      onChange(newOffset);
    }
  };

  const renderPageNumbers = () => {
    if (totalPages <= 1) return null;

    let startPage, endPage;
    if (totalPages <= maxVisiblePages) {
      /**
       * Render page number buttons and ellipsis for large page sets.
       *
       * @returns {JSX.Element|null} Page number buttons and ellipsis
       */
      startPage = 1;
      endPage = totalPages;
    } else {
      const maxPagesBeforeCurrent = Math.floor(maxVisiblePages / 2);
      const maxPagesAfterCurrent = Math.ceil(maxVisiblePages / 2) - 1;

      if (currentPage <= maxPagesBeforeCurrent) {
        startPage = 1;
        endPage = maxVisiblePages;
      } else if (currentPage + maxPagesAfterCurrent >= totalPages) {
        startPage = totalPages - maxVisiblePages + 1;
        endPage = totalPages;
      } else {
        startPage = currentPage - maxPagesBeforeCurrent;
        endPage = currentPage + maxPagesAfterCurrent;
      }
    }

    const pages = Array.from(
      { length: endPage - startPage + 1 },
      (_, i) => startPage + i
    );

    return (
      <>
        {startPage > 1 && (
          <>
            <button
              onClick={() => handlePageChange(0)}
              className="pagination-number"
            >
              1
            </button>
            {startPage > 2 && <span className="pagination-ellipsis">...</span>}
          </>
        )}

        {pages.map((page) => (
          <button
            key={page}
            onClick={() => handlePageChange((page - 1) * limit)}
            className={`pagination-number ${
              currentPage === page ? "active" : ""
            }`}
          >
            {page}
          </button>
        ))}

        {endPage < totalPages && (
          <>
            {endPage < totalPages - 1 && (
              <span className="pagination-ellipsis">...</span>
            )}
            <button
              onClick={() => handlePageChange((totalPages - 1) * limit)}
              className="pagination-number"
            >
              {totalPages}
            </button>
          </>
        )}
      </>
    );
  };

  if (limit <= 0) return null;
  if (totalPages <= 1) {
    return (
      <div className="pagination-container">
        <button
          onClick={() => handlePageChange(offset - limit)}
          disabled={offset === 0}
          className="pagination-arrow"
          aria-label="Previous page"
        >
          <IoIosArrowBack />
        </button>
        <button className="pagination-number active" disabled>
          1
        </button>
        <button
          onClick={() => handlePageChange(offset + limit)}
          disabled={offset + limit >= total}
          className="pagination-arrow"
          aria-label="Next page"
        >
          <IoIosArrowForward />
        </button>
      </div>
    );
  }

  return (
    <div className="pagination-container">
      <button
        onClick={() => handlePageChange(offset - limit)}
        disabled={offset === 0}
        className="pagination-arrow"
        aria-label="Previous page"
      >
        <IoIosArrowBack />
      </button>

      {renderPageNumbers()}

      <button
        onClick={() => handlePageChange(offset + limit)}
        disabled={offset + limit >= total}
        className="pagination-arrow"
        aria-label="Next page"
      >
        <IoIosArrowForward />
      </button>
    </div>
  );
};

Pagination.propTypes = {
  offset: PropTypes.number.isRequired,
  limit: PropTypes.number.isRequired,
  total: PropTypes.number.isRequired,
  onChange: PropTypes.func.isRequired,
};

export default Pagination;
