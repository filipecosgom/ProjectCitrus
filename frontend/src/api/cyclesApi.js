/**
 * API module for cycles.
 * Provides functions to fetch, create, close, and validate cycles.
 * @module cyclesApi
 */

import axios from "axios";
import { apiBaseUrl } from "../config";

const cyclesEndpoint = `${apiBaseUrl}/cycles`;

/**
 * Fetches all cycles with optional filters.
 * @async
 * @param {Object} [params={}] - Filter parameters.
 * @returns {Promise<Object>} List of cycles.
 */
export const fetchCycles = async (params = {}) => {
  try {
    const response = await axios.get(cyclesEndpoint, {
      params,
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Creates a new cycle.
 * @async
 * @param {Object} cycleData - Cycle data.
 * @returns {Promise<Object>} Operation result.
 */
export const createCycle = async (cycleData) => {
  try {
    const response = await axios.post(cyclesEndpoint, cycleData, {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Closes a cycle.
 * @async
 * @param {number} id - Cycle ID.
 * @returns {Promise<Object>} Operation result.
 */
export const closeCycle = async (id) => {
  try {
    const response = await axios.post(
      `${cyclesEndpoint}/${id}/close`,
      {},
      {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Validates if a cycle can be closed.
 * @async
 * @param {number} cycleId - The cycle ID.
 * @returns {Promise<Object>} API response with validation details.
 */
export const canCloseCycle = async (cycleId) => {
  try {
    const response = await axios.get(`${cyclesEndpoint}/${cycleId}/can-close`, {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    console.error("Error validating cycle closure:", error);
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Fetches the count of active users (not admin and not deleted).
 * @async
 * @returns {Promise<Object>} Count of active users.
 */
export const fetchActiveUsersCount = async () => {
  try {
    const response = await axios.get(`${apiBaseUrl}/users`, {
      params: {
        isAdmin: false,
        isDeleted: false,
        limit: 1, // Só precisamos da contagem
      },
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};
