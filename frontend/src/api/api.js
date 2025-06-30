import axios from "axios";
import { apiBaseUrl } from "../config";
import handleNotification from "../handles/handleNotification";
import useAuthStore from "../stores/useAuthStore";

export const api = axios.create({
  baseURL: apiBaseUrl, // Sets the API base URL globally
  withCredentials: true, // Ensures cookies are sent with requests
  headers: {
    "Content-Type": "application/json",
  },
});

// Add a request interceptor to preserve FormData headers
api.interceptors.request.use(config => {
  if (config.data instanceof FormData) {
    Object.assign(config.headers, {
      'Content-Type': 'multipart/form-data'
    });
  }
  return config;
});

// List of endpoints that should NOT refresh the session
const skipEndpoints = ["/login", "/logout", "/request-password-reset", "/activate", "/register"];

api.interceptors.response.use(
  (response) => {
    // Refresh session timer on most successful responses
    const url = response.config.url;
    if (!skipEndpoints.some((endpoint) => url && url.includes(endpoint))) {
      // Call the refreshSessionTimer method if it exists
      const store = useAuthStore.getState();
      if (typeof store.refreshSessionTimer === "function") {
        store.refreshSessionTimer();
      }
    }
    return response;
  },
  (error) => {
    const success = error.response?.data?.success;
    const status = error.response?.status;
    const message = error.response?.data?.message;
    const errorCode = error.response?.data?.errorCode || "errorUnexpected";

    const isAuthError =
      (status === 401 && message?.includes("Missing token"));
    const authStore = useAuthStore.getState();

    // ✅ Only attempt logout if the user is actually stored
    if (isAuthError && authStore.user) {
      authStore.logout();
    }

    // ⛔ Prevent toasts during silent hydration or non-critical fetches
    const errorData = { ...error }; // Clone error so we can flag it
    if (isAuthError && !authStore.user) {
      // No user session, suppressing toast
      errorData.suppressToast = true; // Don't show "session expired" if we're not even logged in
    }

    // ✅ Respect suppressToast flag
    if (!errorData.suppressToast) {
        handleApiError(errorData)
    }

    return Promise.resolve({
      success: false,
      status,
      errorCode,
      message,
      errorHandled: true
    });
  }
);

export const handleApiError = (error) => {
  if (error.response) {
    const { success, message, errorCode } = error.response.data;

    if (!success) {
      handleNotification("error", errorCode || "errorUnexpected");
    }
  } else {
    handleNotification("error", "errorNetworkError");
  }
};
