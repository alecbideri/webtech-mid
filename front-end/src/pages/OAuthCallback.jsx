import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Spinner from '../components/common/Spinner';

/**
 * OAuth Callback page - handles redirect from OAuth provider
 */
export default function OAuthCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { setUser } = useAuth();

  useEffect(() => {
    const token = searchParams.get('token');
    const id = searchParams.get('id');
    const email = searchParams.get('email');
    const firstName = searchParams.get('firstName');
    const lastName = searchParams.get('lastName');
    const role = searchParams.get('role');

    if (token && id && email) {
      // Store auth data
      localStorage.setItem('token', token);
      const user = {
        id: parseInt(id),
        email,
        firstName,
        lastName,
        role,
      };
      localStorage.setItem('user', JSON.stringify(user));

      // Update auth context
      if (setUser) {
        setUser(user);
      }

      // Redirect based on role
      setTimeout(() => {
        if (role === 'ADMIN') {
          navigate('/admin/dashboard');
        } else if (role === 'RECRUITER') {
          navigate('/recruiter/dashboard');
        } else {
          navigate('/seeker/dashboard');
        }
      }, 500);
    } else {
      // No token received, redirect to login with error
      navigate('/login?error=oauth_failed');
    }
  }, [searchParams, navigate, setUser]);

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center">
      <div className="text-center">
        <Spinner size="lg" />
        <p className="mt-4 text-slate-600">Completing sign in...</p>
      </div>
    </div>
  );
}
