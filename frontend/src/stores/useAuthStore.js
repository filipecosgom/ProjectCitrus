import { create } from "zustand";
import { api } from "../api/api"; // Import the global API instance

const useAuthStore = create((set) => ({
  user: null,

  // Fetch authenticated user data
  fetchUser: async () => {
    try {
      const response = await api.get("/user"); // Backend validates JWT via cookies
      set({ user: response.data });
    } catch (error) {
      set({ user: null }); // Clears state if authentication fails
    }
  },

  // Logout the user
  logout: async () => {
    await api.post("/logout");
    set({ user: null });
  },
}));

export default useAuthStore;