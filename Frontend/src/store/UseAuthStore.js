import axiosInstance from "../lib/axios";
import { create } from "zustand";
import SignUp from "../pages/SignUp";
import toast from "react-hot-toast";

export const useAuthStore = create((set) => ({
    authUser : null,
    isSigningUp : false,
    isLoggingIn : false,
    isUpdatingProfile : false,
    isCheckingAuth : true,

    checkAuth: async () => {
        try {
            const res = await axiosInstance.get("/check-auth");
            set({authUser:res.data})
        } catch (error) {
            // console.log("error in checkAuth", error)
            set({authUser:null})
        } finally{
            set({isCheckingAuth:false})
        }
    },

    signup : async (data) => {
        set({isSigningUp:true})
        try {
            const res = await axiosInstance.post("/register", data);
            set({authUser:res.data})
            console.log({authUser:res.data})
            toast.success("Account created successfully")
        } catch (error) {
            console.log("error in signup", error)
            toast.error("Error creating account or user already exists")
        } finally{
            set({isSigningUp:false})
        }
        
    },

    logout : async () => {
        try {
            await axiosInstance.post("/logout")
            set({authUser:null})
            toast.success("Logged out successfully")
        } catch (error) {
            toast.error("Error logging out")
        }
    },

    login : async (data) => {
        set({isLoggingIn:true})
        try { 
            const authHeader = `Basic ${btoa(`${data.email}:${data.password}`)}`
            const res = await axiosInstance.post("/login", data,{
                headers:{
                    'Content-Type': 'application/json',
                    'Authorization': authHeader
                }
            })
            set({authUser:res.data})
            console.log({authUser:res.data})
            console.log({authUser:res.data.email})
            toast.success("Logged in successfully")
        } catch (error) {
            console.log("error in login", error)
            toast.error("Error logging in")
        } finally{
            set({isLoggingIn:false})
        }

    },

    updateProfile : async(data) => {
        set({isUpdatingProfile:true})
        try {
            const res = await axiosInstance.put("/update-profile", data,{
                headers:{
                    'Content-Type': 'multipart/form-data'
                }
            })
            set({authUser:res.data})
            toast.success("Profile updated successfully")
        } catch (error) {
            console.log("error in updateProfile", error)
            toast.error(error)
        } finally{
            set({isUpdatingProfile:false})
        }
    },

        getProfile : async () => {
            try {
                const res = await axiosInstance.get("/profile");
                set({authUser:res.data})
            } catch (error) {
                console.log("error in getProfile", error)
            }
        }

}

))