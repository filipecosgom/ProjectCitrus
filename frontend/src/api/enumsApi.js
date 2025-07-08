import { api, handleApiError } from './api'; // Your configured axios instance

const enumsEndpoint = "/enums"; // Keep only the relative endpoint

// Fetch all roles by calling the /enums/roles endpoint
export const getRoles = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/roles`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    handleApiError(error, "getRoles");
    throw error; // propagate the error if needed
  }
};

// Fetch all offices by calling the /enums/offices endpoint
export const getOffices = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/offices`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    handleApiError(error, "getOffices");
    throw error;
  }
};

export const getAppraisalStates = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/appraisalStates`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    handleApiError(error, "getAppraisalStates");
    throw error; // propagate the error if needed
  }
};

export const getCourseAreas = async () => {
  try {
    const response = await api.get(`${enumsEndpoint}/courseAreas`);
    // Assuming the API returns an array of strings
    return response.data;
  } catch (error) {
    handleApiError(error, "getCourseAreas");
    throw error; // propagate the error if needed
  }
};