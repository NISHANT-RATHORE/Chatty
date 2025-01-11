import axios from "axios";

const axiosInstance = axios.create({
    // baseURL: import.meta.env.VITE_BACKEND_URL,
    baseURL: "http://localhost:9001",
    withCredentials: true,
});

export default axiosInstance;