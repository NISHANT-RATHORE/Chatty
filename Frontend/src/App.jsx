import React, { useEffect } from 'react'
import Navbar from './Components/Navbar'
import { Routes, Route, Navigate } from 'react-router-dom'
import Home from './pages/Home'
import SignUp from './pages/SignUp'
import Login from './pages/Login'
import Settings from './pages/Settings'
import Profile from './pages/Profile'
import { useAuthStore } from './store/UseAuthStore'
import { Loader } from 'lucide-react'
import { Toaster } from 'react-hot-toast'

const App = () => {
  const {authUser,checkAuth,isCheckingAuth} = useAuthStore()

  useEffect(()=>{
    checkAuth()
  },[checkAuth])

  if(isCheckingAuth && !authUser) return(
    <div className='flex items-center justify-center h-screen'>
      <Loader className='size-10 animate-spin'/>
      </div>
  )

  return (
    <div>
      <Navbar />
      <Routes>
        <Route path="/" element={authUser ? <Home /> : <Navigate to="/login"/>} />
        <Route path="/sign-up" element={!authUser ? <SignUp /> : <Navigate to="/"/>} />
        <Route path="/login" element={!authUser ? <Login /> : <Navigate to="/"/>} />
        <Route path="/setting" element={<Settings />} />
        <Route path='/profile' element={authUser ? <Profile /> : <Navigate to="/login"/>} />
      </Routes>

      <Toaster />
    </div>
  )
}

export default App