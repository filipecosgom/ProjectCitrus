/**
 * API module for account activation.
 * Provides functions to activate a user account using a token.
 * @module activationApi
 */
import { api } from "./api"; // Import the global Axios instance

const activationEndpoint = "/activate"; // Keep only the relative endpoint

/**
 * Activates a user account with the provided token.
 * Sends a POST request to the backend activation endpoint.
 *
 * @async
 * @param {string} token - Activation token for the user account.
 * @returns {Promise<Object>} Response data from the backend.
 * @throws Will throw an error if the request fails.
 */
export const activateAccount = async (token) => {
  try {
    const response = await api.post(
      `${activationEndpoint}`,
      {},
      {
        headers: {
          token: token, // Use the token directly in the headers
        },
      }
    );
    return response.data;
  } catch (error) {
    throw error; // para propagar o erro
  }
};
