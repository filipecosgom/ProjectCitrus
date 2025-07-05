import { useState, useEffect, useCallback } from "react";
import useAuthStore from "../stores/useAuthStore";

function useUnreadConversations() {
  const { user } = useAuthStore();
  const [unreadConversations, setUnreadConversations] = useState(new Set());
  const [conversationCount, setConversationCount] = useState(0);
  const [websocket, setWebSocket] = useState(null);

  // ✅ REMOVER isActive - USAR user DIRETAMENTE
  const WS_URL = "wss://localhost:8443/projectcitrus/websocket/notifications/";

  // ✅ ATUALIZAR CONTADOR
  const updateConversationCount = useCallback((newSet) => {
    setConversationCount(newSet.size);
  }, []);

  // ✅ ADICIONAR NOVA CONVERSA
  const addUnreadConversation = useCallback(
    (senderId) => {
      if (!senderId || !user) return; // ✅ USAR user DIRETAMENTE

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
    [updateConversationCount, user] // ✅ USAR user DIRETAMENTE
  );

  // ✅ RESETAR CONTADOR
  const resetConversationCount = useCallback(() => {
    setUnreadConversations(new Set());
    setConversationCount(0);
    console.log("🔄 Contador de conversas resetado");
  }, []);

  // ✅ WEBSOCKET CONTROLADO
  useEffect(() => {
    // ✅ LIMPAR WEBSOCKET SE NÃO TIVER USER
    if (!user) {
      // ✅ USAR user DIRETAMENTE
      console.log("❌ Sem user ativo, fechando WebSocket");
      if (websocket) {
        websocket.close(1000, "No user");
        setWebSocket(null);
      }
      // ✅ RESETAR CONTADOR
      setUnreadConversations(new Set());
      setConversationCount(0);
      return;
    }

    // ✅ NÃO CONECTAR SE JÁ TIVER WEBSOCKET ATIVO
    if (websocket && websocket.readyState === WebSocket.OPEN) {
      console.log("✅ WebSocket já ativo, não duplicar");
      return;
    }

    // ✅ FECHAR WEBSOCKET ANTERIOR SE EXISTIR
    if (websocket) {
      websocket.close(1000, "Reconnecting");
      setWebSocket(null);
    }

    console.log("🔗 Conectando WebSocket para conversas...");
    const ws = new WebSocket(WS_URL);

    ws.onopen = () => {
      console.log("✅ WebSocket conectado para conversas");
      setWebSocket(ws);
    };

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);

        // ✅ ESCUTAR APENAS MENSAGENS
        if (data.type === "MESSAGE") {
          const notification = data.notification;
          if (notification && notification.senderId) {
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

    ws.onclose = (event) => {
      console.log(`🔌 WebSocket fechado: ${event.code} - ${event.reason}`);
      setWebSocket(null);
    };

    ws.onerror = (error) => {
      console.error("❌ Erro WebSocket:", error);
      setWebSocket(null);
    };

    // ✅ CLEANUP FUNCTION
    return () => {
      console.log("🧹 Limpando WebSocket...");
      if (ws.readyState === WebSocket.OPEN) {
        ws.close(1000, "Component cleanup");
      }
    };
  }, [user]); // ✅ APENAS user COMO DEPENDÊNCIA

  // ✅ SEMPRE RETORNAR OS VALORES
  return {
    conversationCount: user ? conversationCount : 0, // ✅ USAR user DIRETAMENTE
    unreadConversations: user ? unreadConversations : new Set(),
    addUnreadConversation,
    resetConversationCount,
  };
}

export default useUnreadConversations;
