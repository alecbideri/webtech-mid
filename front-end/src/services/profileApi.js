import api from './api';

// Profile API calls
export const profileApi = {
  // Get current user's profile
  getProfile: async () => {
    const response = await api.get('/profile');
    return response.data;
  },

  // Update profile
  updateProfile: async (data) => {
    const response = await api.put('/profile', data);
    return response.data;
  },

  // Enable 2FA
  enable2FA: async () => {
    const response = await api.post('/profile/2fa/enable');
    return response.data;
  },

  // Disable 2FA
  disable2FA: async () => {
    const response = await api.post('/profile/2fa/disable');
    return response.data;
  },
};

// Admin API calls for recruiter approval
export const adminApi = {
  // Get pending recruiters
  getPendingRecruiters: async () => {
    const response = await api.get('/admin/recruiters/pending');
    return response.data;
  },

  // Approve a recruiter
  approveRecruiter: async (id) => {
    const response = await api.patch(`/admin/recruiters/${id}/approve`);
    return response.data;
  },

  // Reject a recruiter
  rejectRecruiter: async (id) => {
    const response = await api.patch(`/admin/recruiters/${id}/reject`);
    return response.data;
  },
};

// Auth API extensions for 2FA
export const authApi = {
  // Verify OTP for 2FA login
  verifyOtp: async (email, otpCode) => {
    const response = await api.post('/auth/verify-otp', { email, otpCode });
    return response.data;
  },
};

export default { profileApi, adminApi, authApi };
