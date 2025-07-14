/**
 * @file handleCheckPasswordResetToken.js
 * @module handleCheckPasswordResetToken
 * @description Handles checking the validity of a password reset token.
 * Calls backend API and returns success status.
 * @author Project Citrus Team
 */

/**
 * Checks if a password reset token is valid by calling the backend API.
 * @param {string} token - The password reset token to check.
 * @returns {Promise<boolean>} - Returns true if the token is valid, false otherwise.
 */

import { checkPasswordResetToken } from "../api/authenticationApi";

export const handleCheckPasswordResetToken = async (token) => {
  try {
    const response = await checkPasswordResetToken(token);
    return response.success; // true if valid, false if not
  } catch (error) {
    // This block might never be reached if your interceptor always resolves.
    console.error("Caught error:", error);
    return { success: false, message: error.message };
  }
};

export default handleCheckPasswordResetToken;
