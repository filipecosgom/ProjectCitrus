/**
 * API module for appraisals.
 * Provides functions to fetch, update, and export appraisals.
 * @module appraisalsApi
 */
import { api } from "./api";

const appraisalsEndpoint = "/appraisals";

/**
 * Fetches paginated appraisals with optional filters.
 * @async
 * @param {Object} params - Filter and pagination parameters.
 * @returns {Promise<Object>} Paginated appraisals.
 */
export const fetchPaginatedAppraisals = async ({
  appraisedUserId = null,
  appraisedUserEmail = null,
  appraisedUserName = null,
  appraisingUserId = null,
  appraisingUserName = null,
  appraisingUserEmail = null,
  cycleId = null,
  state = null,
  isAdmin = null,
  isManaged = null,
  parameter = "name",
  order = "ASCENDING",
  offset = 0,
  limit = 10,
} = {}) => {
  try {
    const params = new URLSearchParams();

    // Add optional parameters if they exist
    if (appraisedUserId) params.append("appraisedUserId", appraisedUserId);
    if (appraisedUserEmail)
      params.append("appraisedUserEmail", appraisedUserEmail);
    if (appraisedUserName)
      params.append("appraisedUserName", appraisedUserName);
    if (appraisingUserId) params.append("appraisingUserId", appraisingUserId);
    if (appraisingUserName)
      params.append("appraisingUserName", appraisingUserName);
    if (appraisingUserEmail)
      params.append("appraisingUserEmail", appraisingUserEmail);
    if (cycleId) params.append("cycleId", cycleId);
    if (state) params.append("state", state);

    // Add pagination and sorting parameters
    params.append("parameter", parameter);
    params.append("order", order);
    params.append("offset", offset);
    params.append("limit", limit);

    const response = await api.get(
      `${appraisalsEndpoint}?${params.toString()}`
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
 * Updates an appraisal.
 * @async
 * @param {Object} updateAppraisalDTO - Appraisal update data.
 * @returns {Promise<Object>} Update result.
 */
export const updateAppraisal = async (updateAppraisalDTO) => {
  try {
    const response = await api.patch(appraisalsEndpoint, updateAppraisalDTO, {
      withCredentials: true,
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
 * Fetches appraisals as PDF.
 * @async
 * @param {Object} params - Filter parameters.
 * @returns {Promise<Object>} PDF blob and metadata.
 */
export const fetchAppraisalsPdf = async (params = {}) => {
  try {
    const response = await api.get("/appraisals/pdf", {
      params,
      paramsSerializer: (params) => {
        const searchParams = new URLSearchParams();
        Object.entries(params).forEach(([key, value]) => {
          if (value !== undefined && value !== null && value !== "") {
            searchParams.append(key, value);
          }
        });
        return searchParams.toString();
      },
      responseType: "blob",
      headers: { Accept: "application/pdf" },
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
