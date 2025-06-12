import { create } from "zustand";
import { api } from "../api/api"; // Import the global API instance
import { fetchUserInformation } from "../api/userApi"; // Import the function to fetch user information

const useAuthStore = create((set, get) => {
  const TIME_TO_WARN = 5 * 60 * 1000; // 5 minutes in milliseconds
  let logoutTimeout = null;
  let warningTimeout = null;

  return {
    user: null,
    tokenExpiration: null,

    setUserAndExpiration: (user, tokenExpiration) => {
      clearTimeout(logoutTimeout);
      clearTimeout(warningTimeout);

      const timeUntilLogout = tokenExpiration - Date.now();

      // Warn user 5 minutes before expiration
      if (timeUntilLogout > TIME_TO_WARN) {
        warningTimeout = setTimeout(() => {
          console.log("⚠️ Session expires in 5 minutes!");
          // Display a toast/modal here
        }, timeUntilLogout - 5 * 60 * 1000);
      }

      // Auto logout when expiration is reached
      if (timeUntilLogout > 0) {
        logoutTimeout = setTimeout(() => {
          alert("⏳ Your session has expired. Logging out...");
          get().logout();
        }, timeUntilLogout);
      }

      set({ user, tokenExpiration });
    },

    fetchAndSetUserInformation: async () => {
      try {
        const response = await fetchUserInformation();
        if (response.success) {
          console.log("User information fetched successfully:", response.data);
          const { user, tokenExpiration } = response.data.data || {};
          if (user && tokenExpiration) {
            get().setUserAndExpiration(user, tokenExpiration); // ✅ Pass actual expiration data
            console.log("User and expiration set successfully.");
            console.log("Current stored user:", useAuthStore.getState().user);
            console.log(
              "Current stored expiration:",
              useAuthStore.getState().tokenExpiration
            );
          }
          return { success: true, data: { user: user, tokenExpiration } };
        } else {
          return { success: false, message: response.message };
        }
      } catch (error) {
        console.error("Error fetching user information:", error);
        return { success: false, message: "Failed to fetch user information." };
      }
    },

    logout: async () => {
      clearTimeout(logoutTimeout);
      clearTimeout(warningTimeout);
      await api.post("/logout");
      set({ user: null, expiration: null });
    },
  };
});

export default useAuthStore;
