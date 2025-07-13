import { showSuccessToast, showErrorToast } from "../utils/toastConfig/toastConfig";
import { getAllNotifications, updateNotification } from "../api/notificationsApi";

// Handle fetching all notifications
export const handleGetNotifications = async (onSuccess, onError) => {
  try {
    const result = await getAllNotifications();
    if (result.success) {
      if (onSuccess) onSuccess(result.data);
    } else {
      throw new Error(result.error || "Erro ao buscar notificações.");
    }
  } catch (error) {
    if (onError) onError(error);
    showErrorToast("Erro ao buscar notificações.");
  }
};

// Handle updating a notification (isRead, isSeen, etc)
export const handleUpdateNotification = async (notificationUpdate, onSuccess, onError) => {
  try {
    const result = await updateNotification(notificationUpdate);
    if (result.success) {
      if (onSuccess) onSuccess(result.data);
    } else {
      throw new Error(result.error || "Erro ao atualizar notificação.");
    }
  } catch (error) {
    if (onError) onError(error);
    showErrorToast("Erro ao atualizar notificação.");
  }
};
