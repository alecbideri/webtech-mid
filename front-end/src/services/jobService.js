import api from './api';

/**
 * Job service for job-related API calls
 */
const jobService = {
  /**
   * Get all jobs with pagination
   */
  async getAllJobs(page = 0, size = 10) {
    const response = await api.get(`/jobs?page=${page}&size=${size}`);
    return response.data;
  },

  /**
   * Get open jobs only
   */
  async getOpenJobs(page = 0, size = 10) {
    const response = await api.get(`/jobs/open?page=${page}&size=${size}`);
    return response.data;
  },

  /**
   * Search jobs by keyword
   */
  async searchJobs(keyword, page = 0, size = 10) {
    const response = await api.get(`/jobs/search?keyword=${keyword}&page=${page}&size=${size}`);
    return response.data;
  },

  /**
   * Get job by ID
   */
  async getJobById(id) {
    const response = await api.get(`/jobs/${id}`);
    return response.data;
  },

  /**
   * Create a new job (Recruiter only)
   */
  async createJob(jobData) {
    const response = await api.post('/jobs', jobData);
    return response.data;
  },

  /**
   * Get my jobs (Recruiter only)
   */
  async getMyJobs(page = 0, size = 10) {
    const response = await api.get(`/jobs/my-jobs?page=${page}&size=${size}`);
    return response.data;
  },

  /**
   * Update a job
   */
  async updateJob(id, jobData) {
    const response = await api.put(`/jobs/${id}`, jobData);
    return response.data;
  },

  /**
   * Update job status
   */
  async updateJobStatus(id, status) {
    const response = await api.patch(`/jobs/${id}/status?status=${status}`);
    return response.data;
  },

  /**
   * Delete a job
   */
  async deleteJob(id) {
    const response = await api.delete(`/jobs/${id}`);
    return response.data;
  },
};

export default jobService;
