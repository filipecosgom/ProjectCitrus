import axios from "axios";
import { apiBaseUrl } from "../config";

export const api = axios.create({
  baseURL: apiBaseUrl, // Sets the API base URL globally
  withCredentials: true, // Ensures cookies are sent with requests
  headers: {
    "Content-Type": "application/json",
  },
});

// Global Error Handling (Handles Unauthorized Responses)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.log("Session expired, logging out...");
      // Call logout function if needed
    }
    return Promise.reject(error);
  }
);

// Global Error Handling Function
export const handleApiError = (error, context) => {
  if (error.response) {
    const status = error.response.status;
    const message = error.response.data;

    if (status === 400) throw new Error("errorInvalidData");
    if (status === 401) throw new Error("errorWrongUsernamePassword");
    if (status === 403) {
      if (message === "errorForbidden - inactive user") throw new Error("errorAccountInactive");
      if (message === "errorForbidden - excluded user") throw new Error("errorAccountExcluded");
    }
    throw new Error("errorFailed");
  }
  if (error.request) throw new Error("errorNetwork_error");
  throw new Error("errorUnexpected");
};

export default api;