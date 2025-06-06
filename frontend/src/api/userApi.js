import { api, handleApiError } from "./api"; // Import the global Axios instance

const userEndpoint = "/user";

// User register
export const register = async (email, password) => {
  try {
    const response = await api.post(`${userEndpoint}`, {
      email,
      password,
    });
    return response.data;
  } catch (error) {
    handleApiError(error, "register");
    throw error; // para propagar o erro
  }
};