import { useEffect, useState } from "react";

// Global in-memory blob URL cache
const avatarCache = new Map();

export const useAvatarCache = (userId, hasAvatar) => {
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!userId || !hasAvatar) return;

    let isMounted = true;

    const fetchAvatar = async () => {
      if (avatarCache.has(userId)) {
        setAvatarUrl(avatarCache.get(userId));
        return;
      }

      setLoading(true);
      try {
        const response = await fetch(`/api/user/${userId}/avatar`); // adjust endpoint as needed
        if (!response.ok) throw new Error("Avatar fetch failed");

        const blob = await response.blob();
        const url = URL.createObjectURL(blob);

        avatarCache.set(userId, url);
        if (isMounted) setAvatarUrl(url);
      } catch (err) {
        if (isMounted) setError("Failed to load avatar");
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    fetchAvatar();

    return () => {
      isMounted = false;
      // ❗ Don't revoke the URL — it's cached for reuse
    };
  }, [userId, hasAvatar]);

  return { avatarUrl, loading, error };
};
