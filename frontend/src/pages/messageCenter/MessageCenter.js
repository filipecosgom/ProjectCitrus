import React, { useState, useEffect } from "react";
import useMessageStore from "../../stores/useMessageStore";
import useAuthStore from "../../stores/useAuthStore";
import { FaPaperPlane } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import useWebSocketChat from "../../websockets/useWebSocketChat";
import "./MessageCenter.css";
import handleNotification from "../../handles/handleNotification";
import { useLocation, useNavigate } from "react-router-dom";
import { handleReadConversation } from "../../handles/handleReadConversation";
import { handleSendMessageViaApi } from "../../handles/handleSendMessageViaApi";
import UserIcon from "../../components/userIcon/UserIcon";
import UserSearchBar from "../../components/userSearchBar/UserSearchBar";
import handleGetUserInformation from "../../handles/handleGetUserInformation";
import { transformArrayDatetoDate, dateToFormattedTime } from "../../utils/utilityFunctions";

export const MessageCenter = () => {
  const navigate = useNavigate();
  const webSocketChat = useWebSocketChat();
  const { sendMessage } = useWebSocketChat();
  const userToChat = new URLSearchParams(useLocation().search).get("id");
  const { user } = useAuthStore();
  const {
    conversations,
    selectedUser,
    setSelectedUser,
    fetchAllConversations,
    messages,
    fetchUserConversation,
    addLocalMessage,
    updateMessageStatus,
  } = useMessageStore();
  const [messageInput, setMessageInput] = useState("");
  const [searchSelectedUser, setSearchSelectedUser] = useState(null);
  const { t } = useTranslation();
  const messagesEndRef = React.useRef(null);

  useEffect(() => {
    const getUserInformation = async () => {
      await fetchAllConversations();

      if (userToChat) {
        const updatedConversations = useMessageStore.getState().conversations;
        const userFromURL = updatedConversations.find(
          (u) => u.id === Number(userToChat)
        );

        if (!userFromURL) {
          // Fetch full user info before adding
          const userInfo = await handleGetUserInformation(userToChat);
          if (userInfo) {
            useMessageStore.getState().addNewUserToConversation(userInfo);
            setSelectedUser(userInfo);
          }
        } else {
          if (!selectedUser || selectedUser.username !== userToChat) {
            setSelectedUser(userFromURL);
          }
        }
      }
    };
    getUserInformation();
  }, [userToChat]);

  useEffect(() => {
    if (selectedUser) {
      navigate(`/messages?id=${selectedUser.id}`, { replace: true });
      handleReadConversation(selectedUser.id);
      fetchUserConversation(selectedUser.id);
    }
  }, [selectedUser]);

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [selectedUser, messages]);

  const handleSendMessage = async () => {
    if (messageInput.trim() !== "") {
      // Create a temporary message with a local ID
      const localId = `localId-${Date.now()}`;
      const newMessage = {
        messageId: localId,
        message: messageInput,
        senderId: user.id,
        formattedTimestamp: new Date(),
        status: "sending",
      };
      addLocalMessage(newMessage);
      setMessageInput("");

      if (sendMessage(selectedUser.id, messageInput)) {
        updateMessageStatus(localId, "not_read");
      } else {
        try {
          await handleSendMessageViaApi(selectedUser.id, messageInput);
          handleNotification("success", "chatWsClosedMessageSent");
          updateMessageStatus(localId, "sent");
          setMessageInput(""); // Clear input after sending
        } catch (error) {
          handleNotification("error", "chatWsClosedMessageSent");
          updateMessageStatus(localId, "failed");
        }
      }
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleSearchUserSelect = (user) => {
    if (!user) return;
    // Add to conversations if not present
    if (!conversations.some((u) => u.id === user.id)) {
      // Add the full user object, not just the ID
      useMessageStore.getState().addNewUserToConversation(user);
    }
    setSelectedUser(user);
    setSearchSelectedUser(null); // Clear search selection
  };

  // Helper to robustly convert any date (array or string) to JS Date
  function toDateUniversal(date) {
    if (!date) return null;
    if (Array.isArray(date)) {
      return transformArrayDatetoDate(date);
    }
    // If already a Date
    if (date instanceof Date) return date;
    // If string
    return new Date(date);
  }

  // Formats message timestamps (today: HH:mm, else: DD/MM/YYYY HH:mm)
  function formatMessageTimestamp(date) {
    const d = toDateUniversal(date);
    if (!d) return "";
    const now = new Date();
    const isToday =
      d.getDate() === now.getDate() &&
      d.getMonth() === now.getMonth() &&
      d.getFullYear() === now.getFullYear();
    const pad = (n) => n.toString().padStart(2, "0");
    if (isToday) {
      return dateToFormattedTime(d);
    } else {
      return (
        `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()} ` +
        dateToFormattedTime(d)
      );
    }
  }

  // Formats last seen (today: HH:mm, else: DD/MM/YYYY HH:mm)
  function formatLastSeen(date) {
    if (!date) return "";
    const d = toDateUniversal(date);
    if (!d) return "";
    const now = new Date();
    const isToday =
      d.getDate() === now.getDate() &&
      d.getMonth() === now.getMonth() &&
      d.getFullYear() === now.getFullYear();
    const pad = (n) => n.toString().padStart(2, "0");
    if (isToday) {
      return dateToFormattedTime(d);
    } else {
      return (
        `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()} ` +
        dateToFormattedTime(d)
      );
    }
  }

  return (
    <div className="chat-container">
      {/* Left sidebar - User list (hidden on mobile) */}
      <div className="chat-user-list">
        {/* User SearchBar for starting new conversations */}
        <UserSearchBar
          selectedUser={searchSelectedUser}
          onUserSelect={handleSearchUserSelect}
          placeholder={t("messageCenter.searchPlaceholder")}
          maxResults={30}
          showUserInfo={true}
          compact={true}
          className="message-center-search"
        />
        <div className="chat-user-list-items">
          {conversations.map((user) => (
            <div
              key={user.id}
              className={`chat-user-item ${
                selectedUser?.id === user.id ? "selected" : ""
              }`}
              onClick={() => setSelectedUser(user)}
            >
              <div className="userCard-avatarAndInfoContainer container-user">
                {/* User Avatar */}
                <div className="userCard-avatarContainer">
                  <UserIcon user={user} />
                </div>
                {/* User Info */}
                <div className="userCard-userInfo">
                  <div className="userCard-name">
                    {user.name} {user.surname}
                  </div>
                  <div className="userCard-email">
                    {user.email}
                    {!user.onlineStatus && user.lastSeen && (
                      <div className="userCard-last-seen">
                        {t("messageCenter.lastOnlineAt")}{" "}
                        {formatLastSeen(user.lastSeen)}
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Main chat area */}
      <div className="chat-area">
        {/* Mobile-only chat header for selected user */}
        {selectedUser && (
          <div className="chat-header-mobile">
            <div className="userCard-avatarAndInfoContainer container-user">
              <div className="userCard-avatarContainer">
                <UserIcon user={selectedUser} />
              </div>
              <div className="userCard-userInfo">
                <div className="userCard-name">
                  {selectedUser.name} {selectedUser.surname}
                </div>
                <div className="userCard-email">{selectedUser.email}</div>
                {!selectedUser.onlineStatus && selectedUser.lastSeen && (
                  <div className="userCard-last-seen">
                    {t("messageCenter.lastOnlineAt")}{" "}
                    {formatLastSeen(selectedUser.lastSeen)}
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
        {selectedUser ? (
          <>
            {/* Messages */}
            <div className="chat-messages-container">
              {messages.map((message, index) => (
                <div
                  key={index}
                  className={`chat-message ${
                    message.senderId === user.id ? "sent" : "received"
                  }`}
                >
                  <div className="chat-message-content">
                    <p>{message.message}</p>
                    <div className="chat-message-meta">
                      <span className="chat-message-time">
                        {formatMessageTimestamp(message.formattedTimestamp)}
                        <span
                          className={`chat-status-icon-${message.status}`}
                        >
                          {message.status === "sending" && t("messageCenter.statusSending")}
                          {message.status === "failed" && t("messageCenter.statusFailed")}
                          {message.status === "sent" && t("messageCenter.statusSent")}
                          {message.status === "read" && t("messageCenter.statusRead")}
                          {message.status === "not_read" && t("messageCenter.statusNotRead")}
                        </span>
                      </span>
                    </div>
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>

            {/* Message input */}
            <div className="chat-message-input-container">
              <textarea
                value={messageInput}
                onChange={(e) => setMessageInput(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder={t("messageCenter.typeMessage")}
                rows={1}
              />
              <button
                onClick={handleSendMessage}
                disabled={!messageInput.trim()}
                className="chat-send-button"
              >
                <FaPaperPlane />
              </button>
            </div>
          </>
        ) : (
          <div className="chat-empty-chat">
            <h3>{t("messageCenter.selectConversation")}</h3>
          </div>
        )}
      </div>
    </div>
  );
};

export default MessageCenter;
