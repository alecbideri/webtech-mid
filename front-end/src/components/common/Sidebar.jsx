import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useState } from 'react';

export default function Sidebar({ isOpen, onToggle }) {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);

  if (!isAuthenticated) return null;

  const isActive = (path) => location.pathname === path;

  const getNavItems = () => {
    const commonItems = [
      { path: '/jobs', label: 'Browse Jobs', icon: '🔍' },
    ];

    switch (user?.role) {
      case 'ADMIN':
        return [
          { path: '/admin/dashboard', label: 'Dashboard', icon: '📊' },
          ...commonItems,
        ];
      case 'RECRUITER':
        return [
          { path: '/recruiter/dashboard', label: 'My Jobs', icon: '📋' },
          { path: '/recruiter/post-job', label: 'Post a Job', icon: '➕' },
          ...commonItems,
        ];
      case 'SEEKER':
        return [
          { path: '/seeker/dashboard', label: 'My Applications', icon: '📄' },
          ...commonItems,
        ];
      default:
        return commonItems;
    }
  };

  const navItems = getNavItems();

  return (
    <>
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={onToggle}
        />
      )}

      <aside
        className={`
          fixed top-16 left-0 h-[calc(100vh-4rem)] bg-white border-r border-slate-200 z-50
          transition-all duration-300 ease-in-out
          ${isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
          ${collapsed ? 'w-16' : 'w-64'}
        `}
      >
        <button
          onClick={() => setCollapsed(!collapsed)}
          className="hidden lg:flex absolute -right-3 top-4 w-6 h-6 bg-white border border-slate-200 rounded-full items-center justify-center text-slate-500 hover:text-slate-700 shadow-sm"
        >
          {collapsed ? '→' : '←'}
        </button>

        <nav className="p-4 space-y-2">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              onClick={() => isOpen && onToggle?.()}
              className={`
                flex items-center gap-3 px-4 py-3 rounded-lg transition-colors
                ${isActive(item.path)
                  ? 'bg-blue-50 text-blue-700 font-medium'
                  : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                }
              `}
              title={collapsed ? item.label : undefined}
            >
              <span className="text-xl">{item.icon}</span>
              {!collapsed && <span>{item.label}</span>}
            </Link>
          ))}
        </nav>

        {!collapsed && (
          <div className="absolute bottom-4 left-4 right-4 p-3 bg-slate-50 rounded-lg">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <span className="text-blue-600 font-medium text-sm">
                  {user?.firstName?.[0]}{user?.lastName?.[0]}
                </span>
              </div>
              <div className="min-w-0">
                <div className="font-medium text-slate-800 truncate">
                  {user?.firstName} {user?.lastName}
                </div>
                <div className="text-xs text-slate-500 capitalize">
                  {user?.role?.toLowerCase()}
                </div>
              </div>
            </div>
          </div>
        )}
      </aside>
    </>
  );
}
