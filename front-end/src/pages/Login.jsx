import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import GoogleLoginButton from '../components/common/GoogleLoginButton';
import { authApi } from '../services/profileApi';

export default function Login() {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [otpCode, setOtpCode] = useState('');
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');
  const [loading, setLoading] = useState(false);
  const [requires2FA, setRequires2FA] = useState(false);
  const [pendingEmail, setPendingEmail] = useState('');
  const { login, setUser } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const oauthError = searchParams.get('error');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setInfo('');
    setLoading(true);

    try {
      const result = await login(formData);
      if (result.success) {
        const data = result.data;

        if (data.requires2FA) {
          setRequires2FA(true);
          setPendingEmail(formData.email);
          setInfo('A verification code has been sent to your email.');
          setLoading(false);
          return;
        }

        const role = data.role;
        if (role === 'ADMIN') {
          navigate('/admin/dashboard');
        } else if (role === 'RECRUITER') {
          navigate('/recruiter/dashboard');
        } else {
          navigate('/seeker/dashboard');
        }
      } else {
        setError(result.message || 'Login failed');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authApi.verifyOtp(pendingEmail, otpCode);
      if (response.success) {
        const data = response.data;

        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data));
        setUser(data);

        const role = data.role;
        if (role === 'ADMIN') {
          navigate('/admin/dashboard');
        } else if (role === 'RECRUITER') {
          navigate('/recruiter/dashboard');
        } else {
          navigate('/seeker/dashboard');
        }
      } else {
        setError(response.message || 'Invalid verification code');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid or expired verification code');
    } finally {
      setLoading(false);
    }
  };

  const handleBackToLogin = () => {
    setRequires2FA(false);
    setPendingEmail('');
    setOtpCode('');
    setInfo('');
    setError('');
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center py-12 px-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex items-center space-x-2">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-600 to-blue-700 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold">FJ</span>
            </div>
            <span className="text-2xl font-bold text-slate-800">FindJob</span>
          </Link>
          <h2 className="mt-6 text-3xl font-bold text-slate-800">
            {requires2FA ? 'Verify Your Identity' : 'Welcome back'}
          </h2>
          <p className="mt-2 text-slate-600">
            {requires2FA ? 'Enter the code sent to your email' : 'Sign in to your account'}
          </p>
        </div>

        <div className="card">
          {(error || oauthError) && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">
              {error || 'Google sign-in failed. Please try again.'}
            </div>
          )}

          {info && (
            <div className="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded-lg mb-4">
              {info}
            </div>
          )}

          {requires2FA ? (
            <form onSubmit={handleOtpSubmit} className="space-y-6">
              <div>
                <label className="label" htmlFor="otpCode">Verification Code</label>
                <input
                  type="text"
                  id="otpCode"
                  name="otpCode"
                  value={otpCode}
                  onChange={(e) => setOtpCode(e.target.value)}
                  className="input-field text-center text-2xl tracking-widest"
                  placeholder="000000"
                  maxLength={6}
                  required
                  autoFocus
                />
                <p className="text-sm text-slate-500 mt-2">
                  Enter the 6-digit code sent to {pendingEmail}
                </p>
              </div>

              <button
                type="submit"
                disabled={loading || otpCode.length !== 6}
                className="w-full btn-primary py-3 disabled:opacity-50"
              >
                {loading ? 'Verifying...' : 'Verify Code'}
              </button>

              <button
                type="button"
                onClick={handleBackToLogin}
                className="w-full text-blue-600 hover:text-blue-700 font-medium py-2"
              >
                ← Back to login
              </button>
            </form>
          ) : (
            <>
              <div className="mb-6">
                <GoogleLoginButton text="Sign in with Google" />
              </div>

              <div className="relative mb-6">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-slate-200"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-slate-500">or continue with email</span>
                </div>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="label" htmlFor="email">Email</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    className="input-field"
                    required
                  />
                </div>

                <div>
                  <div className="flex justify-between items-center">
                    <label className="label" htmlFor="password">Password</label>
                    <Link to="/forgot-password" className="text-sm text-blue-600 hover:text-blue-700">
                      Forgot password?
                    </Link>
                  </div>
                  <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    className="input-field"
                    required
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full btn-primary py-3 disabled:opacity-50"
                >
                  {loading ? 'Signing in...' : 'Sign In'}
                </button>
              </form>
            </>
          )}

          {!requires2FA && (
            <p className="mt-6 text-center text-slate-600">
              Don't have an account?{' '}
              <Link to="/register" className="text-blue-600 hover:text-blue-700 font-medium">
                Sign up
              </Link>
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
