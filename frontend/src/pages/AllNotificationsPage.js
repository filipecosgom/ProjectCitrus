import React, { useEffect, useState } from "react";
import useNotificationStore from "../stores/useNotificationStore";
import NotificationItem from "../components/dropdowns/NotificationItem";
import Spinner from "../components/spinner/Spinner";
import "./AllNotificationsPage.css";

export default function AllNotificationsPage() {
  const [loading, setLoading] = useState(true);
  const [allNotifications, setAllNotifications] = useState([]);
  const fetchAndSetNotifications = useNotificationStore(
    (state) => state.fetchAndSetNotifications
  );
  const messageNotifications = useNotificationStore(
    (state) => state.messageNotifications
  );
  const otherNotifications = useNotificationStore(
    (state) => state.otherNotifications
  );

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      await fetchAndSetNotifications();
      setLoading(false);
    };
    fetchData();
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    // Merge and sort all notifications by timestamp (descending)
    const all = [...messageNotifications, ...otherNotifications];
    all.sort((a, b) => {
      const aTime = Array.isArray(a.timestamp)
        ? new Date(...a.timestamp.slice(0, 6))
        : new Date(a.timestamp);
      const bTime = Array.isArray(b.timestamp)
        ? new Date(...b.timestamp.slice(0, 6))
        : new Date(b.timestamp);
      return bTime - aTime;
    });
    setAllNotifications(all);
  }, [messageNotifications, otherNotifications]);

  const handleNotificationClick = (notification) => {
    // You can implement navigation or mark as read here
    // For now, just log
    console.log("Notification clicked:", notification);
  };

  return (
    <div className="all-notifications-page">
      <h2>All Notifications</h2>
      {loading ? (
        <Spinner />
      ) : allNotifications.length === 0 ? (
        <div className="all-notifications-empty">No notifications found.</div>
      ) : (
        <div className="all-notifications-list">
          {allNotifications.map((notification) => (
            <NotificationItem
              key={notification.id}
              notification={notification}
              onClick={handleNotificationClick}
            />
          ))}
        </div>
      )}
    </div>
  );
}
