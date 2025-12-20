import { useState, useEffect } from 'react';
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
  const [stats, setStats] = useState({ totalJobs: 0, totalApplications: 0, totalRecruiters: 0, totalSeekers: 0 });
  const [recentJobs, setRecentJobs] = useState([]);
  const [recentApplications, setRecentApplications] = useState([]);
  const { user } = useAuth();

  useEffect(() => {
    loadStats();
    loadRecentData();
  }, []);

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

  const loadStats = async () => {
    try {
      const response = await api.get('/admin/stats');
      if (response.data.success) {
        setStats(response.data.data);
      }
    } catch (error) {
      console.error('Failed to load stats:', error);
    }
  };

  const loadRecentData = async () => {
    try {
      const [jobsRes, appsRes] = await Promise.all([
        api.get('/admin/recent-jobs'),
        api.get('/admin/recent-applications')
      ]);
      if (jobsRes.data.success) setRecentJobs(jobsRes.data.data);
      if (appsRes.data.success) setRecentApplications(appsRes.data.data);
    } catch (error) {
      console.error('Failed to load recent data:', error);
    }
  };

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

  const getStatusColor = (status) => {
    switch (status) {
      case 'OPEN':
        return 'bg-green-100 text-green-700';
      case 'CLOSED':
        return 'bg-red-100 text-red-700';
      case 'FILLED':
        return 'bg-blue-100 text-blue-700';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-700';
      case 'ACCEPTED':
        return 'bg-green-100 text-green-700';
      case 'REJECTED':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-slate-100 text-slate-700';
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-800">Admin Dashboard</h1>
          <p className="text-slate-600 mt-2">Welcome, {user?.firstName}! Manage all users here.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="card bg-gradient-to-br from-blue-500 to-blue-600 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm font-medium">Total Jobs</p>
                <p className="text-3xl font-bold mt-1">{stats.totalJobs}</p>
              </div>
              <div className="w-12 h-12 bg-blue-400/30 rounded-full flex items-center justify-center">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="card bg-gradient-to-br from-green-500 to-green-600 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-green-100 text-sm font-medium">Total Applications</p>
                <p className="text-3xl font-bold mt-1">{stats.totalApplications}</p>
              </div>
              <div className="w-12 h-12 bg-green-400/30 rounded-full flex items-center justify-center">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="card bg-gradient-to-br from-purple-500 to-purple-600 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-purple-100 text-sm font-medium">Total Recruiters</p>
                <p className="text-3xl font-bold mt-1">{stats.totalRecruiters}</p>
              </div>
              <div className="w-12 h-12 bg-purple-400/30 rounded-full flex items-center justify-center">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
              </div>
            </div>
          </div>

          <div className="card bg-gradient-to-br from-orange-500 to-orange-600 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-orange-100 text-sm font-medium">Total Job Seekers</p>
                <p className="text-3xl font-bold mt-1">{stats.totalSeekers}</p>
              </div>
              <div className="w-12 h-12 bg-orange-400/30 rounded-full flex items-center justify-center">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4 mb-6">
          <h2 className="text-xl font-semibold text-slate-800">User Management</h2>
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search by name or email..."
                className="border border-slate-200 rounded-lg px-4 py-2 w-[250px] sm"
              />
              <svg
                className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400"
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
          <div className="card overflow-hidden mb-8">
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

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mt-8">
          <div className="card">
            <h3 className="text-lg font-semibold text-slate-800 mb-4">Recent Jobs</h3>
            {recentJobs.length === 0 ? (
              <p className="text-slate-500 text-center py-4">No jobs yet</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-slate-50">
                    <tr>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Title</th>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Company</th>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Status</th>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Posted</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentJobs.map((job) => (
                      <tr key={job.id} className="border-t border-slate-100">
                        <td className="px-3 py-2 text-sm font-medium text-slate-800">{job.title}</td>
                        <td className="px-3 py-2 text-sm text-slate-600">{job.company}</td>
                        <td className="px-3 py-2">
                          <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(job.status)}`}>
                            {job.status}
                          </span>
                        </td>
                        <td className="px-3 py-2 text-xs text-slate-500">
                          {new Date(job.createdAt).toLocaleDateString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          <div className="card">
            <h3 className="text-lg font-semibold text-slate-800 mb-4">Recent Applications</h3>
            {recentApplications.length === 0 ? (
              <p className="text-slate-500 text-center py-4">No applications yet</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-slate-50">
                    <tr>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Applicant</th>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Job</th>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Status</th>
                      <th className="text-left px-3 py-2 text-xs font-medium text-slate-600">Applied</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentApplications.map((app) => (
                      <tr key={app.id} className="border-t border-slate-100">
                        <td className="px-3 py-2 text-sm font-medium text-slate-800">{app.seekerName}</td>
                        <td className="px-3 py-2 text-sm text-slate-600">{app.jobTitle}</td>
                        <td className="px-3 py-2">
                          <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(app.status)}`}>
                            {app.status}
                          </span>
                        </td>
                        <td className="px-3 py-2 text-xs text-slate-500">
                          {new Date(app.appliedAt).toLocaleDateString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
