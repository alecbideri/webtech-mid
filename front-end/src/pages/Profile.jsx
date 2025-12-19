import { useState, useEffect } from 'react';
import { profileApi } from '../services/profileApi';
import { useAuth } from '../context/AuthContext';
import './Profile.css';

function Profile() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phone: '',
    bio: '',
    company: '',
    location: '',
    linkedinUrl: '',
  });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await profileApi.getProfile();
      if (response.success) {
        setProfile(response.data);
        setFormData({
          firstName: response.data.firstName || '',
          lastName: response.data.lastName || '',
          phone: response.data.phone || '',
          bio: response.data.bio || '',
          company: response.data.company || '',
          location: response.data.location || '',
          linkedinUrl: response.data.linkedinUrl || '',
        });
      }
    } catch (err) {
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      const response = await profileApi.updateProfile(formData);
      if (response.success) {
        setSuccess('Profile updated successfully!');
        setProfile(response.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const handleToggle2FA = async () => {
    try {
      if (profile?.twoFactorEnabled) {
        const response = await profileApi.disable2FA();
        if (response.success) {
          setProfile((prev) => ({ ...prev, twoFactorEnabled: false }));
          setSuccess('Two-factor authentication disabled');
        }
      } else {
        const response = await profileApi.enable2FA();
        if (response.success) {
          setProfile((prev) => ({ ...prev, twoFactorEnabled: true }));
          setSuccess('Two-factor authentication enabled');
        }
      }
    } catch (err) {
      setError('Failed to update 2FA settings');
    }
  };

  if (loading) {
    return (
      <div className="profile-page">
        <div className="loading-spinner">Loading...</div>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <div className="profile-container">
        <div className="profile-header">
          <div className="profile-avatar">
            {profile?.profileImageUrl ? (
              <img src={profile.profileImageUrl} alt="Profile" />
            ) : (
              <div className="avatar-placeholder">
                {profile?.firstName?.[0]}{profile?.lastName?.[0]}
              </div>
            )}
          </div>
          <div className="profile-info">
            <h1>{profile?.firstName} {profile?.lastName}</h1>
            <p className="profile-email">{profile?.email}</p>
            <span className={`role-badge ${profile?.role?.toLowerCase()}`}>
              {profile?.role}
            </span>
          </div>
        </div>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit} className="profile-form">
          <h2>Edit Profile</h2>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="firstName">First Name</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                required
                minLength={2}
              />
            </div>
            <div className="form-group">
              <label htmlFor="lastName">Last Name</label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                required
                minLength={2}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="phone">Phone</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="+1 (555) 123-4567"
              />
            </div>
            <div className="form-group">
              <label htmlFor="location">Location</label>
              <input
                type="text"
                id="location"
                name="location"
                value={formData.location}
                onChange={handleChange}
                placeholder="City, Country"
              />
            </div>
          </div>

          {user?.role === 'RECRUITER' && (
            <div className="form-group">
              <label htmlFor="company">Company</label>
              <input
                type="text"
                id="company"
                name="company"
                value={formData.company}
                onChange={handleChange}
                placeholder="Your company name"
              />
            </div>
          )}

          <div className="form-group">
            <label htmlFor="linkedinUrl">LinkedIn URL</label>
            <input
              type="url"
              id="linkedinUrl"
              name="linkedinUrl"
              value={formData.linkedinUrl}
              onChange={handleChange}
              placeholder="https://linkedin.com/in/yourprofile"
            />
          </div>

          <div className="form-group">
            <label htmlFor="bio">Bio</label>
            <textarea
              id="bio"
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              rows={4}
              placeholder="Tell us about yourself..."
              maxLength={1000}
            />
            <span className="char-count">{formData.bio.length}/1000</span>
          </div>

          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </form>

        <div className="security-section">
          <h2>Security Settings</h2>

          <div className="security-option">
            <div className="security-info">
              <h3>Two-Factor Authentication</h3>
              <p>Add an extra layer of security. You'll receive a verification code via email when signing in.</p>
            </div>
            <button
              type="button"
              className={`btn ${profile?.twoFactorEnabled ? 'btn-danger' : 'btn-secondary'}`}
              onClick={handleToggle2FA}
            >
              {profile?.twoFactorEnabled ? 'Disable 2FA' : 'Enable 2FA'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
