import React, { useState, useEffect } from "react";
import "./UserIcon.css";
import { generateInitialsAvatar } from "../../components/userOffcanvas/UserOffcanvas";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import { FaUserCircle } from "react-icons/fa";

export default function UserIcon({ user, avatar }) {
  // ✅ HOOKS PRIMEIRO - SEMPRE NA MESMA ORDEM
  const [avatarUrl, setAvatarUrl] = useState(avatar || null);
  const [loading, setLoading] = useState(false);

  // ✅ PROTEÇÃO DEFENSIVA para hasAvatar
  const hasAvatar = user?.hasAvatar ?? false;
  const userName = user?.name || "";
  const userSurname = user?.surname || "";
  const onlineStatus = user?.onlineStatus || false;

  useEffect(() => {
    // ✅ VERIFICAÇÃO dentro do useEffect
    if (!user || !user.id) {
      setAvatarUrl(null);
      return;
    }

    if (avatar) {
      setAvatarUrl(avatar);
      return;
    }

    let userBlobUrl = null;
    if (!hasAvatar) {
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
  }, [user?.id, hasAvatar, avatar]);

  // ✅ VERIFICAÇÃO APÓS OS HOOKS
  if (!user) {
    console.warn("⚠️ UserIcon: user is null - rendering placeholder");
    return (
      <div className="user-icon user-icon-placeholder">
        <FaUserCircle style={{ fontSize: "38px", color: "#ccc" }} />
      </div>
    );
  }

  return (
    <div className="user-icon">
      <img
        src={
          hasAvatar && avatarUrl
            ? avatarUrl
            : generateInitialsAvatar(userName, userSurname)
        }
        alt={`${userName} ${userSurname}`}
        onError={(e) => {
          // ✅ FALLBACK se imagem falhar
          e.target.src = generateInitialsAvatar(userName, userSurname);
        }}
      />
      {/* Status badge: green if online, gray if offline */}
      <div
        className={`user-icon-status ${onlineStatus ? "online" : "offline"}`}
        title={onlineStatus ? "Online" : "Offline"}
      />
    </div>
  );
}
