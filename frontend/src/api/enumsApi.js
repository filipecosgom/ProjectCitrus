/**
 * API module for enums.
 * Provides functions to fetch roles, offices, appraisal states, and course areas.
 * @module enumsApi
 */
import { api } from "./api"; // Your configured axios instance

const enumsEndpoint = "/enums"; // Keep only the relative endpoint

/**
 * Fetches all roles.
 * @async
 * @returns {Promise<Array<string>>} List of roles.
 */
export const getRoles = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/roles`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    console.error("Error fetching roles:", error);
    throw error; // propagate the error if needed
  }
};

/**
 * Fetches all offices.
 * @async
 * @returns {Promise<Array<string>>} List of offices.
 */
export const getOffices = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/offices`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    console.error("Error fetching offices:", error);
    throw error;
  }
};

/**
 * Fetches all appraisal states.
 * @async
 * @returns {Promise<Array<string>>} List of appraisal states.
 */
export const getAppraisalStates = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/appraisalStates`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    console.error("Error fetching appraisal states:", error);
    throw error; // propagate the error if needed
  }
};

/**
 * Fetches all course areas.
 * @async
 * @returns {Promise<Array<string>>} List of course areas.
 */
export const getCourseAreas = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/courseAreas`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    console.error("Error fetching course areas:", error);
    throw error; // propagate the error if needed
  }
};
