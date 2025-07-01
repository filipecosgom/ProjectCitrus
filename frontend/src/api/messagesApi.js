import axios from "axios";
import { apiBaseUrl } from "../config";

const messagesEndpoint = `${apiBaseUrl}/messages/`;

// Fetch messages between the current user and another user (by userId)
export const fetchMessages = async (otherUserId) => {
  try {
    const response = await axios.get(`${messagesEndpoint}${otherUserId}`, {
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

// Fetch all conversations for the current user
export const fetchAllConversations = async () => {
  try {
    const response = await axios.get(`${messagesEndpoint}all`, {
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

// Send a message to another user (by userId)
export const sendMessageApi = async (receiverId, content) => {
  try {
    const payload = {
      receiverId,
      content,
    };
    const response = await axios.post(`${messagesEndpoint}`, payload, {
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

// Mark all messages in a conversation as read (senderId = other user's id)
export const readConversationApi = async (senderId) => {
  try {
    const response = await axios.patch(
      `${messagesEndpoint}${senderId}`,
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
