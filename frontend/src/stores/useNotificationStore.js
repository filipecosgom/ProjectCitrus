import { getAllNotifications } from "../api/notificationsApi";
import { create } from "zustand";

const useNotificationStore = create((set, get) => ({
  messageNotifications: [],
  otherNotifications: [],

  // Fetch notifications from API and set them in the store
  fetchAndSetNotifications: async () => {
    try {
      const result = await getAllNotifications();
      console.log("Fetched notifications:", result);
      const notifications = result.data?.data || [];
      if (result.success && Array.isArray(notifications)) {
        const messages = notifications.filter((n) => n.type === "MESSAGE");
        const others = notifications.filter((n) => n.type !== "MESSAGE");
        get().setMessageNotifications(messages);
        get().setOtherNotifications(others);
      } else {
        get().setMessageNotifications([]);
        get().setOtherNotifications([]);
      }
    } catch (error) {
      get().setMessageNotifications([]);
      get().setOtherNotifications([]);
    }
  },

  setMessageNotifications: (notifications) => {
    set({ messageNotifications: notifications });
  },

  setOtherNotifications: (notifications) => {
    set({ otherNotifications: notifications });
  },

  addMessageNotification: (notification) => {
    set((state) => {
      const idx = state.messageNotifications.findIndex(n => n.id === notification.id);
      if (idx === -1) {
        return { messageNotifications: [notification, ...state.messageNotifications] };
      } else {
        const updated = [...state.messageNotifications];
        updated[idx] = { ...updated[idx], ...notification };
        return { messageNotifications: updated };
      }
    });
  },

  addOtherNotification: (notification) => {
    set((state) => {
      const idx = state.otherNotifications.findIndex(n => n.id === notification.id);
      if (idx === -1) {
        return { otherNotifications: [notification, ...state.otherNotifications] };
      } else {
        const updated = [...state.otherNotifications];
        updated[idx] = { ...updated[idx], ...notification };
        return { otherNotifications: updated };
      }
    });
  },


  markMessageAsRead: (notificationId) => {
    set((state) => ({
      messageNotifications: state.messageNotifications.map((n) =>
        n.id === notificationId ? { ...n, notificationIsRead: true } : n
      ),
    }));
  },

  setMessageUnreadCountToZero: (notificationId) => {
    set((state) => ({
      messageNotifications: state.messageNotifications.map((n) =>
        n.id === notificationId ? { ...n, messageCount: 0 } : n
      ),
    }));
  },

  markOtherAsRead: (notificationId) => {
    set((state) => ({
      otherNotifications: state.otherNotifications.map((n) =>
        n.id === notificationId ? { ...n, notificationIsRead: true } : n
      ),
    }));
  },

  markAllMessagesAsRead: () => {
    set((state) => {
      // Only update if there are unread messages
      const hasUnread = state.messageNotifications.some((n) => !n.notificationIsRead);
      if (!hasUnread) return {};
      return {
        messageNotifications: state.messageNotifications.map((n) => ({
          ...n,
          notificationIsRead: true,
        })),
      };
    });
  },

  markAllOthersAsRead: () => {
    set((state) => ({
      otherNotifications: state.otherNotifications.map((n) => ({
        ...n,
        notificationIsRead: true,
      })),
    }));
  },

    markMessageAsSeen: (notificationId) => {
    set((state) => ({
      messageNotifications: state.messageNotifications.map((n) =>
        n.id === notificationId ? { ...n, notificationIsSeen: true } : n
      ),
    }));
  },

    markOtherAsSeen: (notificationId) => {
    set((state) => ({
      otherNotifications: state.otherNotifications.map((n) =>
        n.id === notificationId ? { ...n, notificationIsSeen: true } : n
      ),
    }));
  },

  clearMessageNotifications: () => {
    set({ messageNotifications: [] });
  },

  clearOtherNotifications: () => {
    set({ otherNotifications: [] });
  },
}));

export default useNotificationStore;
