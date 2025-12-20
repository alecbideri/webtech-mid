import api from './api';

const applicationService = {
  async applyForJob(jobId, coverLetter, resume) {
    const formData = new FormData();
    formData.append('jobId', jobId);
    if (coverLetter) {
      formData.append('coverLetter', coverLetter);
    }
    if (resume) {
      formData.append('resume', resume);
    }
    const response = await api.post('/applications', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  async getMyApplications(page = 0, size = 10) {
    const response = await api.get(`/applications/my-applications?page=${page}&size=${size}`);
    return response.data;
  },

  async getApplicationsForJob(jobId, page = 0, size = 10) {
    const response = await api.get(`/applications/job/${jobId}?page=${page}&size=${size}`);
    return response.data;
  },

  async updateApplicationStatus(id, status, reviewerNotes = '') {
    const response = await api.patch(
      `/applications/${id}/status?status=${status}&reviewerNotes=${reviewerNotes}`
    );
    return response.data;
  },

  async withdrawApplication(id) {
    const response = await api.delete(`/applications/${id}`);
    return response.data;
  },
};

export default applicationService;
