import { uploadUserAvatar } from "../api/userApi";

// Separate avatar update function
export default async function handleUpdateAvatar(userId, avatarFile) {
  if (!avatarFile) return { success: true }; // No avatar to update
  const response = await uploadUserAvatar(userId, avatarFile);
    if (response.data?.success) {
      return { 
        success: true,
        avatar: response.data.avatar // Return new avatar URL/filename
      };
    }
}