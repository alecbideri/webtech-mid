package com.alec.FindJobApp.service;

import com.alec.FindJobApp.model.ApplicationStatus;
import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.JobApplication;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.JobApplicationRepository;
import com.alec.FindJobApp.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

  private final JobApplicationRepository applicationRepository;
  private final JobRepository jobRepository;
  private final EmailService emailService;

  public JobApplication applyForJob(Long jobId, User seeker) {
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

    if (applicationRepository.findByJobAndSeeker(job, seeker).isPresent()) {
      throw new RuntimeException("Already applied to this job");
    }

    JobApplication application = JobApplication.builder()
        .job(job)
        .seeker(seeker)
        .status(ApplicationStatus.APPLIED) // Default
        .build();

    return applicationRepository.save(application);
  }

  public List<JobApplication> getApplicationsForSeeker(User seeker) {
    return applicationRepository.findBySeeker(seeker);
  }

  public List<JobApplication> getApplicationsForJob(Long jobId, User creator) {
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

    // Ensure requestor is the owner of the job
    if (!job.getCreator().getId().equals(creator.getId())) {
      throw new RuntimeException("Unauthorized to view applications for this job");
    }

    return applicationRepository.findByJob(job);
  }

  public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status, User creator) {
    JobApplication application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new RuntimeException("Application not found"));

    // Ensure requestor is the owner of the job
    if (!application.getJob().getCreator().getId().equals(creator.getId())) {
      throw new RuntimeException("Unauthorized to update this application");
    }

    application.setStatus(status);
    JobApplication saved = applicationRepository.save(application);

    // Send email
    if (status == ApplicationStatus.CONFIRMED || status == ApplicationStatus.SCHEDULE_INTERVIEW
        || status == ApplicationStatus.DENIED) {
      emailService.sendApplicationStatusEmail(
          application.getSeeker().getEmail(),
          status.name(),
          application.getJob().getTitle());
    }

    return saved;
  }
}
