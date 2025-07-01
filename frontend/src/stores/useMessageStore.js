import { handleFetchAllConversations } from '../handles/handleFetchAllConversations';
import { handleFetchMessages } from '../handles/handleFetchMessages';
import { create } from "zustand";
import { transformArrayDatetoDate } from "../utils/utilityFunctions";

const useMessageStore = create((set, get) => ({
    messages: [],
    selectedUserId: null,
    conversations: [],
    localUsers: [],
    
    fetchAllConversations: async () => {
      try {
        const conversationUsers = await handleFetchAllConversations();
            const mergedUsers = [...(conversationUsers || [])];
            get().localUsers.forEach((localUser) => {
          // 🛠️ Add local users *only if they are missing* from fetched conversations
          if (!mergedUsers.some(user => user.id === localUser.id)) {
            mergedUsers.push(localUser);
          }
        });    
        set({ conversations: mergedUsers });
      } catch (error) {
        set({ error: error.message });
      }
    },
    


  addNewUserToConversation: (userId) => {
    const { conversations, localUsers } = get();
    if (!conversations.some(user => user.id === userId)) {
        const newUser = { userId, url: null };
        set({ conversations: [...conversations, newUser], localUsers: [...localUsers, newUser] });
    }
},

    
    
    fetchUserConversation: async (otherUserId) => {
      try {
        const result = await handleFetchMessages(otherUserId);
        if (result.success) {
          const formattedMessages = result.messages.map((message) => ({
            ...message,
            status: message.read ? "read" : "not_read",
            formattedTimestamp: transformArrayDatetoDate(message.timestamp),
          }));
          set({ messages: formattedMessages });
        } else {
          set({ error: result.error?.message || "Failed to fetch messages", loading: false });
        }
      } catch (error) {
        set({ error: error.message, loading: false });
      }
    },

    setSelectedUser: (user) => {
      console.log("selectedUser just set to:", user);
      set({ selectedUser: user, messages: []});
      // Optionally clear messages when changing user
      // set({ selectedUser: username, messages: [] });
    },

    addLocalMessage: (message) => {
      set((state) => ({
        messages: [...state.messages, message]
      }));
    },

    markConversationAsRead: () => {
      set((state) => ({
        messages: state.messages.map((message) => ({
          ...message,
          status: "read"
        }))
      }));
    },    

    updateMessageStatus: (messageId, status) => {
      set((state) => ({
        messages: state.messages.map((message) => (
          message.messageId === messageId ? { ...message, status } : message
        ))
      }));
    },

    isMessageAlreadyInQueue: (messageId) => {
      const { messages } = get();
      return messages.some(message => message.messageId === messageId);
    },

    resetMessages: () => set({ messages: [] }),
  }));
  
  export default useMessageStore;
