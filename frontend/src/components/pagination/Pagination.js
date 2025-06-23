// Pagination.jsx
import React from 'react';
import PropTypes from 'prop-types';
import './Pagination.css';

const Pagination = ({ offset, limit, total, onChange }) => {
    const totalPages = Math.ceil(total / limit);
    const currentPage = Math.floor(offset / limit) + 1;
    const maxVisiblePages = 5; // Number of page buttons to show

    const handlePageChange = (newOffset) => {
        if (newOffset >= 0 && newOffset < total) {
            onChange(newOffset);
        }
    };

    const renderPageNumbers = () => {
        if (totalPages <= 1) return null;

        let startPage, endPage;
        if (totalPages <= maxVisiblePages) {
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

        const pages = Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);

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

                {pages.map(page => (
                    <button
                        key={page}
                        onClick={() => handlePageChange((page - 1) * limit)}
                        className={`pagination-number ${currentPage === page ? 'active' : ''}`}
                    >
                        {page}
                    </button>
                ))}

                {endPage < totalPages && (
                    <>
                        {endPage < totalPages - 1 && <span className="pagination-ellipsis">...</span>}
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

    if (total <= limit) return null;

    return (
        <div className="pagination-container">
            <button
                onClick={() => handlePageChange(offset - limit)}
                disabled={offset === 0}
                className="pagination-arrow"
                aria-label="Previous page"
            >
                &lt;
            </button>

            {renderPageNumbers()}

            <button
                onClick={() => handlePageChange(offset + limit)}
                disabled={offset + limit >= total}
                className="pagination-arrow"
                aria-label="Next page"
            >
                &gt;
            </button>
        </div>
    );
};

Pagination.propTypes = {
    offset: PropTypes.number.isRequired,
    limit: PropTypes.number.isRequired,
    total: PropTypes.number.isRequired,
    onChange: PropTypes.func.isRequired
};

export default Pagination;