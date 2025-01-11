// UseAuthStore.js
import axiosInstance from "../lib/axios";
import {create} from "zustand";
import toast from "react-hot-toast";
import {useChatStore} from "./useChatStore.js";

const webSocketUrl = import.meta.env.VITE_WEBSOCKET_URL

export const useAuthStore = create((set, get) => ({
    authUser: null,
    isSigningUp: false,
    isLoggingIn: false,
    isUpdatingProfile: false,
    isCheckingAuth: false,
    onlineUsers: [],
    stompClient: null,
    socket: null,
    isConnected: false,

    checkAuth: async (token) => {
        try {
            const res = await axiosInstance.get("/user/check-auth", {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            set({authUser: res.data.user});
            await get().connectWebSocket()
        } catch (error) {
            console.log("error in checkAuth", error);
            set({authUser: null});
        } finally {
            set({isCheckingAuth: false});
        }
    },

    signup: async (data) => {
        set({isSigningUp: true});
        try {
            const res = await axiosInstance.post("/user/register", data);
            set({authUser: res.data.user});
            const token = res.data.token;
            localStorage.setItem("token", token);
            toast.success("Account created successfully");
            await get().connectWebSocket();
        } catch (error) {
            console.log("error in signup", error);
            toast.error("Error creating account or user already exists");
        } finally {
            set({isSigningUp: false});
        }
    },

    logout: async (navigate) => {
        try {
            // await axiosInstance.post("/user/logout");
            set({authUser: null});
            localStorage.removeItem("token");
            toast.success("Logged out successfully");
            // navigate("/login"); // Navigate to login page
            get().disconnectWebSocket();
        } catch (error) {
            toast.error("Error logging out", error);
        }
    },

    login: async (data) => {
        set({isLoggingIn: true});
        try {
            const authHeader = `Basic ${btoa(`${data.email}:${data.password}`)}`;
            const res = await axiosInstance.post("/user/login", data, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': authHeader
                }
            });
            set({authUser: res.data.user});
            const token = res.data.token;
            localStorage.setItem("token", token);
            toast.success("Logged in successfully");
            await get().connectWebSocket();
        } catch (error) {
            console.log("error in login", error);
            toast.error("Error logging in");
        } finally {
            set({isLoggingIn: false});
        }
    },

    updateProfile: async (data) => {
        const token = localStorage.getItem("token");
        set({isUpdatingProfile: true});
        try {
            const res = await axiosInstance.put("/user/update-profile", data, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${token}`
                }
            });
            set({authUser: res.data});
            toast.success("Profile updated successfully");
        } catch (error) {
            console.log("error in updateProfile", error);
            toast.error(error);
        } finally {
            set({isUpdatingProfile: false});
        }
    },

    getProfile: async () => {
        const token = localStorage.getItem("token");
        try {
            const res = await axiosInstance.get("/user/profile", {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            if(res.status === 200){
                set({authUser: res.data});
            } else {
                console.log("error in getProfile", res)
            }
        } catch (error) {
            console.log("error in getProfile", error);
        }
    },

    connectWebSocket: async () => {
        await get().getProfile();
        const {authUser} = get();

        if (!authUser || !authUser.userId) {
            toast.error("User is not authenticated. Cannot connect to WebSocket.");
            return;
        }

        const id = authUser.userId;
        const socket = new WebSocket(`${webSocketUrl}/messages?userId=${id}`);
        socket.onopen = () => {
            toast.success("Connected to WebSocket");
            set({isConnected: true, socket: socket});
        };

        socket.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data);
                if (message.type === "ONLINE_USERS") {
                    const online = message.users.split(",");
                    set({onlineUsers: online});
                } else if (message.type === "NEW_MESSAGE") {
                    const {selectedUser, messages} = useChatStore.getState();
                    if (selectedUser && message.senderId === selectedUser.userId) {
                        set({messages: [...messages, message]});
                    }
                }
            } catch (error) {
                console.error("Failed to parse message:", error);
            }
        };

        socket.onerror = (error) => {
            console.error("WebSocket connection error:", error);
            toast.error("Failed to connect to WebSocket");
            set({isConnected: false});
        };
    },

    disconnectWebSocket: () => {
        const {socket} = get();
        if (socket) {
            socket.close();
            toast.success("Disconnected from WebSocket");
            set({isConnected: false});
            set({socket: null});
            console.log("WebSocket connection closed");
        } else {
            console.warn("No WebSocket connection to disconnect");
        }
    },
}));

// Custom hook to use navigate with useAuthStore
import {useNavigate} from "react-router-dom";

export const useAuthStoreWithNavigate = () => {
    const navigate = useNavigate();
    const store = useAuthStore();
    return {
        ...store,
        logout: () => store.logout(navigate),
    };
};