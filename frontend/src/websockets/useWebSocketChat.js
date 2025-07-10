import { useEffect, useRef } from "react";
import useMessageStore from '../stores/useMessageStore';
import { transformArrayDatetoDate } from "../utils/utilityFunctions";
import useAuthStore from "../stores/useAuthStore";

function useWebSocketChat() {
  const WS_URL = "wss://localhost:8443/projectcitrus/websocket/chat/";
  const websocketRef = useRef(null); // <-- Only use ref
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
    if (websocketRef.current && websocketRef.current.readyState === WebSocket.OPEN) {
      const messageJSON = JSON.stringify({
        type: "MESSAGE",
        recipientId: userId,
        message: message,
      });
      websocketRef.current.send(messageJSON);
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
    if (websocketRef.current) {
      return;
    }

    const ws = new WebSocket(WS_URL);
    websocketRef.current = ws;

    ws.onopen = () => {
      console.log("WebSocket connected");
    }

    ws.onmessage = function (event) {
      const data = JSON.parse(event.data);

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
          break;
        case "AUTH_FAILED":
          console.error("WebSocket authentication failed");
          ws.close();
          break;
        case "CONVERSATION_READ":
          if (data.readerId === currentChattingUser.user.id) {
            markConversationAsRead();
          }
          break;
        case "PING":
          ws.send(JSON.stringify({ type: "PONG" }));
          break;
        case "SUCCESS":
          markConversationAsRead();
          break;
        default:
          console.warn("Unknown message type:", data.type);
          break;
      }
    };

    ws.onclose = () => {
      console.log("WebSocket closed.");
    };

    return () => {
      console.log("Cleaning up WebSocket...");
      ws.close();
      websocketRef.current = null;
    };
  }, [addLocalMessage, isMessageAlreadyInQueue, markConversationAsRead]);

  const closeWebSocket = () => {
    if (websocketRef.current && websocketRef.current.readyState === WebSocket.OPEN) {
      console.log("Closing WebSocket connection...");
      websocketRef.current.close();
    }
  };

  return { sendMessage, closeWebSocket };
}

export default useWebSocketChat;