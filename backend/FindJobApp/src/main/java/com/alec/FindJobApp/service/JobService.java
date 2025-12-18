package com.alec.FindJobApp.service;

import com.alec.FindJobApp.dto.JobDTO;
import com.alec.FindJobApp.dto.JobRequest;
import com.alec.FindJobApp.exception.BadRequestException;
import com.alec.FindJobApp.exception.ResourceNotFoundException;
import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.JobStatus;
import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for job operations.
 */
@Service
@RequiredArgsConstructor
public class JobService {

  private final JobRepository jobRepository;
  private final UserService userService;

  /**
   * Converts Job entity to JobDTO.
   */
  public JobDTO toDTO(Job job) {
    return JobDTO.builder()
        .id(job.getId())
        .title(job.getTitle())
        .description(job.getDescription())
        .company(job.getCompany())
        .location(job.getLocation())
        .salary(job.getSalary())
        .jobType(job.getJobType())
        .status(job.getStatus())
        .requirements(job.getRequirements())
        .benefits(job.getBenefits())
        .recruiterId(job.getRecruiter().getId())
        .recruiterName(job.getRecruiter().getFullName())
        .applicationCount(job.getApplicationCount())
        .createdAt(job.getCreatedAt())
        .updatedAt(job.getUpdatedAt())
        .build();
  }

  /**
   * Creates a new job posting.
   */
  @Transactional
  public JobDTO createJob(JobRequest request) {
    User recruiter = userService.getCurrentUser();

    if (recruiter.getRole() != Role.RECRUITER) {
      throw new BadRequestException("Only recruiters can post jobs");
    }

    Job job = Job.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .company(request.getCompany())
        .location(request.getLocation())
        .salary(request.getSalary())
        .jobType(request.getJobType())
        .status(JobStatus.OPEN)
        .requirements(request.getRequirements())
        .benefits(request.getBenefits())
        .recruiter(recruiter)
        .build();

    return toDTO(jobRepository.save(job));
  }

  /**
   * Gets a job by ID.
   */
  public Job getJobById(Long id) {
    return jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
  }

  /**
   * Gets a job DTO by ID.
   */
  public JobDTO getJobDTOById(Long id) {
    return toDTO(getJobById(id));
  }

  /**
   * Gets all jobs with pagination.
   */
  public Page<JobDTO> getAllJobs(Pageable pageable) {
    return jobRepository.findAll(pageable).map(this::toDTO);
  }

  /**
   * Gets all open jobs.
   */
  public Page<JobDTO> getOpenJobs(Pageable pageable) {
    return jobRepository.findByStatusOrderByCreatedAtDesc(JobStatus.OPEN, pageable)
        .map(this::toDTO);
  }

  /**
   * Searches jobs by keyword.
   */
  public Page<JobDTO> searchJobs(String keyword, Pageable pageable) {
    return jobRepository.searchJobs(keyword, pageable).map(this::toDTO);
  }

  /**
   * Gets jobs posted by the current recruiter.
   */
  public Page<JobDTO> getMyJobs(Pageable pageable) {
    User recruiter = userService.getCurrentUser();
    return jobRepository.findByRecruiter(recruiter, pageable).map(this::toDTO);
  }

  /**
   * Updates a job posting.
   */
  @Transactional
  public JobDTO updateJob(Long id, JobRequest request) {
    Job job = getJobById(id);
    User currentUser = userService.getCurrentUser();

    // Verify ownership
    if (!job.getRecruiter().getId().equals(currentUser.getId())
        && currentUser.getRole() != Role.ADMIN) {
      throw new BadRequestException("You can only update your own jobs");
    }

    job.setTitle(request.getTitle());
    job.setDescription(request.getDescription());
    job.setCompany(request.getCompany());
    job.setLocation(request.getLocation());
    job.setSalary(request.getSalary());
    job.setJobType(request.getJobType());
    if (request.getStatus() != null) {
      job.setStatus(request.getStatus());
    }
    job.setRequirements(request.getRequirements());
    job.setBenefits(request.getBenefits());

    return toDTO(jobRepository.save(job));
  }

  /**
   * Updates job status.
   */
  @Transactional
  public JobDTO updateJobStatus(Long id, JobStatus status) {
    Job job = getJobById(id);
    User currentUser = userService.getCurrentUser();

    // Verify ownership
    if (!job.getRecruiter().getId().equals(currentUser.getId())
        && currentUser.getRole() != Role.ADMIN) {
      throw new BadRequestException("You can only update your own jobs");
    }

    job.setStatus(status);
    return toDTO(jobRepository.save(job));
  }

  /**
   * Deletes a job posting.
   */
  @Transactional
  public void deleteJob(Long id) {
    Job job = getJobById(id);
    User currentUser = userService.getCurrentUser();

    // Verify ownership or admin
    if (!job.getRecruiter().getId().equals(currentUser.getId())
        && currentUser.getRole() != Role.ADMIN) {
      throw new BadRequestException("You can only delete your own jobs");
    }

    jobRepository.delete(job);
  }
}
