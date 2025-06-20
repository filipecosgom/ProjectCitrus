// Avatar cache implementation
export const avatarCache = new Map();
export const CACHE_TTL = 5 * 60 * 1000; // 5 minutes cache duration

// Clear cache for specific user or entire cache
export const clearAvatarCache = (userId) => {
  if (userId) {
    const entry = avatarCache.get(userId);
    if (entry) URL.revokeObjectURL(entry.url);
    avatarCache.delete(userId);
  } else {
    // Clear all cached avatars
    avatarCache.forEach(entry => URL.revokeObjectURL(entry.url));
    avatarCache.clear();
  }
};
