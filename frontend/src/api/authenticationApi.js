import { api, handleApiError } from "./api"; // Import the global Axios instance

const authenticationEndpoint = "/auth"; // Keep only the relative endpoint


// User Login
export const login = async (email, password) => {
  try {
    const response = await api.post(`${authenticationEndpoint}/login`, {
      email,
      password,
    });
    return response.data;
  } catch (error) {
    handleApiError(error, "login");
  }
};

