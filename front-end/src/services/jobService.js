import api from './api';

const jobService = {
  async getAllJobs(page = 0, size = 10) {
    const response = await api.get(`/jobs?page=${page}&size=${size}`);
    return response.data;
  },

  async getOpenJobs(page = 0, size = 10) {
    const response = await api.get(`/jobs/open?page=${page}&size=${size}`);
    return response.data;
  },

  async searchJobs(keyword, page = 0, size = 10) {
    const response = await api.get(`/jobs/search?keyword=${keyword}&page=${page}&size=${size}`);
    return response.data;
  },

  async getJobById(id) {
    const response = await api.get(`/jobs/${id}`);
    return response.data;
  },

  async createJob(jobData) {
    const response = await api.post('/jobs', jobData);
    return response.data;
  },

  async getMyJobs(page = 0, size = 10) {
    const response = await api.get(`/jobs/my-jobs?page=${page}&size=${size}`);
    return response.data;
  },

  async updateJob(id, jobData) {
    const response = await api.put(`/jobs/${id}`, jobData);
    return response.data;
  },

  async updateJobStatus(id, status) {
    const response = await api.patch(`/jobs/${id}/status?status=${status}`);
    return response.data;
  },

  async deleteJob(id) {
    const response = await api.delete(`/jobs/${id}`);
    return response.data;
  },
};

export default jobService;
