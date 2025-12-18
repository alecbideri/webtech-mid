import { useState, useEffect } from 'react';
import Pagination from '../../components/common/Pagination';
import Spinner from '../../components/common/Spinner';
import applicationService from '../../services/applicationService';
import { useAuth } from '../../context/AuthContext';

/**
 * Seeker Dashboard - View and manage applications
 */
export default function SeekerDashboard() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const { user } = useAuth();

  useEffect(() => {
    loadApplications();
  }, [currentPage]);

  const loadApplications = async () => {
    setLoading(true);
    try {
      const result = await applicationService.getMyApplications(currentPage);
      if (result.success) {
        setApplications(result.data.content);
        setTotalPages(result.data.totalPages);
      }
    } catch (error) {
      console.error('Failed to load applications:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-700';
      case 'REVIEWED':
        return 'bg-blue-100 text-blue-700';
      case 'ACCEPTED':
        return 'bg-green-100 text-green-700';
      case 'REJECTED':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-slate-100 text-slate-700';
    }
  };

  const handleWithdraw = async (id) => {
    if (!confirm('Are you sure you want to withdraw this application?')) return;

    try {
      await applicationService.withdrawApplication(id);
      loadApplications();
    } catch (error) {
      console.error('Failed to withdraw application:', error);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-800">My Applications</h1>
          <p className="text-slate-600 mt-2">Welcome back, {user?.firstName}!</p>
        </div>

        {loading ? (
          <div className="py-16">
            <Spinner size="lg" />
          </div>
        ) : applications.length === 0 ? (
          <div className="card text-center py-16">
            <p className="text-slate-600 text-lg mb-4">You haven't applied to any jobs yet.</p>
            <a href="/jobs" className="btn-primary">Browse Jobs</a>
          </div>
        ) : (
          <div className="space-y-4">
            {applications.map((app) => (
              <div key={app.id} className="card">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-800">{app.jobTitle}</h3>
                    <p className="text-slate-600">{app.company}</p>
                    <div className="flex items-center gap-4 mt-2 text-sm text-slate-500">
                      <span>Applied: {new Date(app.appliedAt).toLocaleDateString()}</span>
                      {app.reviewedAt && (
                        <span>Reviewed: {new Date(app.reviewedAt).toLocaleDateString()}</span>
                      )}
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(app.status)}`}>
                      {app.status}
                    </span>
                    {app.status === 'PENDING' && (
                      <button
                        onClick={() => handleWithdraw(app.id)}
                        className="text-red-600 hover:text-red-700 text-sm"
                      >
                        Withdraw
                      </button>
                    )}
                  </div>
                </div>
                {app.reviewerNotes && (
                  <div className="mt-4 p-3 bg-slate-50 rounded-lg">
                    <p className="text-sm text-slate-600">
                      <strong>Recruiter Notes:</strong> {app.reviewerNotes}
                    </p>
                  </div>
                )}
              </div>
            ))}
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
