import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { useState } from 'react';
import Header from './components/common/Header';
import Sidebar from './components/common/Sidebar';
import Footer from './components/common/Footer';

import Landing from './pages/Landing';
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import OAuthCallback from './pages/OAuthCallback';
import Jobs from './pages/Jobs';
import Profile from './pages/Profile';
import SeekerDashboard from './pages/seeker/Dashboard';
import RecruiterDashboard from './pages/recruiter/Dashboard';
import PostJob from './pages/recruiter/PostJob';
import ManageApplications from './pages/recruiter/ManageApplications';
import AdminDashboard from './pages/admin/Dashboard';

function ProtectedRoute({ children, allowedRoles = [] }) {
  const { isAuthenticated, user, loading } = useAuth();

  if (loading) return null;

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}

function Layout({ children, showSidebar = false, showFooter = true }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen flex flex-col">
      <Header onMenuClick={() => setSidebarOpen(!sidebarOpen)} />
      <div className="flex flex-1">
        {showSidebar && (
          <Sidebar isOpen={sidebarOpen} onToggle={() => setSidebarOpen(!sidebarOpen)} />
        )}
        <main className={`flex-1 ${showSidebar ? 'lg:ml-64' : ''}`}>
          {children}
        </main>
      </div>
      {showFooter && <Footer />}
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Layout showFooter={true}><Landing /></Layout>} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/oauth/callback" element={<OAuthCallback />} />
          <Route path="/jobs" element={<Layout showFooter={true}><Jobs /></Layout>} />

          <Route
            path="/profile"
            element={
              <ProtectedRoute allowedRoles={['SEEKER', 'RECRUITER', 'ADMIN']}>
                <Layout showSidebar={true} showFooter={false}><Profile /></Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/seeker/dashboard"
            element={
              <ProtectedRoute allowedRoles={['SEEKER']}>
                <Layout showSidebar={true} showFooter={false}><SeekerDashboard /></Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/recruiter/dashboard"
            element={
              <ProtectedRoute allowedRoles={['RECRUITER']}>
                <Layout showSidebar={true} showFooter={false}><RecruiterDashboard /></Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/recruiter/post-job"
            element={
              <ProtectedRoute allowedRoles={['RECRUITER']}>
                <Layout showSidebar={true} showFooter={false}><PostJob /></Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/recruiter/jobs/:jobId/applications"
            element={
              <ProtectedRoute allowedRoles={['RECRUITER']}>
                <Layout showSidebar={true} showFooter={false}><ManageApplications /></Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Layout showSidebar={true} showFooter={false}><AdminDashboard /></Layout>
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
