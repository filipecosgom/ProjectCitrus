/**
 * @file handleFetchAllConversations.js
 * @module handleFetchAllConversations
 * @description Handles fetching all user conversations from the backend.
 * Returns an array of conversations or an error object with notification flags.
 * @author Project Citrus Team
 */

/**
 * Fetches all user conversations from the backend.
 * Returns only the conversations array on success, or an error object with notification flags on failure.
 * @returns {Promise<Array|Object>} Array of conversations or error object
 */

import { fetchAllConversations } from "../api/messagesApi";

export const handleFetchAllConversations = async () => {
  const response = await fetchAllConversations();

  if (response.success) {
    // Return only the conversations array for simplicity
    return response.data?.data || [];
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
