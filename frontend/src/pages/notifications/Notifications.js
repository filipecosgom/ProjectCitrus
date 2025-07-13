import React, { useState, useEffect, useRef } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import SearchBar from "../../components/searchBar/SearchBar";
import SortControls from "../../components/sortControls/SortControls";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/Spinner";
import NotificationCard from "../../components/notificationCard/NotificationCard";
import {
  FaBell,
  FaEnvelope,
  FaAward,
  FaBook,
  FaCheckCircle,
  FaEyeSlash,
  FaEye,
} from "react-icons/fa";
import useAuthStore from "../../stores/useAuthStore";
import {
  fetchNotifications,
  markNotificationAsRead,
} from "../../api/notificationsApi";
import handleNotification from "../../handles/handleNotification";
import "./Notifications.css";

export default function Notifications() {
  const { t } = useTranslation();
  const [urlSearchParams, setUrlSearchParams] = useSearchParams();
  const navigate = useNavigate();

  const [notifications, setNotifications] = useState([]);
  const [filteredNotifications, setFilteredNotifications] = useState([]);
  const [resultsLoading, setResultsLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 10,
    total: 0,
  });

  const [searchParams, setSearchParams] = useState({
    query: "",
    searchType: "content",
    limit: 10,
    type: "",
    isRead: "",
  });

  const [sort, setSort] = useState({
    sortBy: "creationDate",
    sortOrder: "DESCENDING",
  });

  const user = useAuthStore((state) => state.user);
  const containerRef = useRef();

  // Fetch notifications from API
  const loadNotifications = async () => {
    try {
      setResultsLoading(true);
      const result = await fetchNotifications();

      if (result.success && result.data.success) {
        setNotifications(result.data.data || []);
      } else {
        handleNotification("error", "errorLoadingNotifications");
        setNotifications([]);
      }
    } catch (error) {
      console.error("Error loading notifications:", error);
      handleNotification("error", "errorLoadingNotifications");
      setNotifications([]);
    } finally {
      setResultsLoading(false);
      setPageLoading(false);
    }
  };

  // Load notifications on mount
  useEffect(() => {
    loadNotifications();
  }, []);

  // Apply search, filter, sort, and pagination
  useEffect(() => {
    let data = [...notifications];

    // Search/filter
    if (searchParams.query) {
      const query = searchParams.query.toLowerCase();
      data = data.filter((notification) => {
        switch (searchParams.searchType) {
          case "content":
            return notification.content?.toLowerCase().includes(query);
          case "type":
            return notification.type?.toLowerCase().includes(query);
          default:
            return notification.content?.toLowerCase().includes(query);
        }
      });
    }

    // Filter by type
    if (searchParams.type) {
      data = data.filter(
        (notification) => notification.type === searchParams.type
      );
    }

    // Filter by read status
    if (searchParams.isRead !== "") {
      const isRead = searchParams.isRead === "true";
      data = data.filter((notification) => notification.read === isRead);
    }

    // Sort
    if (sort.sortBy) {
      data.sort((a, b) => {
        let aValue = a[sort.sortBy];
        let bValue = b[sort.sortBy];

        // Handle date sorting
        if (sort.sortBy === "creationDate") {
          aValue = new Date(aValue);
          bValue = new Date(bValue);
        }

        if (sort.sortOrder === "ASCENDING") {
          return aValue > bValue ? 1 : -1;
        } else {
          return aValue < bValue ? 1 : -1;
        }
      });
    }

    // Pagination
    const start = pagination.offset;
    const end = start + pagination.limit;
    setFilteredNotifications(data.slice(start, end));
    setPagination((prev) => ({
      ...prev,
      total: data.length,
    }));
  }, [notifications, searchParams, sort, pagination.offset, pagination.limit]);

  // Handlers
  const handleSearch = (query, searchType, limit, filters = {}) => {
    setSearchParams((prev) => ({
      ...prev,
      query,
      searchType,
      limit,
      ...filters,
    }));
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  const handleNotificationClick = async (notification) => {
    try {
      // Mark as read if not already read
      if (!notification.read) {
        const result = await markNotificationAsRead(notification.id);
        if (result.success) {
          // Update local state
          setNotifications((prev) =>
            prev.map((n) =>
              n.id === notification.id ? { ...n, read: true } : n
            )
          );
        }
      }

      // Handle navigation based on notification type
      switch (notification.type) {
        case "MESSAGE":
          navigate(`/messages?id=${notification.senderId}`);
          break;
        case "EVALUATION":
          navigate("/appraisals");
          break;
        case "COURSE":
          navigate("/courses");
          break;
        default:
          break;
      }
    } catch (error) {
      console.error("Error handling notification click:", error);
      handleNotification("error", "errorProcessingNotification");
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      const unreadNotifications = notifications.filter((n) => !n.read);

      if (unreadNotifications.length === 0) {
        handleNotification("info", "noUnreadNotifications");
        return;
      }

      // Mark all as read (you might need to implement a bulk endpoint)
      const promises = unreadNotifications.map((notification) =>
        markNotificationAsRead(notification.id)
      );

      const results = await Promise.all(promises);
      const successCount = results.filter((r) => r.success).length;

      if (successCount > 0) {
        setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
        handleNotification("success", "notificationsMarkedAsRead", {
          count: successCount,
        });
      }
    } catch (error) {
      console.error("Error marking all as read:", error);
      handleNotification("error", "errorMarkingNotifications");
    }
  };

  // Get notification icon
  const getNotificationIcon = (type) => {
    switch (type) {
      case "MESSAGE":
        return <FaEnvelope className="notification-type-icon message" />;
      case "EVALUATION":
        return <FaAward className="notification-type-icon evaluation" />;
      case "COURSE":
        return <FaBook className="notification-type-icon course" />;
      default:
        return <FaBell className="notification-type-icon default" />;
    }
  };

  // Format date
  const formatDate = (dateString) => {
    try {
      const date = new Date(dateString);
      const now = new Date();
      const diffMs = now - date;
      const diffHours = diffMs / (1000 * 60 * 60);
      const diffDays = diffMs / (1000 * 60 * 60 * 24);

      if (diffHours < 1) {
        return t("justNow");
      } else if (diffHours < 24) {
        return t("hoursAgo", { count: Math.floor(diffHours) });
      } else if (diffDays < 7) {
        return t("daysAgo", { count: Math.floor(diffDays) });
      } else {
        return date.toLocaleDateString();
      }
    } catch (err) {
      return t("invalidDate");
    }
  };

  if (pageLoading) {
    return <Spinner />;
  }

  const notificationFilters = {
    searchTypes: [
      { value: "content", label: t("notificationSearchContent") },
      { value: "type", label: t("notificationSearchType") },
    ],
    filters: [
      {
        key: "type",
        label: t("notificationType"),
        type: "select",
        options: [
          { value: "", label: t("allTypes") },
          { value: "MESSAGE", label: t("notificationTypeMessage") },
          { value: "EVALUATION", label: t("notificationTypeEvaluation") },
          { value: "COURSE", label: t("notificationTypeCourse") },
        ],
      },
      {
        key: "isRead",
        label: t("notificationStatus"),
        type: "select",
        options: [
          { value: "", label: t("allStatuses") },
          { value: "false", label: t("notificationUnread") },
          { value: "true", label: t("notificationRead") },
        ],
      },
    ],
  };

  const sortFields = [
    { value: "creationDate", label: t("notificationSortDate") },
    { value: "type", label: t("notificationSortType") },
    { value: "read", label: t("notificationSortStatus") },
  ];

  return (
    <div className="notifications-container" ref={containerRef}>
      <div className="notifications-header">
        <div className="notifications-searchBarAndButton">
          <SearchBar
            onSearch={handleSearch}
            searchTypes={notificationFilters.searchTypes}
            filters={notificationFilters.filters}
            placeholder={t("notificationSearchPlaceholder")}
          />
          <button
            className="mark-all-read-btn"
            onClick={handleMarkAllAsRead}
            disabled={resultsLoading}
          >
            <FaCheckCircle className="mark-all-read-icon" />
            <span className="mark-all-read-text">{t("markAllAsRead")}</span>
          </button>
        </div>
      </div>

      <SortControls
        fields={sortFields}
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={handleSortChange}
        isCardMode={true}
      />

      <div className="notifications-content">
        {resultsLoading ? (
          <div className="notifications-loading">
            <Spinner />
          </div>
        ) : filteredNotifications.length === 0 ? (
          <div className="notifications-empty">
            <FaBell className="notifications-empty-icon" />
            <p>{t("noNotificationsFound")}</p>
          </div>
        ) : (
          <div className="notifications-grid">
            {filteredNotifications.map((notification) => (
              <div
                key={notification.id}
                className={`notification-card ${
                  !notification.read ? "unread" : "read"
                }`}
                onClick={() => handleNotificationClick(notification)}
              >
                <div className="notification-card-header">
                  <div className="notification-type">
                    {getNotificationIcon(notification.type)}
                    <span className="notification-type-text">
                      {t(`notificationType${notification.type}`)}
                    </span>
                  </div>
                  <div className="notification-status">
                    {notification.read ? (
                      <FaEye className="notification-status-icon read" />
                    ) : (
                      <FaEyeSlash className="notification-status-icon unread" />
                    )}
                  </div>
                </div>

                <div className="notification-card-content">
                  <p className="notification-content">{notification.content}</p>
                  {notification.messageCount > 1 && (
                    <div className="notification-message-count">
                      {t("notificationMessageCount", {
                        count: notification.messageCount,
                      })}
                    </div>
                  )}
                </div>

                <div className="notification-card-footer">
                  <span className="notification-date">
                    {formatDate(notification.creationDate)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <Pagination
        offset={pagination.offset}
        limit={pagination.limit}
        total={pagination.total}
        onChange={handlePageChange}
      />
    </div>
  );
}
