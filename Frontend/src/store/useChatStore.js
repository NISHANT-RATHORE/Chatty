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
    isConnected: false,

    getUsers: async () => {
        const token = localStorage.getItem("token");
        set({ isUsersLoading: true });
        try {
            const res = await axiosInstance.get("/messages/users", {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            set({ users: res.data });
        } catch (error) {
            toast.error("error occurred");
        } finally {
            set({ isUsersLoading: false });
        }
    },

    getMessages: async (userId) => {
        const token = localStorage.getItem("token");
        set({ isMessagesLoading: true });
        try {
            const res = await axiosInstance.get(`/messages/${userId}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            set({ messages: res.data });
        } catch (error) {
            toast.error(error.response.data.message);
        } finally {
            set({ isMessagesLoading: false });
        }
    },

    sendMessage: async (payload) => {
        const token = localStorage.getItem("token");
        const { text, image } = payload;
        const { selectedUser } = get();

        if (!selectedUser || !selectedUser.userId) {
            toast.error("Please select a user to send the message.");
            return;
        }

        const formData = new FormData();
        formData.append("text", text ? text.toString() : "");
        if (image) {
            formData.append("image", image);
        }

        try {
            const res = await axiosInstance.post(`/messages/send/${selectedUser.userId}`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    'Authorization': `Bearer ${token}`
                },
            });

            // set({ messages: [...messages, res.data] });

            const socket = useAuthStore.getState().socket;
            if (socket) {
                socket.send(JSON.stringify(res.data));
            }
        } catch (error) {
            const errorMessage = error.response?.data?.message || "Failed to send the message.";
            toast.error(errorMessage);
        }
    },

    subscribeToMessages: () => {
        const { selectedUser } = get();
        if (!selectedUser) return;

        const socket = useAuthStore.getState().socket;
        if (socket) {
            socket.onmessage = (event) => {
                try {
                    const newMessage = JSON.parse(event.data);
                    if (newMessage.senderId === selectedUser.userId || newMessage.receiverId === selectedUser.userId) {
                        set({ messages: [...get().messages, newMessage] });
                    }
                } catch (error) {
                    console.error("Failed to parse message:", error);
                }
            };
        }
    },

    unsubscribeFromMessages: () => {
        const socket = useAuthStore.getState().socket;
        if (socket) {
            socket.onmessage = null;
        }
    },

    setSelectedUser: (selectedUser) => set({ selectedUser }),
}));