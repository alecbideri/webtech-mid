import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import JobCard from '../components/job/JobCard';
import Pagination from '../components/common/Pagination';
import Spinner from '../components/common/Spinner';
import Modal from '../components/common/Modal';
import jobService from '../services/jobService';
import applicationService from '../services/applicationService';
import { useAuth } from '../context/AuthContext';

export default function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchParams, setSearchParams] = useSearchParams();
  const { isAuthenticated, hasRole } = useAuth();

  const [applyModal, setApplyModal] = useState({ open: false, job: null });
  const [coverLetter, setCoverLetter] = useState('');
  const [resume, setResume] = useState(null);
  const [applying, setApplying] = useState(false);
  const [applySuccess, setApplySuccess] = useState('');
  const [applyError, setApplyError] = useState('');

  useEffect(() => {
    loadJobs();
  }, [currentPage, searchParams]);

  const loadJobs = async () => {
    setLoading(true);
    try {
      const keyword = searchParams.get('search') || '';
      setSearchKeyword(keyword);

      let result;
      if (keyword) {
        result = await jobService.searchJobs(keyword, currentPage);
      } else {
        result = await jobService.getOpenJobs(currentPage);
      }

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

  const handleSearch = (e) => {
    e.preventDefault();
    setCurrentPage(0);
    if (searchKeyword.trim()) {
      setSearchParams({ search: searchKeyword });
    } else {
      setSearchParams({});
    }
  };

  const handleApply = (job) => {
    setApplyModal({ open: true, job });
    setCoverLetter('');
    setResume(null);
    setApplySuccess('');
    setApplyError('');
  };

  const submitApplication = async () => {
    setApplying(true);
    setApplyError('');

    try {
      const result = await applicationService.applyForJob(
        applyModal.job.id,
        coverLetter,
        resume
      );

      if (result.success) {
        setApplySuccess('Application submitted successfully!');
        setTimeout(() => {
          setApplyModal({ open: false, job: null });
        }, 2000);
      } else {
        setApplyError(result.message || 'Failed to submit application');
      }
    } catch (error) {
      setApplyError(error.response?.data?.message || 'An error occurred');
    } finally {
      setApplying(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <form onSubmit={handleSearch} className="flex gap-4">
            <input
              type="text"
              placeholder="Search jobs by title, company, or location..."
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              className="flex-1 input-field"
            />
            <button type="submit" className="btn-primary px-6">
              Search
            </button>
          </form>
        </div>

        {loading ? (
          <div className="py-16">
            <Spinner size="lg" />
          </div>
        ) : jobs.length === 0 ? (
          <div className="text-center py-16">
            <p className="text-slate-600 text-lg">No jobs found</p>
          </div>
        ) : (
          <div className="space-y-4">
            {jobs.map((job) => (
              <JobCard
                key={job.id}
                job={job}
                showApplyButton={isAuthenticated && hasRole('SEEKER')}
                onApply={handleApply}
              />
            ))}
          </div>
        )}

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </div>

      <Modal
        isOpen={applyModal.open}
        onClose={() => setApplyModal({ open: false, job: null })}
        title={`Apply for ${applyModal.job?.title}`}
        size="lg"
      >
        {applySuccess ? (
          <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-center">
            {applySuccess}
          </div>
        ) : (
          <div className="space-y-4">
            {applyError && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
                {applyError}
              </div>
            )}

            <div>
              <label className="label">Cover Letter (Optional)</label>
              <textarea
                value={coverLetter}
                onChange={(e) => setCoverLetter(e.target.value)}
                className="input-field h-32"
                placeholder="Tell the employer why you're a good fit for this role..."
              />
            </div>

            <div>
              <label className="label">Resume (Optional)</label>
              <input
                type="file"
                accept=".pdf,.doc,.docx"
                onChange={(e) => setResume(e.target.files[0])}
                className="input-field"
              />
              <p className="text-sm text-slate-500 mt-1">Accepted formats: PDF, DOC, DOCX</p>
            </div>

            <div className="flex justify-end gap-4 pt-4">
              <button
                onClick={() => setApplyModal({ open: false, job: null })}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                onClick={submitApplication}
                disabled={applying}
                className="btn-primary"
              >
                {applying ? 'Submitting...' : 'Submit Application'}
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
