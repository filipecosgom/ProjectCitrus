import { create } from "zustand";
import { api } from "../api/api"; // Import the global API instance
import { fetchSelfInformation, fetchUserAvatar } from "../api/userApi"; // Import the function to fetch user information
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
    avatar: null, // Stores avatar URL
    avatarBlob: null, // Stores original blob if needed
    tokenExpiration: null,
    remainingTime: null,

    setAvatar: (avatarUrl, blob = null) => {
      set({ avatar: avatarUrl, avatarBlob: blob });
    },

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
          handleNotification("info", "infoAboutToExpire");
          alert("tmeout");
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

    fetchAndSetUserAvatar: async () => {
      const oldAvatar = get().avatar;
      if (oldAvatar?.startsWith("blob:")) {
        URL.revokeObjectURL(oldAvatar);
      }
      try {
        const { user } = get(); // Get current state
        if (!user?.id) {
          return { success: false, message: "No user ID available" };
        }

        const response = await fetchUserAvatar(user.id);

        if (response.success) {
          // Create blob URL from the response
          const avatarUrl = URL.createObjectURL(response.blob);

          // Update Zustand store
          set({
            avatar: avatarUrl,
            avatarBlob: response.blob,
            lastAvatarUpdate: Date.now(), // Optional: track freshness
          });

          return {
            success: true,
            data: {
              url: avatarUrl,
              contentType: response.contentType,
            },
          };
        }
        return {
          success: false,
          message: response.message || "Failed to fetch avatar",
        };
      } catch (error) {
        console.error("Avatar fetch failed:", error);
        return {
          success: false,
          message: error instanceof Error ? error.message : "Network error",
        };
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
