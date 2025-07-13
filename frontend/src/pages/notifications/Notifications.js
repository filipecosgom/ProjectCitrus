import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { handleUpdateNotification } from "../../handles/handleNotificationApi";
import Pagination from "../../components/pagination/Pagination";
import SortControls from "../../components/sortControls/SortControls";
import Spinner from "../../components/spinner/Spinner";
import NotificationRow from "../../components/dropdowns/NotificationRow";
import SearchBar from "../../components/searchbar/Searchbar";
import useNotificationStore from "../../stores/useNotificationStore";
import "./Notifications.css";
import { FaEye } from "react-icons/fa";

export default function Notifications() {
  const { t } = useTranslation();
  const messageNotifications = useNotificationStore(
    (state) => state.messageNotifications
  );
  const otherNotifications = useNotificationStore(
    (state) => state.otherNotifications
  );
  const fetchAndSetNotifications = useNotificationStore(
    (state) => state.fetchAndSetNotifications
  );
  const markAllMessagesAsRead = useNotificationStore(
    (state) => state.markAllMessagesAsRead
  );
  const markAllOthersAsRead = useNotificationStore(
    (state) => state.markAllOthersAsRead
  );
  const markMessageAsSeen = useNotificationStore(
    (state) => state.markMessageAsSeen
  );
  const markOtherAsSeen = useNotificationStore(
    (state) => state.markOtherAsSeen
  );
  const setMessageUnreadCountToZero = useNotificationStore(
    (state) => state.setMessageUnreadCountToZero
  );
  const navigate = useNavigate();
  const [pageLoading, setPageLoading] = useState(true);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 10,
    total: 0,
  });
  const [notifications, setNotifications] = useState([]);
  const [sort, setSort] = useState({ sortBy: "date", sortOrder: "DESCENDING" });
  const [searchQuery, setSearchQuery] = useState("");
  const containerRef = useRef();
  const [selectedNotification, setNotificationSelection] = useState(null);
  const [selectedNotifications, setSelectedNotifications] = useState(new Set());

  // Fetch notifications from API/store on mount and mark all as read
  useEffect(() => {
    const markAllAsRead = async () => {
      await fetchAndSetNotifications();
      // Mark all as read in store
      markAllMessagesAsRead();
      markAllOthersAsRead();
      // Mark all as read in backend
      for (const n of messageNotifications.filter(
        (n) => !n.notificationIsRead
      )) {
        await handleUpdateNotification({
          notificationId: n.id,
          notificationIsRead: true,
        });
      }
      for (const n of otherNotifications.filter((n) => !n.notificationIsRead)) {
        await handleUpdateNotification({
          notificationId: n.id,
          notificationIsRead: true,
        });
      }
      setPageLoading(false);
    };
    markAllAsRead();
    // eslint-disable-next-line
  }, []);

  // Merge notifications from store whenever they change
  useEffect(() => {
    // Merge all notifications
    const all = [...messageNotifications, ...otherNotifications];
    setNotifications(all);
    setPagination((prev) => ({ ...prev, total: all.length }));
  }, [messageNotifications, otherNotifications]);

  // Sort notifications when notifications or sort changes
  const sortedNotifications = React.useMemo(() => {
    const arr = [...notifications];
    if (sort.sortBy === "date") {
      arr.sort((a, b) => {
        const aTime = Array.isArray(a.timestamp)
          ? new Date(...a.timestamp.slice(0, 6))
          : new Date(a.timestamp);
        const bTime = Array.isArray(b.timestamp)
          ? new Date(...b.timestamp.slice(0, 6))
          : new Date(b.timestamp);
        return sort.sortOrder === "DESCENDING" ? bTime - aTime : aTime - bTime;
      });
    } else if (sort.sortBy === "type") {
      arr.sort((a, b) => {
        if (a.type === b.type) return 0;
        if (sort.sortOrder === "ASCENDING") {
          return a.type.localeCompare(b.type);
        } else {
          return b.type.localeCompare(a.type);
        }
      });
    }
    return arr;
  }, [notifications, sort]);

  // Filter notifications by search query (content)
  const filteredNotifications = searchQuery
    ? sortedNotifications.filter((n) =>
        (n.content || "").toLowerCase().includes(searchQuery.toLowerCase())
      )
    : sortedNotifications;

  // Pagination: get current page notifications
  const paginatedNotifications = filteredNotifications.slice(
    pagination.offset,
    pagination.offset + pagination.limit
  );
  // Handle search from SearchBar
  const handleSearch = (query, _searchType, newLimit) => {
    setSearchQuery(query);
    setPagination((prev) => ({
      ...prev,
      offset: 0,
      limit: newLimit || prev.limit,
    }));
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  // Mark all notifications as seen
  const handleMarkAllAsSeen = async () => {
    const unseen = notifications.filter((n) => !n.notificationIsSeen);
    for (const n of unseen) {
      await handleUpdateNotification({
        notificationId: n.id,
        notificationIsSeen: true,
        notificationIsRead: true,
      });
      if (n.type === "MESSAGE") {
        markMessageAsSeen(n.id);
        setMessageUnreadCountToZero(n.id);
        markAllMessagesAsRead();
      } else {
        markOtherAsSeen(n.id);
        markAllOthersAsRead();
      }
    }
    setSelectedNotifications(new Set());
  };

  if (pageLoading) {
    return <Spinner />;
  }

  // Handle click on a notification: mark as seen and redirect
  const handleNotificationClick = async (notification) => {
    if (notification.type === "MESSAGE") {
      if (!notification.notificationIsSeen) {
        await handleUpdateNotification({
          notificationId: notification.id,
          notificationIsSeen: true,
        });
        markMessageAsSeen(notification.id);
      }
      setMessageUnreadCountToZero(notification.id);
      navigate(`/messages?id=${notification.sender?.id}`);
    } else {
      if (!notification.notificationIsSeen) {
        await handleUpdateNotification({
          notificationId: notification.id,
          notificationIsSeen: true,
        });
        markOtherAsSeen(notification.id);
      }
      // Redirect based on type
      if (notification.type === "APPRAISAL") {
        navigate(`/profile?id=${notification.recipient?.id}&tab=appraisals`);
      } else if (notification.type === "CYCLE") {
        navigate(`/appraisals?state=IN_PROGRESS`);
      } else if (notification.type === "COURSE") {
        navigate(`/profile?id=${notification.recipient?.id}&tab=training`);
      } else {
        navigate(`/notifications`);
      }
    }
  };

  const handleNotificationSelection = (notification, isSelected) => {
    const newSelectedNotifications = new Set(selectedNotifications);
    if (isSelected) {
      newSelectedNotifications.add(notification);
    } else {
      newSelectedNotifications.delete(notification);
    }
    setSelectedNotifications(newSelectedNotifications);
  };

  return (
    <div className="notifications-container" ref={containerRef}>
      <SearchBar
        onSearch={handleSearch}
        defaultValues={{
          query: searchQuery,
          searchType: "content",
          limit: pagination.limit,
        }}
        limitOptions={[5, 10, 20]}
        actions={
          <button
            className={`notifications-markAsSeen-btn${
              selectedNotifications.size === 0 ? " disabled" : ""
            }`}
            onClick={handleMarkAllAsSeen}
            disabled={selectedNotifications.size === 0}
          >
            <FaEye className="notifications-markAsSeen-icon" />
            {t("notifications.markAllAsSeen")}
          </button>
        }
        placeholder={t("searchBarPlaceholder", {
          type: t("notifications.content", "content"),
        })}
      />
      <SortControls
        fields={[
          {
            id: "notifications.sortByType",
            className: "notifications-sortByType",
            key: "type",
            label: () => t("notifications.sortByType", "Type"),
          },
          {
            id: "notifications.sortByDate",
            className: "notifications-sortByDate",
            key: "date",
            label: () => t("notifications.sortByDate", "Date"),
          },
        ]}
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={({ sortBy, sortOrder }) => setSort({ sortBy, sortOrder })}
        className="notifications-sort-controls"
      />
      <div className="notifications-list">
        {paginatedNotifications.length === 0 ? (
          <div className="notifications-empty">
            {t("notifications.noNotificationsFound")}
          </div>
        ) : (
          paginatedNotifications.map((notification) => (
            <NotificationRow
              key={notification.id}
              notification={notification}
              onClick={handleNotificationClick}
              isSelected={selectedNotifications.has(notification.id)}
              onSelectionChange={handleNotificationSelection}
            />
          ))
        )}
      </div>
      <Pagination
        offset={pagination.offset}
        limit={pagination.limit}
        total={filteredNotifications.length}
        onChange={handlePageChange}
      />
    </div>
  );
}
