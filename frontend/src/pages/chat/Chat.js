import React, { useState, useEffect } from "react";
import useMessageStore from "../../stores/useMessageStore";
import useAuthStore from "../../stores/useAuthStore";
import { FaPaperPlane } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import useWebSocketChat from "../../websockets/useWebSocketChat";
import "./Chat.css";
import handleNotification from "../../handles/handleNotification";
import { useLocation, useNavigate } from "react-router-dom";
import { handleReadConversation } from '../../handles/handleReadConversation';
import { handleSendMessageViaApi } from '../../handles/handleSendMessageViaApi';
import UserIcon from "../../components/userIcon/UserIcon";

export const Chat = () => {
  const navigate = useNavigate();
  const webSocketChat = useWebSocketChat();
  const { sendMessage } = useWebSocketChat();
  const userToChat = new URLSearchParams(useLocation().search).get("id");
  const { user } = useAuthStore();
  const { conversations,
     selectedUser,
      setSelectedUser,
       fetchAllConversations,
        messages,
         fetchUserConversation,
          addLocalMessage,
           updateMessageStatus  } = useMessageStore();
  const [messageInput, setMessageInput] = useState("");
  const { t } = useTranslation();

  useEffect(() => {
    const getUserInformation = async () => {
      await fetchAllConversations();

      if (userToChat) {
        const updatedConversations = useMessageStore.getState().conversations;
        const userFromURL = updatedConversations.find(u => u.id === Number(userToChat));

        if (!userFromURL) {
          useMessageStore.getState().addNewUserToConversation(userToChat);

          setTimeout(() => { // Small delay to allow state to settle
            const finalConversations = useMessageStore.getState().conversations;
            const addedUser = finalConversations.find(u => u.id === userToChat);
            
            if (addedUser && (!selectedUser || selectedUser.id !== userToChat)) {
              setSelectedUser(addedUser);
            }
          }, 50);
        } else {
          if (!selectedUser || selectedUser.username !== userToChat) {
            setSelectedUser(userFromURL);
          }
        }
      }
    };
    getUserInformation();
}, [ userToChat]); 

useEffect(() => {
    if (selectedUser) {
      navigate(`/chat?id=${selectedUser.id}`, { replace: true });
      handleReadConversation(selectedUser.id);
      fetchUserConversation(selectedUser.id);
    }
  }, [selectedUser]);
    


  const handleSendMessage = async () => {
      if (messageInput.trim() !== "") {
        // Create a temporary message with a local ID
        const localId = `localId-${Date.now()}`;
        const newMessage = {
          messageId: localId,
          message: messageInput,
          senderId: user.id,
          formattedTimestamp: new Date(),
          status: 'sending'
        };
        addLocalMessage(newMessage);
        setMessageInput("");

          if(sendMessage(selectedUser.id, messageInput)) {
            updateMessageStatus(localId, 'not_read');
          }
          else {
            try {
              await handleSendMessageViaApi(selectedUser.id, messageInput);
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
      <div className="chat-user-list">
        <h2 className="chat-user-list-header">Conversations</h2>
        <div className="chat-user-list-items">
          {conversations.map((user) => (
            <div 
              key={user.id} 
              className={`chat-user-item ${selectedUser?.id === user.id ? 'selected' : ''}`}
              onClick={() => setSelectedUser(user)}
            >
              <div className="user-avatar">
                <UserIcon user={user} />
              </div>
              <div className="chat-user-info">
                <h3>{user.name} {user.surname}</h3>
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
                <UserIcon user={selectedUser}/>
                <h2>{selectedUser.username}</h2>
              </div>
            </div>

            {/* Messages */}
            <div className="chat-messages-container">
              {messages.map((message, index) => (
                <div
                  key={index}
                  className={`chat-message ${message.senderId === user.id ? 'sent' : 'received'}`}
                >
                  <div className="chat-message-content">
                    <p>{message.message}</p>
                    <div className="chat-message-meta">
                      <span className="chat-message-time">
                        {new Date(message.formattedTimestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      <span className={`chat-status-icon-${message.status}`}>
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
            <div className="chat-message-input-container">
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
          <div className="chat-empty-chat">
            <h3>Select a conversation to start chatting</h3>
          </div>
        )}
      </div>
    </div>
  );
};

export default Chat;