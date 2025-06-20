import { fetchUserAvatar } from "../api/userApi";
import { avatarCache, CACHE_TTL, clearAvatarCache } from '../utils/AvatarCache';

export const handleGetUserAvatar = async (id) => {
  try {
    // Check cache first
    const cached = avatarCache.get(id);
    if (cached && Date.now() - cached.timestamp < CACHE_TTL) {
      return {
        success: true,
        avatar: cached.url,
        userData: cached.userData,
        fromCache: true
      };
    }

    // Fetch fresh avatar if not in cache or expired
    const response = await fetchUserAvatar(id);
    
    if (!response.success) {
      console.error('Avatar fetch failed:', response.message);
      return {
        success: false,
        error: response.message || 'Failed to fetch avatar'
      };
    }

    // Create blob URL
    const imageUrl = URL.createObjectURL(response.blob);

    // Update cache (clean up old entry if exists)
    if (avatarCache.has(id)) {
      URL.revokeObjectURL(avatarCache.get(id).url);
    }
    
    avatarCache.set(id, {
      url: imageUrl,
      timestamp: Date.now(),
      userData: response.data,
      contentType: response.contentType
    });

    return {
      success: true,
      avatar: imageUrl,
      userData: response.data,
      fromCache: false
    };

  } catch (error) {
    console.error('Error in handleGetUserAvatar:', error);
    return {
      success: false,
      error: error.message || 'Unknown error fetching avatar'
    };
  }
};

// Default export remains the same
export default handleGetUserAvatar;