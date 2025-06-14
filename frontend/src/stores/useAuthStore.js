import { create } from "zustand";
import { api } from "../api/api"; // Import the global API instance
import { fetchUserInformation } from "../api/userApi"; // Import the function to fetch user information
import handleNotification from "../handles/handleNotification";

const useAuthStore = create((set, get) => {
  const TIME_TO_WARN = 5 * 60 * 1000; // 5 minutes

  let timers = {}; // Object to manage timeouts and interval

  const clearTimers = () => {
    Object.values(timers).forEach(clearTimeout); // Clear all timeouts
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
      timers.trackingInterval = setInterval(updateRemainingTime, 1000); // Update every second

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

    logout: async () => {
      clearTimers();
      await api.post("/logout");
      set({ user: null, tokenExpiration: null, remainingTime: null });
    },
  };
});

export default useAuthStore;