import { useState, useEffect } from "react";
import useAuthStore from "../stores/useAuthStore";
import handleNotification from "../handles/handleNotification";
import { useTranslation } from "react-i18next";
import {
  transformArrayDatetoDate,
  dateToFormattedTime,
} from "../utils/utilityFunctions";

function useWebSocketNotifications(isAuthenticated) {
  const { user } = useAuthStore(); // get user info, not token
  const WS_URL = "wss://localhost:8443/projectcitrus/websocket/notifications/";
  const [notificationCount, setNotificationCount] = useState(0);
  const [websocket, setWebSocket] = useState(null);
  const { t } = useTranslation();

  useEffect(() => {
    console.log("🔇 WebSocket notifications DESABILITADO");
    return; // ← SAIR IMEDIATAMENTE
  }, []);

  useEffect(() => {
    if (!isAuthenticated || !user) {
      if (websocket) {
        websocket.close();
        setWebSocket(null);
      }
      return;
    }
    if (websocket) return;

    const ws = new WebSocket(WS_URL);

    ws.onopen = () => {
      console.log("WebSocket notifications connected");
    };

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);

      if (data.type === "NOTIFICATION_COUNT") {
        setNotificationCount(data.count);
      }
      if (data.type === "MESSAGE") {
        let notification = data.notification;
        let timestamp = transformArrayDatetoDate(notification.timestamp);
        timestamp = dateToFormattedTime(timestamp);
        handleNotification("success", "wsNotificationsLastMessage", {
          sender: notification.senderUsername,
          message: notification.content,
          timestamp: timestamp,
        });
        handleNotification("success", "wsNotificationsUnreadMessages", {
          numMessages: notification.messageCount,
          sender: notification.senderUsername,
        });
      }
      if (data.type === "AUTH_FAILED") {
        handleNotification("error", "WebSocket authentication failed");
        ws.close();
      }
      if (data.type === "PING") {
        ws.send(JSON.stringify({ type: "PONG" }));
      }
    };

    ws.onclose = () => {
      console.log("WebSocket notifications closed.");
      setWebSocket(null);
    };

    setWebSocket(ws);

    return () => {
      console.log("Cleaning up WebSocket notifications...");
      ws.close();
    };
  }, [isAuthenticated, user]);

  return { notificationCount, websocket };
}

export default useWebSocketNotifications;
