import React, { useState, useEffect } from "react";
import { getLoggedUserInformation } from "../../Handles/handleLogin";
import useMessageStore from "../../Stores/useMessageStore";
import useUserStore from "../../Stores/useUserStore";
import { FaPaperPlane } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import useWebSocketChat from "../../Websockets/useWebSocketChat";
import { sendMessageApi, readConversationApi } from '../api/messagesApi';
import "./chat.css";
import handleNotification from "../../handles/handleNotification";
import { useLocation, useNavigate } from "react-router-dom";

export const Chat = () => {
  const navigate = useNavigate();
  const webSocketChat = useWebSocketChat();
  const { sendMessage } = useWebSocketChat();
  const token = useUserStore((state) => state.token);
  const userToChat = new URLSearchParams(useLocation().search).get("id");
  const [userUsername, setUsername ] = useState(null)
  const { conversations, selectedUser, setSelectedUser, fetchAllConversations, messages, fetchUserConversation, addLocalMessage, updateMessageStatus  } = useMessageStore();
  const [messageInput, setMessageInput] = useState("");
  const { t } = useTranslation();

  useEffect(() => {
    const getUserInformation = async () => {
      await fetchAllConversations(token);
      let userInformation = await getLoggedUserInformation();
      setUsername(userInformation.username);

      if (userToChat) {
        const updatedConversations = useMessageStore.getState().conversations;
        const userFromURL = updatedConversations.find(u => u.username === userToChat);

        if (!userFromURL) {
          console.log(`Adding new user to conversation: ${userToChat}`);
          useMessageStore.getState().addNewUserToConversation(userToChat);

          setTimeout(() => { // Small delay to allow state to settle
            const finalConversations = useMessageStore.getState().conversations;
            const addedUser = finalConversations.find(u => u.username === userToChat);
            
            if (addedUser && (!selectedUser || selectedUser.username !== userToChat)) {
              setSelectedUser(addedUser);
            }
          }, 50);
        } else {
          console.log(`Selecting existing user: ${userToChat}`);
          if (!selectedUser || selectedUser.username !== userToChat) {
            setSelectedUser(userFromURL);
          }
        }
      }
    };

    getUserInformation();
}, [token, userToChat]); 
  

  useEffect(() => {
    if (selectedUser) {
      navigate(`/chat?username=${selectedUser.username}`, { replace: true });
      readConversationApi(token, selectedUser.username);
      fetchUserConversation(token, selectedUser.username);
    }
  }, [selectedUser]);
  


  const handleSendMessage = async () => {
      if (messageInput.trim() !== "") {
        // Create a temporary message with a local ID
        const localId = `localId-${Date.now()}`;
        const newMessage = {
          messageId: localId,
          message: messageInput,
          sender: userUsername,
          formattedTimestamp: new Date(),
          status: 'sending'
        };
        addLocalMessage(newMessage);
        setMessageInput(""); // Clear input after sending

          if(sendMessage(selectedUser.username, messageInput)) {
            updateMessageStatus(localId, 'not_read');
          }
          else {
            try {
              const response = await sendMessageApi(token, messageInput, selectedUser.username);
              handleNotification("success", "chatWsClosedMessageSent");
              updateMessageStatus(localId, 'sent');
              setMessageInput(""); // Clear input after sending
            }
            catch(error) {
              handleNotification("error", "chatWsClosedMessageSent");
              updateMessageStatus(localId, 'failed');
            }
          }
          
      }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div className="chat-container">
      {/* Left sidebar - User list */}
      <div className="user-list">
        <h2 className="user-list-header">Conversations</h2>
        <div className="user-list-items">
          {conversations.map((user) => (
            <div 
              key={user.username} 
              className={`user-item ${selectedUser?.username === user.username ? 'selected' : ''}`}
              onClick={() => setSelectedUser(user)}
            >
              <div className="user-avatar">
                {user.url ? (
                  <img src={user.url} alt={user.username} />
                ) : (
                  <div className="avatar-placeholder">{user.username.charAt(0).toUpperCase()}</div>
                )}
              </div>
              <div className="user-info">
                <h3>{user.username}</h3>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Main chat area */}
      <div className="chat-area">
        {selectedUser ? (
          <>
            {/* Chat header */}
            <div className="chat-header">
              <div className="chat-user">
                {selectedUser.url ? (
                  <img src={selectedUser.url} alt={selectedUser.username} className="chat-user-avatar" />
                ) : (
                  <div className="chat-avatar-placeholder">
                    {selectedUser.username.charAt(0).toUpperCase()}
                  </div>
                )}
                <h2>{selectedUser.username}</h2>
              </div>
            </div>

            {/* Messages */}
            <div className="messages-container">
              {messages.map((message, index) => (
                <div
                  key={index}
                  className={`message ${message.sender === selectedUser.username ? 'received' : 'sent'}`}
                >
                  <div className="message-content">
                    <p>{message.message}</p>
                    <div className="message-meta">
                      <span className="message-time">
                        {new Date(message.formattedTimestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      <span className={`status-icon-${message.status}`}>
                        {message.status === 'sending' && '⏳'}
                        {message.status === 'failed' && '❌'}
                        {message.status === 'sent' && '✓'}
                        {message.status === 'read' && '✓✓'}
                        {message.status === 'not_read' && '✓✓'}
                      </span>
                      </span>
                  </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Message input */}
            <div className="message-input-container">
              <textarea
                value={messageInput}
                onChange={(e) => setMessageInput(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Type a message..."
                rows={1}
              />
              <button
                onClick={handleSendMessage}
                disabled={!messageInput.trim()}
                className="send-button"
              >
                <FaPaperPlane />
              </button>
            </div>
          </>
        ) : (
          <div className="empty-chat">
            <h3>Select a conversation to start chatting</h3>
          </div>
        )}
      </div>
    </div>
  );
};

export default Chat;