import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import styles from './UserCard.css';
import UserIcon from '../userIcon/UserIcon';
import { handleGetUserAvatar } from '../../handles/handleGetUserAvatar';

const UserCard = ({ user }) => {
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [loadingAvatar, setLoadingAvatar] = useState(false);
  const [avatarError, setAvatarError] = useState(null);

  const formattedBirthdate = user.birthdate 
    ? new Date(...user.birthdate).toLocaleDateString() 
    : 'Not specified';

  // Determine status for UserIcon
  const getStatusIcon = () => {
    if (user.userIsAdmin) return 'check';
    if (user.userIsDeleted) return 'cross';
    if (user.accountState === 'INCOMPLETE') return 'stroke';
    return null;
  };

  useEffect(() => {
    if (user.hasAvatar) {
      const fetchAvatar = async () => {
        setLoadingAvatar(true);
        try {
          const result = await handleGetUserAvatar(user.id);
          if (result.success) {
            setAvatarUrl(result.avatar);
          } else {
            setAvatarError(result.error);
          }
        } catch (error) {
          setAvatarError('Failed to load avatar');
        } finally {
          setLoadingAvatar(false);
        }
      };

      fetchAvatar();
    }

    // Cleanup function to revoke object URL when component unmounts
    return () => {
      if (avatarUrl) {
        URL.revokeObjectURL(avatarUrl);
      }
    };
  }, [user.id, user.hasAvatar]);

  const getStatusClass = () => {
    if (user.userIsAdmin) return styles.admin;
    if (user.userIsManager) return styles.manager;
    return '';
  };


  return (
    <div className={`${styles.card} ${getStatusClass()}`}>
      <div className={styles.header}>
        <div className={styles.avatarContainer}>
          <UserIcon 
            avatar={avatarUrl} 
            status={getStatusIcon()} 
          />
        </div>
        <div className={styles.titles}>
          <h3>{user.name} {user.surname}</h3>
          <p className={styles.role}>{user.role.replace(/_/g, ' ')}</p>
          {avatarError && (
            <small className={styles.avatarError}>
              Avatar: {avatarError}
            </small>
          )}
        </div>
      </div>

      {/* Rest of the card content remains the same */}
      <div className={styles.statusBar}>
        <span className={`${styles.status} ${
          user.accountState === 'COMPLETE' ? styles.complete : styles.incomplete
        }`}>
          {user.accountState}
        </span>
        {user.userIsAdmin && <span className={styles.badge}>Admin</span>}
        {user.userIsManager && <span className={styles.badge}>Manager</span>}
      </div>

      <div className={styles.details}>
        <div className={styles.detailItem}>
          <span className={styles.icon}>📧</span>
          <span>{user.email}</span>
        </div>
        
        <div className={styles.detailItem}>
          <span className={styles.icon}>📱</span>
          <span>{user.phone || 'Not specified'}</span>
        </div>
        
        <div className={styles.detailItem}>
          <span className={styles.icon}>📍</span>
          <span>{user.office.replace(/_/g, ' ')} • {user.municipality}</span>
        </div>
        
        <div className={styles.detailItem}>
          <span className={styles.icon}>🎂</span>
          <span>{formattedBirthdate}</span>
        </div>
      </div>

      {user.manager && (
        <div className={styles.managerSection}>
          <div className={styles.detailItem}>
            <span className={styles.icon}>👤</span>
            <div>
              <small>Manager</small>
              <div>{user.manager.name} {user.manager.surname}</div>
            </div>
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
      surname: PropTypes.string
    })
  }).isRequired
};

export default UserCard;