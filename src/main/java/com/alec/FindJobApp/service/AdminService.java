package com.alec.FindJobApp.service;

import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.JobApplicationRepository;
import com.alec.FindJobApp.repository.JobRepository;
import com.alec.FindJobApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final JobRepository jobRepository;
  private final JobApplicationRepository applicationRepository;

  public List<User> getAllProfiles() {
    return userRepository.findAll();
  }

  public List<User> getProfilesByRole(Role role) {
    return userRepository.findByRole(role);
  }

  public void verifyCreator(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    if (user.getRole() == Role.JOB_CREATOR) {
      user.setVerified(true);
      userRepository.save(user);
    }
  }

  public void deleteUser(Long userId) {
    // Cascade delete should handle jobs/applications if configured in Entities,
    // but let's assume we might need manual cleanup or rely on DB constraints.
    // For now, standard deleteById.
    userRepository.deleteById(userId);
  }

  public void deleteJob(Long jobId) {
    jobRepository.deleteById(jobId);
  }

  public void deleteAllJobsAndAccounts() {
    applicationRepository.deleteAll(); // Delete dependent data first
    jobRepository.deleteAll();
    // Don't delete Sysadmin obviously? Or maybe re-insert default admin?
    // Requirement says "delete all records from jobs and accounts".
    // We should probably keep the current admin or allow re-init.
    // For safety, let's delete all non-admin users.
    List<User> users = userRepository.findAll();
    for (User user : users) {
      if (user.getRole() != Role.SYSADMIN) {
        userRepository.delete(user);
      }
    }
  }

  public Map<String, Object> getDashboardAnalytics() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUsers", userRepository.count());
    stats.put("totalJobs", jobRepository.count());
    stats.put("totalApplications", applicationRepository.count());
    stats.put("jobCreators", userRepository.findByRole(Role.JOB_CREATOR).size());
    stats.put("jobSeekers", userRepository.findByRole(Role.JOB_SEEKER).size());
    return stats;
  }
}
