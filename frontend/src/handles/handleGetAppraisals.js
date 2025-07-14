/**
 * @file handleGetAppraisals.js
 * @module handleGetAppraisals
 * @description Handles fetching paginated appraisals from the backend, processes date fields, and returns pagination info.
 * Maps backend error codes/messages to notification flags.
 * @author Project Citrus Team
 */

/**
 * Fetches paginated appraisals from the backend and processes date fields.
 * Returns appraisals array, pagination info, and success status.
 * @param {Object} [params={}] - Parameters for fetching appraisals
 * @returns {Promise<Object>} Object with appraisals, pagination, success, and error info
 */

import { fetchPaginatedAppraisals } from "../api/appraisalsApi";
import {
  dateToFormattedDate,
  transformArrayLocalDatetoLocalDate,
} from "../utils/utilityFunctions";

export const handleGetAppraisals = async (params = {}) => {
  const response = await fetchPaginatedAppraisals(params);

  if (response.success) {
    const appraisals = response.data?.data?.appraisals || [];
    const paginationInfo = {
      totalAppraisals: response.data?.data?.totalAppraisals || 0,
      offset: response.data?.data?.offset || 0,
      limit: response.data?.data?.limit || 10,
    };

    // Optionally process dates if needed
    const processedAppraisals = appraisals.map((appraisal) => {
      let processed = { ...appraisal };
      if (appraisal.creationDate) {
        processed.creationDate = dateToFormattedDate(
          transformArrayLocalDatetoLocalDate(appraisal.creationDate)
        );
      }
      if (appraisal.endDate) {
        processed.endDate = dateToFormattedDate(
          transformArrayLocalDatetoLocalDate(appraisal.endDate)
        );
      }
      return processed;
    });

    return {
      appraisals: processedAppraisals,
      pagination: paginationInfo,
      success: true,
    };
  } else {
    if (response.error?.errorHandled) {
      return {
        success: false,
        error: response.error,
        suppressToast: true,
      };
    }
    return {
      success: false,
      error: response.error || { message: "Unknown error" },
      shouldNotify: true,
    };
  }
};
