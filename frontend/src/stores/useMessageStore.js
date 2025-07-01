import { fetchMessages, fetchAllConversations } from '../api/messagesApi';
import { create } from "zustand";
import { transformArrayDatetoDate } from "../Utils/utilityFunctions";
import { useEffect } from "react";
import User from "../Pages/User/User";

const useMessageStore = create((set, get) => ({
    messages: [],
    selectedUser: null,
    conversations: [],
    localUsers: [],
    
    fetchAllConversations: async (token) => {
      try {
        const conversationUsers = await fetchAllConversations(token);
    
        // 🔥 Ensure local users persist by merging them properly
        const mergedUsers = [...conversationUsers];
    
        get().localUsers.forEach((localUser) => {
          // 🛠️ Add local users *only if they are missing* from fetched conversations
          if (!mergedUsers.some(user => user.username === localUser.username)) {
            mergedUsers.push(localUser);
          }
        });
    
        set({ conversations: mergedUsers });
      } catch (error) {
        set({ error: error.message });
      }
    },
    


  addNewUserToConversation: (username) => {
    const { conversations, localUsers } = get();
    if (!conversations.some(user => user.username === username)) {
        const newUser = { username, url: null };
        set({ conversations: [...conversations, newUser], localUsers: [...localUsers, newUser] });
    }
},

    
    
    fetchUserConversation: async (token, username) => {
      try {
        const userMessages = await fetchMessages(token, username);
        const formattedMessages = userMessages.map((message) => ({
          ...message, // Keep original message properties
          status: message.read ? "read" : "not_read",
          formattedTimestamp: transformArrayDatetoDate(message.timestamp)
        }));
        set({ messages: formattedMessages }); // No need for extraction
      } catch (error) {
        set({ error: error.message, loading: false });
      }
    },

    setSelectedUser: (user) => {
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
  