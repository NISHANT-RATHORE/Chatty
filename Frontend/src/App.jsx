import {useEffect} from 'react';
import Navbar from './Components/Navbar';
import {Routes, Route, Navigate, useNavigate} from 'react-router-dom';
import Home from './pages/Home';
import SignUp from './pages/SignUp';
import Login from './pages/Login';
import Profile from './pages/Profile';
import {useAuthStore} from './store/UseAuthStore';
import {Loader} from 'lucide-react';
import {Toaster} from 'react-hot-toast';

const App = () => {
    const {authUser, checkAuth, isCheckingAuth, getProfile} = useAuthStore();
    const navigate = useNavigate(); // Initialize useNavigate here

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            getProfile(); // Ensure only one function is used if possible
            checkAuth(token);
        }
    }, [checkAuth, getProfile]);
    console.log(authUser)


    if (isCheckingAuth && !authUser) {
        return (
            <div className="flex items-center justify-center h-screen">
                <Loader size={10} className="animate-spin" />
            </div>
        );
    }

    return (
        <div>
            <Navbar />
            <Routes>
                <Route path="/" element={authUser ? <Home /> : <Navigate to="/login" />} />
                <Route path="/sign-up" element={!authUser ? <SignUp /> : <Navigate to="/" />} />
                <Route path="/login" element={!authUser ? <Login /> : <Navigate to="/" />} />
                <Route path="/profile" element={authUser ? <Profile /> : <Navigate to="/login" />} />
            </Routes>
            <Toaster />
        </div>
    );
};

export default App;
