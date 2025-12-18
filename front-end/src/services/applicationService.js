import api from './api';

/**
 * Application service for job application operations
 */
const applicationService = {
  /**
   * Apply for a job (Seeker only)
   */
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

  /**
   * Get my applications (Seeker)
   */
  async getMyApplications(page = 0, size = 10) {
    const response = await api.get(`/applications/my-applications?page=${page}&size=${size}`);
    return response.data;
  },

  /**
   * Get applications for a job (Recruiter)
   */
  async getApplicationsForJob(jobId, page = 0, size = 10) {
    const response = await api.get(`/applications/job/${jobId}?page=${page}&size=${size}`);
    return response.data;
  },

  /**
   * Update application status (Recruiter)
   */
  async updateApplicationStatus(id, status, reviewerNotes = '') {
    const response = await api.patch(
      `/applications/${id}/status?status=${status}&reviewerNotes=${reviewerNotes}`
    );
    return response.data;
  },

  /**
   * Withdraw application (Seeker)
   */
  async withdrawApplication(id) {
    const response = await api.delete(`/applications/${id}`);
    return response.data;
  },
};

export default applicationService;
