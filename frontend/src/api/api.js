import axios from "axios";
import { apiBaseUrl } from "../config";
import handleNotification from "../handles/handleNotification";
import { IntlProvider } from "react-intl";


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
    // Get global intl instance for translations
    const intl = IntlProvider();
    if (intl) {
      handleApiError(error, intl);
    }
    return Promise.reject(error);
  }
);



export const handleApiError = (error, intl) => {
  console.error("API Error:", error);
  if (error.response) {
    const { success, message, errorCode } = error.response.data;
    console.log("Here first")

    if (!success) {
      handleNotification(intl, "error", errorCode || "errorUnexpected");
      console.log("Here second")
    }
  } else {
    console.log("Here third")
    handleNotification(intl, "error", "errorNetworkError");
  }
};