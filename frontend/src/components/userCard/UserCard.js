import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import styles from "./UserCard.css";
import UserIcon from "../userIcon/UserIcon";
import { handleGetUserAvatar } from "../../handles/handleGetUserAvatar";
import Spinner from "../spinner/spinner";
import noManager from '../../assets/photos/noManager.webp';

const UserCard = ({ user }) => {
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [managerAvatarUrl, setManagerAvatarUrl] = useState(null);
  const [loadingAvatar, setLoadingAvatar] = useState(false);
  const [loadingManagerAvatar, setLoadingManagerAvatar] = useState(false);

  // Determine status for UserIcon
  const getStatusIcon = () => {
    if (user.userIsAdmin) return "check";
    if (user.userIsDeleted) return "cross";
    if (user.accountState === "INCOMPLETE") return "stroke";
    return null;
  };

  useEffect(() => {
    if (!user) return;

    let userBlobUrl = null;
    let managerBlobUrl = null;

    const fetchUserAvatar = async () => {
      if (!user.hasAvatar) return;

      setLoadingAvatar(true);
      try {
        const result = await handleGetUserAvatar(user.id);
        if (!result.success || !result.avatar) throw new Error(result.error);
        userBlobUrl = result.avatar;
        setAvatarUrl(result.avatar);
      } catch {
        setAvatarUrl(null)
      } finally {
        setLoadingAvatar(false);
      }
    };

    const fetchManagerAvatar = async () => {
      if (!user.manager?.hasAvatar) return;

      setLoadingManagerAvatar(true);
      try {
        const result = await handleGetUserAvatar(user.manager.id);
        if (!result.success || !result.avatar) throw new Error(result.error);
        managerBlobUrl = result.avatar;
        setManagerAvatarUrl(result.avatar);
      } catch {
        setManagerAvatarUrl(null);
      } finally {
        setLoadingManagerAvatar(false);
      }
    };

    fetchUserAvatar();
    fetchManagerAvatar();

    return () => {
      if (userBlobUrl?.startsWith("blob:")) URL.revokeObjectURL(userBlobUrl);
      if (managerBlobUrl?.startsWith("blob:"))
        URL.revokeObjectURL(managerBlobUrl);
    };
  }, [user.id, user.hasAvatar, user.manager]);

  const getStatusClass = () => {
    if (user.userIsAdmin) return styles.admin;
    if (user.userIsManager) return styles.manager;
    return "";
  };

  return (
    <div className={`userCard-container ${getStatusClass()}`}>
      <div className="userCard-avatarAndInfoContainer container-user">
        {/* User Avatar */}
        <div className="userCard-avatarContainer">
          {loadingAvatar ? (
            <Spinner /> // You can replace this with a spinner
          ) : (
            <UserIcon avatar={avatarUrl} status={getStatusIcon()} />
          )}
        </div>

        {/* User Info */}
        <div className="userCard-userInfo">
          <div className="userCard-name">
            {user.name} {user.surname}
          </div>
          <div className="userCard-email">{user.email}</div>
        </div>
      </div>

      {/* Role */}
      <div className="userCard-role">{user.role.replace(/_/g, " ")}</div>

      {/* Office */}
      <div className="userCard-office">{user.office.replace(/_/g, " ")}</div>

      {/* Manager Section */}
      {user.manager && (
        <div className="userCard-avatarAndInfoContainer container-manager">
          <div className="userCard-managerAvatar">
            {loadingManagerAvatar ? (
              <Spinner />
            ) : (
              <UserIcon
                avatar={managerAvatarUrl}
                status={user.manager.status}
              />
            )}
          </div>

          <div className="userCard-managerInfo">
            <div className="userCard-managerName">
              {user.manager.name} {user.manager.surname}
            </div>
            <div className="userCard-managerEmail">{user.manager.email}</div>
          </div>
        </div>
      )}
    </div>
  );
};

UserCard.propTypes = {
  user: PropTypes.shape({
    id: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired,
    surname: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired,
    phone: PropTypes.string,
    role: PropTypes.string.isRequired,
    office: PropTypes.string.isRequired,
    municipality: PropTypes.string.isRequired,
    birthdate: PropTypes.array,
    accountState: PropTypes.string.isRequired,
    userIsAdmin: PropTypes.bool.isRequired,
    userIsManager: PropTypes.bool.isRequired,
    userIsDeleted: PropTypes.bool.isRequired,
    hasAvatar: PropTypes.bool.isRequired,
    manager: PropTypes.shape({
      id: PropTypes.number,
      name: PropTypes.string,
      surname: PropTypes.string,
    }),
  }).isRequired,
};

export default UserCard;
