/**
 * API module for handling notification-related operations.
 * Provides functions to fetch, update, and mark notifications as read for the current user.
 * Uses JWT authentication and expects JSON responses from the backend.
 * @module notificationsApi
 */
import axios from "axios";
import { apiBaseUrl } from "../config";

const notificationsEndpoint = `${apiBaseUrl}/notifications/`;

/**
 * Fetches all notifications for the current user.
 * @async
 * @function getAllNotifications
 * @returns {Promise<Object>} An object containing success status, HTTP status, and notification data or error details.
 */
export const getAllNotifications = async () => {
  try {
    const response = await axios.get(`${notificationsEndpoint}`, {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

// ✅ UPDATE A NOTIFICATION (isRead, isSeen, etc)
/**
 * Updates a notification's properties (e.g., isRead, isSeen) for the current user.
 * Sends a PATCH request with the update payload.
 * @async
 * @function updateNotification
 * @param {Object} notificationUpdate - The update payload for the notification.
 * @returns {Promise<Object>} An object containing success status, HTTP status, and updated notification data or error details.
 */
export const updateNotification = async (notificationUpdate) => {
  try {
    // PATCH to /notifications/{id} with the update payload
    const response = await axios.patch(
      `${notificationsEndpoint}`,
      notificationUpdate,
      {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};
// ✅ FUNÇÃO CORRIGIDA PARA USAR JWT
/**
 * Marks all message notifications as read for the current user.
 * Uses JWT authentication and sends a PUT request to the backend.
 * @async
 * @function markMessageNotificationsAsRead
 * @returns {Promise<Object>} An object containing success status, HTTP status, and updated notification data or error details.
 */
export const markMessageNotificationsAsRead = async () => {
  try {
    const response = await axios.put(
      `${notificationsEndpoint}mark-messages-read`,
      {},
      {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};
