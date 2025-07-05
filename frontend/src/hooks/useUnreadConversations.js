import { useState, useEffect, useCallback } from "react";
import useAuthStore from "../stores/useAuthStore";

function useUnreadConversations() {
  const { user } = useAuthStore();
  const [unreadConversations, setUnreadConversations] = useState(new Set());
  const [conversationCount, setConversationCount] = useState(0);
  const [websocket, setWebSocket] = useState(null);

  const WS_URL = "wss://localhost:8443/projectcitrus/websocket/notifications/";

  // ✅ ATUALIZAR CONTADOR
  const updateConversationCount = useCallback((newSet) => {
    setConversationCount(newSet.size);
  }, []);

  // ✅ ADICIONAR NOVA CONVERSA
  const addUnreadConversation = useCallback(
    (senderId) => {
      if (!senderId) return;

      setUnreadConversations((prev) => {
        const newSet = new Set(prev);
        if (!newSet.has(senderId)) {
          newSet.add(senderId);
          updateConversationCount(newSet);
          console.log(
            `📩 Nova conversa não lida: ${senderId}. Total: ${newSet.size}`
          );
        }
        return newSet;
      });
    },
    [updateConversationCount]
  );

  // ✅ RESETAR CONTADOR (quando abre dropdown)
  const resetConversationCount = useCallback(() => {
    setUnreadConversations(new Set());
    setConversationCount(0);
    console.log("🔄 Contador de conversas resetado");
  }, []);

  // ✅ CONECTAR WEBSOCKET
  useEffect(() => {
    if (!user) {
      if (websocket) {
        websocket.close();
        setWebSocket(null);
      }
      return;
    }

    if (websocket) return; // Já conectado

    const ws = new WebSocket(WS_URL);

    ws.onopen = () => {
      console.log("🔗 WebSocket conectado para conversas");
    };

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);

        // ✅ ESCUTAR APENAS MENSAGENS
        if (data.type === "MESSAGE") {
          const notification = data.notification;
          if (notification && notification.senderId) {
            // ✅ ADICIONAR CONVERSA SE NOVA
            addUnreadConversation(notification.senderId);
          }
        }

        // ✅ MANTER PING/PONG
        if (data.type === "PING") {
          ws.send(JSON.stringify({ type: "PONG" }));
        }
      } catch (error) {
        console.error("❌ Erro ao processar mensagem WebSocket:", error);
      }
    };

    ws.onclose = () => {
      console.log("🔌 WebSocket desconectado");
      setWebSocket(null);
    };

    ws.onerror = (error) => {
      console.error("❌ Erro WebSocket:", error);
    };

    setWebSocket(ws);

    return () => {
      console.log("🧹 Limpando WebSocket...");
      ws.close();
    };
  }, [user, websocket, addUnreadConversation]);

  return {
    conversationCount,
    unreadConversations,
    addUnreadConversation,
    resetConversationCount,
  };
}

export default useUnreadConversations;
