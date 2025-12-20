import api from './api';

export const profileApi = {
  getProfile: async () => {
    const response = await api.get('/profile');
    return response.data;
  },

  updateProfile: async (data) => {
    const response = await api.put('/profile', data);
    return response.data;
  },

  enable2FA: async () => {
    const response = await api.post('/profile/2fa/enable');
    return response.data;
  },

  disable2FA: async () => {
    const response = await api.post('/profile/2fa/disable');
    return response.data;
  },
};

export const adminApi = {
  getPendingRecruiters: async () => {
    const response = await api.get('/admin/recruiters/pending');
    return response.data;
  },

  approveRecruiter: async (id) => {
    const response = await api.patch(`/admin/recruiters/${id}/approve`);
    return response.data;
  },

  rejectRecruiter: async (id) => {
    const response = await api.patch(`/admin/recruiters/${id}/reject`);
    return response.data;
  },
};

export const authApi = {
  verifyOtp: async (email, otpCode) => {
    const response = await api.post('/auth/verify-otp', { email, otpCode });
    return response.data;
  },
};

export default { profileApi, adminApi, authApi };
