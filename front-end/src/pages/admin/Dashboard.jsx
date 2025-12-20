import { useState, useEffect, useCallback } from 'react';
import Pagination from '../../components/common/Pagination';
import Spinner from '../../components/common/Spinner';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [roleFilter, setRoleFilter] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchQuery);
      setCurrentPage(0);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  useEffect(() => {
    loadUsers();
  }, [currentPage, roleFilter, debouncedSearch]);

  const loadUsers = async () => {
    setLoading(true);
    try {
      let url;
      if (debouncedSearch) {
        url = `/admin/users/search?q=${encodeURIComponent(debouncedSearch)}&page=${currentPage}&size=10`;
        if (roleFilter) {
          url += `&role=${roleFilter}`;
        }
      } else if (roleFilter) {
        url = `/admin/users/role/${roleFilter}?page=${currentPage}&size=10`;
      } else {
        url = `/admin/users?page=${currentPage}&size=10`;
      }
      const response = await api.get(url);
      if (response.data.success) {
        setUsers(response.data.data.content);
        setTotalPages(response.data.data.totalPages);
      }
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDeactivate = async (userId) => {
    if (!confirm('Are you sure you want to deactivate this user?')) return;
    try {
      await api.patch(`/admin/users/${userId}/deactivate`);
      loadUsers();
    } catch (error) {
      console.error('Failed to deactivate user:', error);
    }
  };

  const handleActivate = async (userId) => {
    try {
      await api.patch(`/admin/users/${userId}/activate`);
      loadUsers();
    } catch (error) {
      console.error('Failed to activate user:', error);
    }
  };

  const handleDelete = async (userId) => {
    if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) return;
    try {
      await api.delete(`/admin/users/${userId}`);
      loadUsers();
    } catch (error) {
      console.error('Failed to delete user:', error);
    }
  };

  const clearSearch = () => {
    setSearchQuery('');
    setDebouncedSearch('');
  };

  const getRoleColor = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-100 text-purple-700';
      case 'RECRUITER':
        return 'bg-blue-100 text-blue-700';
      case 'SEEKER':
        return 'bg-green-100 text-green-700';
      default:
        return 'bg-slate-100 text-slate-700';
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4 mb-8">
          <div>
            <h1 className="text-3xl font-bold text-slate-800">Admin Dashboard</h1>
            <p className="text-slate-600 mt-2">Welcome, {user?.firstName}! Manage all users here.</p>
          </div>
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search by name or email..."
                className="input-field pl-10 pr-10 w-full sm:w-64"
              />
              <svg
                className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>
              {searchQuery && (
                <button
                  onClick={clearSearch}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                >
                  ✕
                </button>
              )}
            </div>
            <select
              value={roleFilter}
              onChange={(e) => { setRoleFilter(e.target.value); setCurrentPage(0); }}
              className="input-field w-auto"
            >
              <option value="">All Roles</option>
              <option value="SEEKER">Job Seekers</option>
              <option value="RECRUITER">Recruiters</option>
              <option value="ADMIN">Admins</option>
            </select>
          </div>
        </div>

        {debouncedSearch && (
          <div className="mb-4 text-sm text-slate-600">
            Showing results for "<span className="font-medium">{debouncedSearch}</span>"
            <button onClick={clearSearch} className="ml-2 text-blue-600 hover:text-blue-700">
              Clear
            </button>
          </div>
        )}

        {loading ? (
          <div className="py-16">
            <Spinner size="lg" />
          </div>
        ) : (
          <div className="card overflow-hidden">
            <table className="w-full">
              <thead className="bg-slate-50">
                <tr>
                  <th className="text-left px-4 py-3 text-sm font-medium text-slate-600">User</th>
                  <th className="text-left px-4 py-3 text-sm font-medium text-slate-600">Email</th>
                  <th className="text-left px-4 py-3 text-sm font-medium text-slate-600">Role</th>
                  <th className="text-left px-4 py-3 text-sm font-medium text-slate-600">Status</th>
                  <th className="text-left px-4 py-3 text-sm font-medium text-slate-600">Joined</th>
                  <th className="text-left px-4 py-3 text-sm font-medium text-slate-600">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="px-4 py-8 text-center text-slate-500">
                      {debouncedSearch ? 'No users found matching your search.' : 'No users found.'}
                    </td>
                  </tr>
                ) : (
                  users.map((u) => (
                    <tr key={u.id} className="border-t border-slate-100">
                      <td className="px-4 py-3">
                        <div className="font-medium text-slate-800">{u.firstName} {u.lastName}</div>
                      </td>
                      <td className="px-4 py-3 text-slate-600">{u.email}</td>
                      <td className="px-4 py-3">
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${getRoleColor(u.role)}`}>
                          {u.role}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${u.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                          {u.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-slate-600">
                        {new Date(u.createdAt).toLocaleDateString()}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex gap-2">
                          {u.isActive ? (
                            <button
                              onClick={() => handleDeactivate(u.id)}
                              className="text-orange-600 hover:text-orange-700 text-sm"
                            >
                              Deactivate
                            </button>
                          ) : (
                            <button
                              onClick={() => handleActivate(u.id)}
                              className="text-green-600 hover:text-green-700 text-sm"
                            >
                              Activate
                            </button>
                          )}
                          <button
                            onClick={() => handleDelete(u.id)}
                            className="text-red-600 hover:text-red-700 text-sm"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </div>
    </div>
  );
}
