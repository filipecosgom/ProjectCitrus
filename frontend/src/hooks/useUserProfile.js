import { useState, useEffect, useCallback } from "react";
import handleGetUserInformation from "../handles/handleGetUserInformation";
import handleGetUserAvatar from "../handles/handleGetUserAvatar";

export default function useUserProfile(userId) {
  const [user, setUser] = useState(null);
  const [userAvatar, setUserAvatar] = useState(null);
  const [managerAvatar, setManagerAvatar] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchUserInformation = useCallback(async () => {
    if (!userId) return;
    setLoading(true);
    try {
      const userInfo = await handleGetUserInformation(userId);
      if (userInfo) {
        setUser(userInfo);
      }
      if (userInfo?.hasAvatar) {
        const userAvatar = await handleGetUserAvatar(userId);
        if (userAvatar) {
          setUserAvatar(userAvatar.avatar);
        }
      }
      if (userInfo?.manager?.hasAvatar) {
        const managerAvatar = await handleGetUserAvatar(userInfo.manager.id);
        if (managerAvatar) {
          setManagerAvatar(managerAvatar.avatar);
        }
      }
    } catch (error) {
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchUserInformation();
  }, [fetchUserInformation]);

  return {
    user,
    userAvatar,
    managerAvatar,
    loading,
    refreshUser: fetchUserInformation,
  };
}
