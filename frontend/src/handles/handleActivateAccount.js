/**
 * @file handleActivateAccount.js
 * @module handleActivateAccount
 * @description Handles user account activation using a token and notifies the user of success or error.
 * Maps backend error codes/messages to translation keys for notification.
 * @author Project Citrus Team
 */

/**
 * Activates a user account using the provided token.
 * Notifies the user of success or error using handleNotification.
 * @param {string} token - Activation token
 * @returns {Promise<boolean>} True if activation succeeded, false otherwise
 */

import { activateAccount } from "../api/activationApi";
import handleNotification from "./handleNotification";

export const handleActivateAccount = async (token) => {
  const response = await activateAccount(token);
  if (response.success) {
    return true; // Success
  } else {
    // Map backend error codes/messages to translation keys
    let errorKey = null;
    const errorMsg = response.message || response.errorCode || response.error;
    if (errorMsg) {
      if (errorMsg.includes("Invalid token")) errorKey = "errorInvalidToken";
      else if (errorMsg.includes("Token expired"))
        errorKey = "errorTokenExpired";
      else if (errorMsg.includes("activation failed"))
        errorKey = "errorAccountActivationFailed";
      else if (errorMsg.includes("server")) errorKey = "errorServerIssue";
      // Add more mappings as needed
    }
    handleNotification("error", errorKey || "errorAccountActivationFailed");
    return false;
  }
};
export default handleActivateAccount;
