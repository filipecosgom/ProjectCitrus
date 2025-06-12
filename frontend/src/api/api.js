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

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const message = error.response?.data?.message;

    const isAuthError =
      status === 401 || (status === 403 && message?.includes("Invalid token"));
    const authStore = useAuthStore.getState();

    // ✅ Only attempt logout if the user is actually stored
    if (isAuthError && authStore.user) {
      console.log("Session invalid, logging out...");
      authStore.logout();
    }

    // ⛔ Prevent toasts during silent hydration or non-critical fetches
    const errorData = { ...error }; // Clone error so we can flag it
    if (isAuthError && !authStore.user) {
      errorData.suppressToast = true; // Don't show "session expired" if we're not even logged in
    }

    // ✅ Respect suppressToast flag
    if (!errorData.suppressToast) {
        handleApiError(errorData)
    }

    return Promise.reject(error);
  }
);

export const handleApiError = (error) => {
  if (error.response) {
    const { success, message, errorCode } = error.response.data;
    console.log(errorCode);

    if (!success) {
      handleNotification("error", errorCode || "errorUnexpected");
      console.log("ending");
    }
  } else {
    handleNotification("error", "errorNetworkError");
  }
};
