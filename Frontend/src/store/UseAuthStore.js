import axiosInstance from "../lib/axios";
import {create} from "zustand";
import toast from "react-hot-toast";
import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";

export const useAuthStore = create((set, get) => ({
        authUser: null,
        isSigningUp: false,
        isLoggingIn: false,
        isUpdatingProfile: false,
        isCheckingAuth: true,
        onlineUsers: [],
        stompClient: null,
        isConnected: false,

        checkAuth: async () => {
            try {
                const res = await axiosInstance.get("/user/check-auth");
                set({authUser: res.data})
            } catch (error) {
                console.log("error in checkAuth", error)
                set({authUser: null})
            } finally {
                set({isCheckingAuth: false})
            }
        },

        signup: async (data) => {
            set({isSigningUp: true})
            try {
                const res = await axiosInstance.post("/user/register", data);
                set({authUser: res.data})
                toast.success("Account created successfully")
                get().connectWebSocket()
            } catch (error) {
                console.log("error in signup", error)
                toast.error("Error creating account or user already exists")
            } finally {
                set({isSigningUp: false})
            }

        },

        logout: async () => {
            try {
                await axiosInstance.post("/user/logout")
                set({authUser: null})
                toast.success("Logged out successfully")
                get().disconnectWebSocket()
                // eslint-disable-next-line no-unused-vars
            } catch (error) {
                toast.error("Error logging out")
            }
        },

        login: async (data) => {
            set({isLoggingIn: true})
            try {
                const authHeader = `Basic ${btoa(`${data.email}:${data.password}`)}`
                const res = await axiosInstance.post("/user/login", data, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': authHeader
                    }
                })
                set({authUser: res.data})
                toast.success("Logged in successfully")
                get().connectWebSocket()
                console.log(get().isConnected)
            } catch (error) {
                console.log("error in login", error)
                toast.error("Error logging in")
            } finally {
                set({isLoggingIn: false})
            }

        },

        updateProfile: async (data) => {
            set({isUpdatingProfile: true})
            try {
                const res = await axiosInstance.put("/user/update-profile", data, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                })
                set({authUser: res.data})
                toast.success("Profile updated successfully")
            } catch (error) {
                console.log("error in updateProfile", error)
                toast.error(error)
            } finally {
                set({isUpdatingProfile: false})
            }
        },

        getProfile: async () => {
            try {
                const res = await axiosInstance.get("/user/profile");
                set({authUser: res.data})

            } catch (error) {
                console.log("error in getProfile", error)
            }
        },

        connectWebSocket: async () => {

            const sock = new SockJS("http://localhost:9003/messages");
            const client = Stomp.over(sock);

            client.connect(
                {},
                (frame) => {
                    set({stompClient: client});
                    toast.success("Connected to WebSocket");
                    set({isConnected: true});
                    console.log("Connected: " + frame);
                },
                (error) => {
                    console.error("WebSocket connection error:", error);
                    toast.error("Failed to connect to WebSocket");
                    set({isConnected: false});
                }
            );
        },

        disconnectWebSocket: () => {
            const {stompClient} = get();
            if (stompClient) {
                stompClient.disconnect(() => {
                    toast.success("Disconnected from WebSocket");
                    set({isConnected: false});
                });
            }
        },

    }

))