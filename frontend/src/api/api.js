import axios from "axios";
import { apiBaseUrl } from "../config";
import handleNotification from "../handles/handleNotification";
import { getIntl, getCurrentLocale } from "../utils/Intl";


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
    console.log(error);
    
    console.log(error.response);
    let errorData = error;
    // Get global intl instance for translations
    const currentLocale = getCurrentLocale();
    const intl = getIntl();
    console.log('Current locale in interceptor:', currentLocale);
    if (intl) {
      console.log('aqui');
      handleApiError(errorData, intl);
    }
    return Promise.reject(error);
  }
);



export const handleApiError = (error, intl) => {
  if (error.response) {
    const { success, message, errorCode } = error.response.data;

    if (!success) {
      handleNotification(intl, "error", errorCode || "errorUnexpected");
      console.log("ending")
    }
  } else {
    handleNotification(intl, "error", "errorNetworkError");
  }
};