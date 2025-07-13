import axios from "axios";
import { apiBaseUrl } from "../config";

const notificationsEndpoint = `${apiBaseUrl}/notifications/`;

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
