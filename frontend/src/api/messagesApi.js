/**
 * API module for messaging.
 * Provides functions to fetch, send, and update messages and conversations.
 * @module messagesApi
 */
import axios from "axios";
import { apiBaseUrl } from "../config";

const messagesEndpoint = `${apiBaseUrl}/messages/`;

/**
 * Fetches messages between the current user and another user.
 * @async
 * @param {number|string} otherUserId - Other user's ID.
 * @returns {Promise<Object>} Messages data.
 */
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

/**
 * Fetches all conversations for the current user.
 * @async
 * @returns {Promise<Object>} Conversations data.
 */
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

/**
 * Sends a message to another user.
 * @async
 * @param {number|string} receiverId - Receiver's user ID.
 * @param {string} content - Message content.
 * @returns {Promise<Object>} Send result.
 */
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

/**
 * Marks all messages in a conversation as read.
 * @async
 * @param {number|string} senderId - Sender's user ID.
 * @returns {Promise<Object>} Operation result.
 */
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

/**
 * Fetches conversation previews for the message dropdown.
 * @async
 * @returns {Promise<Object>} List of conversation previews.
 */
export const fetchConversationPreviews = async () => {
  try {
    const response = await axios.get(`${messagesEndpoint}conversations`, {
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
