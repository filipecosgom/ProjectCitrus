/**
 * @file handleGeneratePdfOfAppraisals.js
 * @module handleGeneratePdfOfAppraisals
 * @description Handles generating a PDF of appraisals with backend parameters and notifications.
 * Maps sort order, locale, and notifies user of success or error.
 * @author Project Citrus Team
 */

/**
 * Maps sort order to backend format ("ascending" or "descending").
 * @param {string} order - Sort order string
 * @returns {string} Backend sort order
 */

/**
 * Generates a PDF of appraisals using provided parameters.
 * Notifies user of success or error, returns blob or error object.
 * @param {Object} params - Parameters for PDF generation (sortOrder, language, etc.)
 * @returns {Promise<Object>} Object with success, blob, status,
 */

import { fetchAppraisalsPdf } from "../api/appraisalsApi";
import useLocaleStore from "../stores/useLocaleStore";
import handleNotification from "./handleNotification";

// Map sortOrder to backend lowercase
const mapOrderToBackend = (order) => {
  if (!order) return "descending";
  if (order.toLowerCase().startsWith("asc")) return "ascending";
  return "descending";
};

export const handleGeneratePdfOfAppraisals = async (params = {}) => {
  const pdfParams = { ...params };
  pdfParams.language = useLocaleStore.getState().locale || "en";
  if (params.sortOrder || params.order) {
    pdfParams.order = mapOrderToBackend(params.sortOrder || params.order);
  }

  const response = await fetchAppraisalsPdf(pdfParams);

  if (response.success) {
    handleNotification("success", "appraisal.pdfGenerated");
    return {
      success: true,
      blob: response.data,
      status: response.status,
    };
  } else {
    if (response.error?.errorHandled) {
      return {
        success: false,
        error: response.error,
        suppressToast: true,
      };
    }
    handleNotification("error", "appraisal.pdfGenerationFailed");
    return {
      success: false,
      error: response.error || { message: "Unknown error" },
      shouldNotify: true,
    };
  }
};
