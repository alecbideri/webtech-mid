package com.alec.FindJobApp.service;

import com.alec.FindJobApp.dto.ApplicationDTO;
import com.alec.FindJobApp.exception.BadRequestException;
import com.alec.FindJobApp.exception.ResourceNotFoundException;
import com.alec.FindJobApp.model.*;
import com.alec.FindJobApp.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Service for job application operations.
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final JobService jobService;
  private final UserService userService;
  private final FileStorageService fileStorageService;
  private final EmailService emailService;

  /**
   * Converts Application entity to ApplicationDTO.
   */
  public ApplicationDTO toDTO(Application application) {
    return ApplicationDTO.builder()
        .id(application.getId())
        .jobId(application.getJob().getId())
        .jobTitle(application.getJob().getTitle())
        .company(application.getJob().getCompany())
        .seekerId(application.getSeeker().getId())
        .seekerName(application.getSeeker().getFullName())
        .seekerEmail(application.getSeeker().getEmail())
        .status(application.getStatus())
        .coverLetter(application.getCoverLetter())
        .resumeFilename(application.getResumeFilename())
        .appliedAt(application.getAppliedAt())
        .reviewedAt(application.getReviewedAt())
        .reviewerNotes(application.getReviewerNotes())
        .build();
  }

  /**
   * Applies for a job.
   */
  @Transactional
  public ApplicationDTO applyForJob(Long jobId, String coverLetter, MultipartFile resume) {
    User seeker = userService.getCurrentUser();

    if (seeker.getRole() != Role.SEEKER) {
      throw new BadRequestException("Only job seekers can apply for jobs");
    }

    Job job = jobService.getJobById(jobId);

    if (job.getStatus() != JobStatus.OPEN) {
      throw new BadRequestException("This job is no longer accepting applications");
    }

    // Check if already applied
    if (applicationRepository.existsByJobAndSeeker(job, seeker)) {
      throw new BadRequestException("You have already applied for this job");
    }

    // Store resume if provided
    String resumePath = null;
    String resumeFilename = null;
    if (resume != null && !resume.isEmpty()) {
      resumePath = fileStorageService.storeFile(resume, "resumes");
      resumeFilename = resume.getOriginalFilename();
    }

    Application application = Application.builder()
        .job(job)
        .seeker(seeker)
        .status(ApplicationStatus.PENDING)
        .coverLetter(coverLetter)
        .resumePath(resumePath)
        .resumeFilename(resumeFilename)
        .build();

    return toDTO(applicationRepository.save(application));
  }

  /**
   * Gets an application by ID.
   */
  public Application getApplicationById(Long id) {
    return applicationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
  }

  /**
   * Gets applications for the current seeker.
   */
  public Page<ApplicationDTO> getMyApplications(Pageable pageable) {
    User seeker = userService.getCurrentUser();
    return applicationRepository.findBySeeker(seeker, pageable).map(this::toDTO);
  }

  /**
   * Gets applications for a specific job (recruiter only).
   */
  public Page<ApplicationDTO> getApplicationsForJob(Long jobId, Pageable pageable) {
    Job job = jobService.getJobById(jobId);
    User currentUser = userService.getCurrentUser();

    // Verify ownership
    if (!job.getRecruiter().getId().equals(currentUser.getId())
        && currentUser.getRole() != Role.ADMIN) {
      throw new BadRequestException("You can only view applications for your own jobs");
    }

    return applicationRepository.findByJob(job, pageable).map(this::toDTO);
  }

  /**
   * Updates application status (recruiter only).
   */
  @Transactional
  public ApplicationDTO updateApplicationStatus(Long id, ApplicationStatus status, String reviewerNotes) {
    Application application = getApplicationById(id);
    User currentUser = userService.getCurrentUser();

    // Verify ownership
    if (!application.getJob().getRecruiter().getId().equals(currentUser.getId())
        && currentUser.getRole() != Role.ADMIN) {
      throw new BadRequestException("You can only update applications for your own jobs");
    }

    application.setStatus(status);
    application.setReviewedAt(LocalDateTime.now());
    if (reviewerNotes != null) {
      application.setReviewerNotes(reviewerNotes);
    }

    Application saved = applicationRepository.save(application);

    // Send email notification
    emailService.sendApplicationStatusEmail(
        application.getSeeker().getEmail(),
        application.getSeeker().getFullName(),
        application.getJob().getTitle(),
        application.getJob().getCompany(),
        status.name());

    return toDTO(saved);
  }

  /**
   * Withdraws an application (seeker only).
   */
  @Transactional
  public void withdrawApplication(Long id) {
    Application application = getApplicationById(id);
    User currentUser = userService.getCurrentUser();

    if (!application.getSeeker().getId().equals(currentUser.getId())) {
      throw new BadRequestException("You can only withdraw your own applications");
    }

    // Delete resume file if exists
    if (application.getResumePath() != null) {
      fileStorageService.deleteFile(application.getResumePath());
    }

    applicationRepository.delete(application);
  }
}
