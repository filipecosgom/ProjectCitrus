/**
 * @file handleGetUserAvatar.js
 * @module handleGetUserAvatar
 * @description Handles fetching a user's avatar image from the backend and returns a blob URL.
 * Returns success status, avatar URL, and error info if applicable.
 * @author Project Citrus Team
 */

/**
 * Fetches a user's avatar image from the backend and returns a blob URL.
 * @param {number|string} id - User ID
 * @returns {Promise<Object>} Object with success, avatar URL, user data, and error info
 */

import { fetchUserAvatar } from "../api/userApi";

export const handleGetUserAvatar = async (id) => {
  try {
    const response = await fetchUserAvatar(id);

    if (!response.success) {
      console.error("Avatar fetch failed:", response.message);
      return {
        success: false,
        error: response.message || "Failed to fetch avatar",
      };
    }

    const imageUrl = URL.createObjectURL(response.blob);
    return {
      success: true,
      avatar: imageUrl,
      userData: response.data,
    };
  } catch (error) {
    console.error("Error in handleGetUserAvatar:", error);
    return {
      success: false,
      error: error.message || "Unknown error fetching avatar",
    };
  }
};

export default handleGetUserAvatar;
