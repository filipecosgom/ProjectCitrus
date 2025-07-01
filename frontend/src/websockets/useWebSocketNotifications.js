import { useState, useEffect, useRef } from "react";
import useUserStore from "../Stores/useUserStore";
import handleNotification from "../handles/handleNotification";
import { useTranslation } from "react-i18next";
import {
  transformArrayDatetoDate,
  dateToFormattedTime,
} from "../Utils/utilityFunctions";

function useWebSocketNotifications(isAuthenticated) {
  const WS_URL = "wss://localhost:8443/projectcitrus/websocket/notifications/";
  const user = useUserStore((state) => state.user);
  const [notificationCount, setNotificationCount] = useState(0);
  const [websocket, setWebSocket] = useState(null);
  const { t } = useTranslation();

  useEffect(() => {
    const ws = new WebSocket(WS_URL);

    ws.current.onopen = () => {
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
        console.log(notification);
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
      if (data.type === "PING") {
        ws.send(JSON.stringify({ type: "PONG" }));
      }
    };

    ws.onclose = () => {
      console.log("WebSocket closed.");
      setWebSocket(null);
    };

    setWebSocket(ws); // Persist the WebSocket instance

    return () => {
      console.log("Cleaning up WebSocket...");
      ws.close();
    };
  }, [isAuthenticated, token]); // Dependencies

  return { notificationCount, websocket };
}

export default useWebSocketNotifications;
