import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Pagination from '../../components/common/Pagination';
import Spinner from '../../components/common/Spinner';
import jobService from '../../services/jobService';
import { useAuth } from '../../context/AuthContext';

/**
 * Recruiter Dashboard - Manage job postings
 */
export default function RecruiterDashboard() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const { user } = useAuth();

  useEffect(() => {
    loadMyJobs();
  }, [currentPage]);

  const loadMyJobs = async () => {
    setLoading(true);
    try {
      const result = await jobService.getMyJobs(currentPage);
      if (result.success) {
        setJobs(result.data.content);
        setTotalPages(result.data.totalPages);
      }
    } catch (error) {
      console.error('Failed to load jobs:', error);
    } finally {
      setLoading(false);
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
      default:
        return 'bg-slate-100 text-slate-700';
    }
  };

  const handleStatusChange = async (jobId, status) => {
    try {
      await jobService.updateJobStatus(jobId, status);
      loadMyJobs();
    } catch (error) {
      console.error('Failed to update status:', error);
    }
  };

  const handleDelete = async (jobId) => {
    if (!confirm('Are you sure you want to delete this job?')) return;

    try {
      await jobService.deleteJob(jobId);
      loadMyJobs();
    } catch (error) {
      console.error('Failed to delete job:', error);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-slate-800">My Job Postings</h1>
            <p className="text-slate-600 mt-2">Welcome back, {user?.firstName}!</p>
          </div>
          <Link to="/recruiter/post-job" className="btn-primary">
            Post New Job
          </Link>
        </div>

        {loading ? (
          <div className="py-16">
            <Spinner size="lg" />
          </div>
        ) : jobs.length === 0 ? (
          <div className="card text-center py-16">
            <p className="text-slate-600 text-lg mb-4">You haven't posted any jobs yet.</p>
            <Link to="/recruiter/post-job" className="btn-primary">Post Your First Job</Link>
          </div>
        ) : (
          <div className="space-y-4">
            {jobs.map((job) => (
              <div key={job.id} className="card">
                <div className="flex justify-between items-start">
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <h3 className="text-lg font-semibold text-slate-800">{job.title}</h3>
                      <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(job.status)}`}>
                        {job.status}
                      </span>
                    </div>
                    <p className="text-slate-600">{job.company} • {job.location}</p>
                    <div className="flex items-center gap-4 mt-2 text-sm text-slate-500">
                      <span>{job.applicationCount} applications</span>
                      <span>Posted: {new Date(job.createdAt).toLocaleDateString()}</span>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Link
                      to={`/recruiter/jobs/${job.id}/applications`}
                      className="btn-secondary text-sm"
                    >
                      View Applications
                    </Link>
                    <select
                      value={job.status}
                      onChange={(e) => handleStatusChange(job.id, e.target.value)}
                      className="input-field text-sm w-auto"
                    >
                      <option value="OPEN">Open</option>
                      <option value="CLOSED">Closed</option>
                      <option value="FILLED">Filled</option>
                    </select>
                    <button
                      onClick={() => handleDelete(job.id)}
                      className="text-red-600 hover:text-red-700"
                    >
                      Delete
                    </button>
                  </div>
                </div>
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
