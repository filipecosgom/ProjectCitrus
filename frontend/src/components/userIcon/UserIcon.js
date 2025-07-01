import React, { useState, useEffect } from "react";
import "./UserIcon.css";
import { generateInitialsAvatar } from "../../components/userOffcanvas/UserOffcanvas";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import Spinner from "../spinner/spinner";

export default function UserIcon({ user}) {
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let userBlobUrl = null;
    if (!user || !user.hasAvatar) {
      setAvatarUrl(null);
      return;
    }
    setLoading(true);
    handleGetUserAvatar(user.id)
      .then((result) => {
        if (result.success && result.avatar) {
          userBlobUrl = result.avatar;
          setAvatarUrl(result.avatar);
        } else {
          setAvatarUrl(null);
        }
      })
      .catch(() => setAvatarUrl(null))
      .finally(() => setLoading(false));
    return () => {
      if (userBlobUrl?.startsWith("blob:")) URL.revokeObjectURL(userBlobUrl);
    };
  }, [user?.id, user?.hasAvatar]);

  if (loading) {
    return (
      <div className="user-icon">
        <Spinner />
      </div>
    );
  }

  return (
    <div className="user-icon">
      <img
        src={user.hasAvatar ? avatarUrl : generateInitialsAvatar(user.name, user.surname)}
        alt={`${user.name} ${user.surname}`}
      />
    </div>
  );
}