import { api } from "./api"; // No need to import handleApiError here

const userEndpoint = "/user";

export const register = async (newUser) => {
  try {
    const response = await api.post(userEndpoint, newUser);
    console.log("API Response:", response); // Debugging log
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

export const fetchUserInformation = async(token) => {
  
  
}
