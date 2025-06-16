import { api, handleApiError } from "./api"; // Import the global Axios instance

const activationEndpoint = "/activate"; // Keep only the relative endpoint

// User Login
export const activateAccount = async (token) => {
  console.log(token);
  try {
    const response = await api.post(`${activationEndpoint}`,{}, {
      headers: {
        "token": token, // Use the token directly in the headers
      },
    });
    console.log("API Response:", response); // Debugging log
    return response.data;
  } catch (error) {
    handleApiError(error, "login");
    throw error; // para propagar o erro
  }
};