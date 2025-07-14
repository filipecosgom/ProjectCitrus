import useAuthStore from "../stores/useAuthStore";
import { useEffect, useRef } from "react";
import useNotificationStore from "../stores/useNotificationStore";

function useWebSocketNotifications() {
  const { user } = useAuthStore();
  const WS_URL = "wss://localhost:8443/projectcitrus/websocket/notifications/";
  const websocketRef = useRef(null); // <-- Only use ref
  const addMessageNotification =
    useNotificationStore.getState().addMessageNotification;
  const addOtherNotification =
    useNotificationStore.getState().addOtherNotification;
  const setMessageNotifications =
    useNotificationStore.getState().setMessageNotifications;
  const setOtherNotifications =
    useNotificationStore.getState().setOtherNotifications;

  useEffect(() => {
    if (websocketRef.current) {
      return;
    }

    const ws = new WebSocket(WS_URL);
    websocketRef.current = ws;

    ws.onopen = () => {
      console.log("WebSocket connected");
      // Fetch and set notifications on WebSocket open
      useNotificationStore.getState().fetchAndSetNotifications();
    };

    ws.onmessage = function (event) {
      const data = JSON.parse(event.data);

      switch (data.type) {
        case "MESSAGE":
          console.log("Received message notification:", data);
          if (data.notification) {
            addMessageNotification(data.notification);
          }
          break;
        case "APPRAISAL":
          console.log("Received appraisal notification:", data);
          addOtherNotification(data.notification);
          break;
        case "COURSE":
          console.log("Received course notification:", data);
          if (data.notification) {
            addOtherNotification(data.notification);
          }
          break;
        case "CYCLE_OPEN":
          console.log("Received cycle notification:", data);
          addOtherNotification(data.notification);
          break;
        case "CYCLE_CLOSE":
          console.log("Received cycle notification:", data);
          addOtherNotification(data.notification);
          break;
        case "USER_UPDATE":
          console.log("Received user update notification:", data);
          addOtherNotification(data.notification);
          break;
        case "AUTH_FAILED":
          console.error("WebSocket authentication failed");
          ws.close();
          break;
        case "PING":
          ws.send(JSON.stringify({ type: "PONG" }));
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
  }, []);

  const closeWebSocket = () => {
    if (
      websocketRef.current &&
      websocketRef.current.readyState === WebSocket.OPEN
    ) {
      console.log("Closing WebSocket connection...");
      websocketRef.current.close();
    }
  };

  return { closeWebSocket };
}

export default useWebSocketNotifications;
