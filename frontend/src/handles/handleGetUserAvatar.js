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