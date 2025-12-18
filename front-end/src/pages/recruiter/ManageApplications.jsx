import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Pagination from '../../components/common/Pagination';
import Spinner from '../../components/common/Spinner';
import applicationService from '../../services/applicationService';
import jobService from '../../services/jobService';

/**
 * Manage Applications page for recruiters
 */
export default function ManageApplications() {
  const { jobId } = useParams();
  const [job, setJob] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadData();
  }, [jobId, currentPage]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [jobResult, appsResult] = await Promise.all([
        jobService.getJobById(jobId),
        applicationService.getApplicationsForJob(jobId, currentPage),
      ]);

      if (jobResult.success) setJob(jobResult.data);
      if (appsResult.success) {
        setApplications(appsResult.data.content);
        setTotalPages(appsResult.data.totalPages);
      }
    } catch (error) {
      console.error('Failed to load data:', error);
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

  const handleStatusUpdate = async (appId, status) => {
    const notes = status === 'REJECTED'
      ? prompt('Enter rejection reason (optional):')
      : '';

    try {
      await applicationService.updateApplicationStatus(appId, status, notes || '');
      loadData();
    } catch (error) {
      console.error('Failed to update status:', error);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-800">
            Applications for: {job?.title}
          </h1>
          <p className="text-slate-600 mt-2">{job?.company} • {job?.location}</p>
        </div>

        {applications.length === 0 ? (
          <div className="card text-center py-16">
            <p className="text-slate-600 text-lg">No applications yet.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {applications.map((app) => (
              <div key={app.id} className="card">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-800">{app.seekerName}</h3>
                    <p className="text-slate-600">{app.seekerEmail}</p>
                    <div className="flex items-center gap-4 mt-2 text-sm text-slate-500">
                      <span>Applied: {new Date(app.appliedAt).toLocaleDateString()}</span>
                      {app.resumeFilename && (
                        <span className="text-blue-600">📎 {app.resumeFilename}</span>
                      )}
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(app.status)}`}>
                      {app.status}
                    </span>
                  </div>
                </div>

                {app.coverLetter && (
                  <div className="mt-4 p-3 bg-slate-50 rounded-lg">
                    <p className="text-sm text-slate-600">
                      <strong>Cover Letter:</strong> {app.coverLetter}
                    </p>
                  </div>
                )}

                <div className="mt-4 pt-4 border-t border-slate-100 flex gap-2">
                  <button
                    onClick={() => handleStatusUpdate(app.id, 'REVIEWED')}
                    className="btn-secondary text-sm"
                    disabled={app.status !== 'PENDING'}
                  >
                    Mark Reviewed
                  </button>
                  <button
                    onClick={() => handleStatusUpdate(app.id, 'ACCEPTED')}
                    className="btn-success text-sm"
                    disabled={app.status === 'ACCEPTED' || app.status === 'REJECTED'}
                  >
                    Accept
                  </button>
                  <button
                    onClick={() => handleStatusUpdate(app.id, 'REJECTED')}
                    className="btn-danger text-sm"
                    disabled={app.status === 'ACCEPTED' || app.status === 'REJECTED'}
                  >
                    Reject
                  </button>
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
