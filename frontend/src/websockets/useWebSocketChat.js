import { useState, useEffect, useRef } from "react";
import useMessageStore from '../stores/useMessageStore';
import { transformArrayDatetoDate } from "../utils/utilityFunctions";
import useAuthStore from "../stores/useAuthStore";

function useWebSocketChat() {
  const WS_URL = "wss://localhost:8443/projectcitrus/websocket/chat/";
  const [websocket, setWebSocket] = useState(null);
  const { user } = useAuthStore();
  const {
    selectedUser,
    addLocalMessage,
    isMessageAlreadyInQueue,
    markConversationAsRead,
  } = useMessageStore();
  const currentChattingUser = useRef(selectedUser);

  useEffect(() => {
    currentChattingUser.user = selectedUser;
  }, [selectedUser]);

  const sendMessage = (userId, message) => {
    if (websocket && websocket.readyState === WebSocket.OPEN) {
      const messageJSON = JSON.stringify({
        type: "MESSAGE",
        recipientId: userId,
        message: message,
      });
      websocket.send(messageJSON);
      console.log("Message sent:", messageJSON);
      return true;
    } else {
      console.error("WebSocket is not connected");
      return false;
    }
  };

  useEffect(() => {
    // Reconnect when user info changes (e.g., login/logout)
    // If you want to reconnect on login/logout, add user as a dependency
  }, [user]);

  useEffect(() => {
    if (websocket) {
      return;
    }

    const ws = new WebSocket(WS_URL);

    ws.onopen = () => {
      console.log("WebSocket connected");
    }

    ws.onmessage = function (event) {
      const data = JSON.parse(event.data);
      console.log("WebSocket received:", data);
      console.log("Current chatting user:", currentChattingUser.user);

      switch (data.type) {
        case "MESSAGE":
          let message = data.message;
          if (message.senderId === currentChattingUser.user.id) {
            if (!isMessageAlreadyInQueue(message.id)) {
              const newMessage = {
                messageId: message.id,
                message: message.messageContent,
                sender: message.senderId,
                formattedTimestamp: transformArrayDatetoDate(message.sentDate),
                status: "read",
              };
              addLocalMessage(newMessage);
            }
          }
          break;
        case "AUTHENTICATED":
          console.log("Authenticated to websocket chat");
          break;
        case "AUTH_FAILED":
          console.error("WebSocket authentication failed");
          ws.close();
          break;
        case "CONVERSATION_READ":
          if (data.senderId === currentChattingUser.user.userId) {
            markConversationAsRead();
          }
          break;
        case "PING":
          ws.send(JSON.stringify({ type: "PONG" }));
          break;
        case "SUCCESS":
          markConversationAsRead();
        default:
          console.warn("Unknown message type:", data.type);
      }
    };

    ws.onclose = () => {
      console.log("WebSocket closed.");
    };

    setWebSocket(ws);

    return () => {
      console.log("Cleaning up WebSocket...");
      ws.close();
    };
  }, []);

  return { sendMessage };
}

export default useWebSocketChat;