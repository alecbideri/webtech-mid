import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

/**
 * Header component with logo, search, and user menu
 */
export default function Header() {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <header className="bg-white shadow-sm border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-gradient-to-br from-blue-600 to-blue-700 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">FJ</span>
            </div>
            <span className="text-xl font-bold text-slate-800">FindJob</span>
          </Link>

          {/* Navigation */}
          <nav className="hidden md:flex items-center space-x-6">
            <Link to="/jobs" className="text-slate-600 hover:text-blue-600 transition-colors">
              Browse Jobs
            </Link>
            {isAuthenticated && user?.role === 'RECRUITER' && (
              <Link to="/recruiter/dashboard" className="text-slate-600 hover:text-blue-600 transition-colors">
                My Jobs
              </Link>
            )}
            {isAuthenticated && user?.role === 'SEEKER' && (
              <Link to="/seeker/dashboard" className="text-slate-600 hover:text-blue-600 transition-colors">
                My Applications
              </Link>
            )}
            {isAuthenticated && user?.role === 'ADMIN' && (
              <Link to="/admin/dashboard" className="text-slate-600 hover:text-blue-600 transition-colors">
                Admin Panel
              </Link>
            )}
          </nav>

          {/* Auth Buttons */}
          <div className="flex items-center space-x-4">
            {isAuthenticated ? (
              <div className="flex items-center space-x-4">
                <Link to="/profile" className="text-slate-600 hover:text-blue-600 transition-colors">
                  <div className="flex items-center space-x-2">
                    <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                      <span className="text-blue-600 font-medium text-sm">
                        {user?.firstName?.[0]}{user?.lastName?.[0]}
                      </span>
                    </div>
                    <span className="hidden sm:block">{user?.firstName}</span>
                  </div>
                </Link>
                <button
                  onClick={logout}
                  className="text-slate-600 hover:text-red-600 transition-colors"
                >
                  Logout
                </button>
              </div>
            ) : (
              <>
                <Link to="/login" className="text-slate-600 hover:text-blue-600 transition-colors">
                  Login
                </Link>
                <Link to="/register" className="btn-primary">
                  Sign Up
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
