import { api, handleApiError } from "./api"; // Import the global Axios instance

const authenticationEndpoint = "/auth"; // Keep only the relative endpoint


// User Login
export const login = async (loginInformation) => {
  console.log(loginInformation);
  try {
    const response = await api.post(`${authenticationEndpoint}/login`, loginInformation);
    return response.data;
  } catch (error) {
    handleApiError(error, "login");
    throw error; // para propagar o erro
  }
};

// User Login
export const requestSecretKey = async (userInformation) => {
  console.log(userInformation);
  try {
    const response = await api.post(`${authenticationEndpoint}`, userInformation);
    return response.data;
  } catch (error) {
    handleApiError(error, "auth");
    throw error;
  }
};



