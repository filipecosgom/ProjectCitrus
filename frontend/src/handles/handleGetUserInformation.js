/**
 * @file handleGetUserInformation.js
 * @module handleGetUserInformation
 * @description Handles fetching user information from the backend and transforms birthdate field.
 * Returns user data object or null on failure.
 * @author Project Citrus Team
 */

/**
 * Fetches user information from the backend and transforms birthdate field.
 * @param {number|string} id - User ID
 * @returns {Promise<Object|null>} User data object or null on failure
 */

import { fetchUserInformation } from "../api/userApi";
import {
  transformArrayLocalDatetoLocalDate,
  dateToFormattedDate,
} from "../utils/utilityFunctions";

export const handleGetUserInformation = async (id) => {
  const response = await fetchUserInformation(id);
  // Fetch user details after successful login
  if (response.success) {
    if (response.data.data.birthdate) {
      response.data.data.birthdate = dateToFormattedDate(
        transformArrayLocalDatetoLocalDate(response.data.data.birthdate)
      );
    }
    return response.data.data; // Success
  } else {
    return null; // Failure
  }
};
export default handleGetUserInformation;
