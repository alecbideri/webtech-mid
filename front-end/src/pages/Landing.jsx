import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Landing() {
  const { isAuthenticated, user } = useAuth();

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-20 pb-16">
        <div className="text-center">
          <h1 className="text-5xl md:text-6xl font-bold text-slate-800 mb-6">
            Find Your <span className="text-blue-600">Dream Job</span>
          </h1>
          <p className="text-xl text-slate-600 max-w-2xl mx-auto mb-8">
            Connect with top employers and discover opportunities that match your skills and aspirations.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link to="/jobs" className="btn-primary text-lg px-8 py-3">
              Browse Jobs
            </Link>
            {!isAuthenticated && (
              <Link to="/register" className="btn-secondary text-lg px-8 py-3">
                Create Account
              </Link>
            )}
            {isAuthenticated && user?.role === 'RECRUITER' && (
              <Link to="/recruiter/post-job" className="btn-success text-lg px-8 py-3">
                Post a Job
              </Link>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-20">
          <div className="card text-center">
            <div className="text-4xl font-bold text-blue-600 mb-2">1000+</div>
            <div className="text-slate-600">Active Jobs</div>
          </div>
          <div className="card text-center">
            <div className="text-4xl font-bold text-emerald-600 mb-2">500+</div>
            <div className="text-slate-600">Companies</div>
          </div>
          <div className="card text-center">
            <div className="text-4xl font-bold text-purple-600 mb-2">10k+</div>
            <div className="text-slate-600">Job Seekers</div>
          </div>
        </div>
      </section>

      <section className="bg-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl font-bold text-center text-slate-800 mb-12">
            Why Choose FindJob?
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center p-6">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Easy Job Search</h3>
              <p className="text-slate-600">Find jobs that match your skills with our powerful search.</p>
            </div>

            <div className="text-center p-6">
              <div className="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Quick Apply</h3>
              <p className="text-slate-600">Apply to jobs with just a few clicks and track your applications.</p>
            </div>

            <div className="text-center p-6">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Email Notifications</h3>
              <p className="text-slate-600">Get instant updates when your application status changes.</p>
            </div>
          </div>
        </div>
      </section>

      <section className="py-20">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <h2 className="text-3xl font-bold text-slate-800 mb-4">
            Ready to Start Your Journey?
          </h2>
          <p className="text-lg text-slate-600 mb-8">
            Join thousands of job seekers who have found their dream jobs through FindJob.
          </p>
          {!isAuthenticated && (
            <Link to="/register" className="btn-primary text-lg px-8 py-3">
              Get Started Free
            </Link>
          )}
        </div>
      </section>

      <footer className="bg-slate-800 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <div className="flex items-center justify-center space-x-2 mb-4">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <span className="font-bold text-sm">FJ</span>
            </div>
            <span className="text-xl font-bold">FindJob</span>
          </div>
          <p className="text-slate-400">© 2024 FindJob. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
