import { create } from "zustand";
import { api } from "../api/api"; // Import the global API instance
import { fetchSelfInformation } from "../api/userApi"; // Import the function to fetch user information
import handleNotification from "../handles/handleNotification";

const useAuthStore = create((set, get) => {
  const TIME_TO_WARN = 5 * 60 * 1000; // 5 minutes
  let timers = {}; // Object to manage timeouts and interval

  const clearTimers = () => {
    Object.values(timers).forEach(clearTimeout);
    clearInterval(timers.trackingInterval);
  };

  return {
    user: null,
    tokenExpiration: null,
    remainingTime: null,

    setUserAndExpiration: (user, tokenExpiration) => {
      clearTimers();
      set({ user, tokenExpiration });

      const updateRemainingTime = () => {
        const timeLeft = tokenExpiration - Date.now();
        set({ remainingTime: timeLeft });

        if (timeLeft <= 0) {
          clearTimers();
          get().logout();
        }
      };

      updateRemainingTime();
      timers.trackingInterval = setInterval(updateRemainingTime, 1000);

      if (tokenExpiration - Date.now() > TIME_TO_WARN) {
        timers.warningTimeout = setTimeout(() => {
          handleNotification("warn", "infoAboutToExpire");
        }, tokenExpiration - Date.now() - TIME_TO_WARN);
      }

      timers.logoutTimeout = setTimeout(() => {
        handleNotification("warn", "infoSessionExpired");
        get().logout();
      }, tokenExpiration - Date.now());
    },

    fetchAndSetUserInformation: async () => {
      try {
        console.log("Fetching user information...");
        const response = await fetchSelfInformation();
        if (response.success) {
          const { user, tokenExpiration } = response.data.data || {};
          if (user && tokenExpiration) {
            console.log("User information fetched successfully:", user);

            get().setUserAndExpiration(user, tokenExpiration);
          }
          return { success: true, data: { user, tokenExpiration } };
        } else {
          return { success: false, message: response.message };
        }
      } catch (error) {
        console.error("Error fetching user information:", error);
        return { success: false, message: "Failed to fetch user information." };
      }
    },

    logout: async () => {
      clearTimers();
      await api.post("/logout");
      set({ user: null, tokenExpiration: null, remainingTime: null });
    },
  };
});

export default useAuthStore;