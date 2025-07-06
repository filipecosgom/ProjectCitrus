// ✅ FUNÇÃO CORRIGIDA PARA USAR JWT
export const markMessageNotificationsAsRead = async () => {
  try {
    const response = await fetch(
      "/projectcitrus/rest/notifications/mark-messages-read",
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // ✅ ENVIA COOKIE JWT AUTOMATICAMENTE
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();
    return {
      success: true,
      data: result,
    };
  } catch (error) {
    console.error("❌ Erro ao marcar notificações como lidas:", error);
    return {
      success: false,
      error: error.message,
    };
  }
};
