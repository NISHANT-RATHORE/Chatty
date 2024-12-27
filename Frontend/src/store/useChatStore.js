import { create } from "zustand";
import toast from "react-hot-toast";
import axiosInstance from "../lib/axios";
import { useAuthStore } from "./UseAuthStore";

export const useChatStore = create((set, get) => ({
  messages: [],
  users: [],
  selectedUser: null,
  isUsersLoading: false,
  isMessagesLoading: false,

  getUsers: async () => {
    set({ isUsersLoading: true });
    try {
      const res = await axiosInstance.get("/messages/users");
      set({ users: res.data });
    } catch (error) {
      toast.error(error.response.data.message);
    } finally {
      set({ isUsersLoading: false });
    }
  },

  getMessages: async (userId) => {
    set({ isMessagesLoading: true });
    try {
      const res = await axiosInstance.get(`/messages/${userId}`);
      set({ messages: res.data });
    } catch (error) {
      toast.error(error.response.data.message);
    } finally {
      set({ isMessagesLoading: false });
    }
  },

  sendMessage: async (payload) => {
    const { text, image } = payload; // Destructure text and image from the payload
    console.log("Type of text:", typeof text); // Debug the type of `text`
    
    const { selectedUser, messages } = get();

    if (!selectedUser || !selectedUser.userId) {
        console.error("Selected user is not available.");
        toast.error("Please select a user to send the message.");
        return;
    }

    // Prepare FormData for the request
    const formData = new FormData();
    formData.append("text", text ? text.toString() : ""); // Ensure text is always a string
    if (image) {
        formData.append("image", image); // Append the image only if provided
    }

    console.log([...formData.entries()]); // Debug: Check FormData content

    try {
        // Make the POST request
        const res = await axiosInstance.post(`/messages/send/${selectedUser.userId}`, formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });

        // Update messages state with the new message
        set({ messages: [...messages, res.data] });
    } catch (error) {
        console.error("Error in sendMessage:", error);

        // Provide user feedback
        const errorMessage = error.response?.data?.message || "Failed to send the message.";
        toast.error(errorMessage);
    }
},


  subscribeToMessages: () => {
    const { selectedUser } = get();
    if (!selectedUser) return;

    const socket = useAuthStore.getState().socket;

    socket.on("newMessage", (newMessage) => {
      const isMessageSentFromSelectedUser = newMessage.senderId === selectedUser._id;
      if (!isMessageSentFromSelectedUser) return;

      set({
        messages: [...get().messages, newMessage],
      });
    });
  },

  unsubscribeFromMessages: () => {
    const socket = useAuthStore.getState().socket;
    socket.off("newMessage");
  },

  setSelectedUser: (selectedUser) => set({ selectedUser }),
}));