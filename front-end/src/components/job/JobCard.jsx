import { Link } from 'react-router-dom';

/**
 * JobCard component for displaying job listings
 */
export default function JobCard({ job, showApplyButton = false, onApply }) {
  const formatSalary = (salary) => {
    if (!salary) return 'Not specified';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0,
    }).format(salary);
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

  const getJobTypeColor = (type) => {
    switch (type) {
      case 'FULL_TIME':
        return 'bg-purple-100 text-purple-700';
      case 'PART_TIME':
        return 'bg-orange-100 text-orange-700';
      case 'CONTRACT':
        return 'bg-yellow-100 text-yellow-700';
      case 'INTERNSHIP':
        return 'bg-pink-100 text-pink-700';
      case 'REMOTE':
        return 'bg-cyan-100 text-cyan-700';
      default:
        return 'bg-slate-100 text-slate-700';
    }
  };

  return (
    <div className="card hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start">
        <div className="flex-1">
          <div className="flex items-center space-x-2 mb-2">
            <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(job.status)}`}>
              {job.status}
            </span>
            <span className={`px-2 py-1 text-xs font-medium rounded-full ${getJobTypeColor(job.jobType)}`}>
              {job.jobType?.replace('_', ' ')}
            </span>
          </div>

          <Link to={`/jobs/${job.id}`}>
            <h3 className="text-lg font-semibold text-slate-800 hover:text-blue-600 transition-colors">
              {job.title}
            </h3>
          </Link>

          <p className="text-slate-600 mt-1">{job.company}</p>

          <div className="flex items-center space-x-4 mt-3 text-sm text-slate-500">
            <span className="flex items-center">
              <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              {job.location}
            </span>
            <span className="flex items-center">
              <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {formatSalary(job.salary)}
            </span>
          </div>
        </div>

        {showApplyButton && (
          <>
            {job.status === 'OPEN' && (
              <button
                onClick={() => onApply?.(job)}
                className="btn-primary ml-4"
              >
                Apply Now
              </button>
            )}
            {job.status === 'FILLED' && (
              <span className="ml-4 px-4 py-2 bg-blue-100 text-blue-700 font-semibold rounded-lg">
                ✓ Position Filled
              </span>
            )}
            {job.status === 'CLOSED' && (
              <span className="ml-4 px-4 py-2 bg-slate-100 text-slate-500 font-semibold rounded-lg">
                Not Accepting
              </span>
            )}
          </>
        )}
      </div>

      <p className="text-slate-600 mt-4 line-clamp-2">{job.description}</p>

      <div className="flex items-center justify-between mt-4 pt-4 border-t border-slate-100">
        <span className="text-sm text-slate-500">
          Posted {new Date(job.createdAt).toLocaleDateString()}
        </span>
        <span className="text-sm text-slate-500">
          {job.applicationCount} application{job.applicationCount !== 1 ? 's' : ''}
        </span>
      </div>
    </div>
  );
}
