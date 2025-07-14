/**
 * API module for authentication.
 * Provides functions for login, password reset, and logout.
 * @module authenticationApi
 */

import { api } from "./api"; // Import the global Axios instance

const authenticationEndpoint = "/auth"; // Keep only the relative endpoint

/**
 * Logs in a user.
 * @async
 * @param {Object} loginInformation - Login credentials.
 * @returns {Promise<Object>} Login result.
 * @throws Will throw an error if the request fails.
 */
export const login = async (loginInformation) => {
  try {
    const response = await api.post(
      `${authenticationEndpoint}/login`,
      loginInformation
    );
    return response.data;
  } catch (error) {
    throw error; // para propagar o erro
  }
};

/**
 * Requests a secret key for two-factor authentication.
 * @async
 * @param {Object} userInformation - User information.
 * @returns {Promise<Object>} Secret key result.
 * @throws Will throw an error if the request fails.
 */
export const requestSecretKey = async (userInformation) => {
  try {
    const response = await api.post(
      `${authenticationEndpoint}`,
      userInformation
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

/**
 * Requests a password reset email.
 * @async
 * @param {Object} email - User email.
 * @param {string} lang - Language code.
 * @returns {Promise<Object>} Operation result.
 */
export const requestPasswordReset = async (email, lang) => {
  try {
    const response = await api.post(
      `${authenticationEndpoint}/password-reset`,
      email,
      {
        headers: {
          "Accept-Language": lang,
        },
      }
    );
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

/**
 * Checks if a password reset token is valid.
 * @async
 * @param {string} token - Password reset token.
 * @returns {Promise<Object>} Validation result.
 */
export const checkPasswordResetToken = async (token) => {
  try {
    const response = await api.get(`${authenticationEndpoint}/password-reset`, {
      headers: {
        token: token,
      },
    });
    // If response.data is present, assume the successful format
    if (response.data) {
      // Normalize to a common format:
      return {
        success: response.data.success,
        status: response.status,
        data: response.data,
      };
    }

    // Otherwise, assume it's already normalized (error response from interceptor)
    return response;
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

/**
 * Changes the user's password using a reset token.
 * @async
 * @param {string} passwordResetToken - Password reset token.
 * @param {Object} newPassword - New password data.
 * @returns {Promise<Object>} Operation result.
 */
export const changePassword = async (passwordResetToken, newPassword) => {
  try {
    const response = await api.patch(
      `${authenticationEndpoint}/password-reset`,
      newPassword,
      {
        headers: {
          token: passwordResetToken,
        },
      }
    );
    // If response.data is present, assume the successful format
    if (response.data) {
      // Normalize to a common format:
      return {
        success: response.data.success,
        status: response.status,
        data: response.data,
      };
    }

    // Otherwise, assume it's already normalized (error response from interceptor)
    return response;
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

/**
 * Logs out the current user.
 * @async
 * @returns {Promise<Object>} Logout result.
 */
export const logoutRequest = async () => {
  try {
    const response = await api.post(`${authenticationEndpoint}/logout`);
    return response.data;
  } catch (error) {
    // Optionally handle error
    return { success: false, error };
  }
};
