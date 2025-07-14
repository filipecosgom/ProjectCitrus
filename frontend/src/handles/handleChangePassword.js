/**
 * @file handleChangePassword.js
 * @module handleChangePassword
 * @description Handles password change using a reset token and new password.
 * Calls backend API and returns success status.
 * @author Project Citrus Team
 */

/**
 * Changes the user's password using a password reset token and new password.
 * @param {string} passwordResetToken - Token for password reset
 * @param {string} newPassword - New password to set
 * @returns {Promise<boolean>} True if password change succeeded, false otherwise
 */

import { changePassword } from "../api/authenticationApi";
export const handleChangePassword = async (passwordResetToken, newPassword) => {
  const password = {
    password: newPassword,
  };
  const response = await changePassword(passwordResetToken, password);
  return response.success;
};

export default handleChangePassword;
