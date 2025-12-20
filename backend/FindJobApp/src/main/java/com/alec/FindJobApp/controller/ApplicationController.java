package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.ApplicationDTO;
import com.alec.FindJobApp.model.ApplicationStatus;
import com.alec.FindJobApp.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

  private final ApplicationService applicationService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('SEEKER')")
  public ResponseEntity<ApiResponse<ApplicationDTO>> applyForJob(
      @RequestParam Long jobId,
      @RequestParam(required = false) String coverLetter,
      @RequestParam(required = false) MultipartFile resume) {
    ApplicationDTO application = applicationService.applyForJob(jobId, coverLetter, resume);
    return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", application));
  }

  @GetMapping("/my-applications")
  @PreAuthorize("hasRole('SEEKER')")
  public ResponseEntity<ApiResponse<Page<ApplicationDTO>>> getMyApplications(
      @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(applicationService.getMyApplications(pageable)));
  }

  @GetMapping("/job/{jobId}")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<ApiResponse<Page<ApplicationDTO>>> getApplicationsForJob(
      @PathVariable Long jobId,
      @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(applicationService.getApplicationsForJob(jobId, pageable)));
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<ApiResponse<ApplicationDTO>> updateApplicationStatus(
      @PathVariable Long id,
      @RequestParam ApplicationStatus status,
      @RequestParam(required = false) String reviewerNotes) {
    ApplicationDTO application = applicationService.updateApplicationStatus(id, status, reviewerNotes);
    return ResponseEntity.ok(ApiResponse.success("Application status updated", application));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SEEKER')")
  public ResponseEntity<ApiResponse<Void>> withdrawApplication(@PathVariable Long id) {
    applicationService.withdrawApplication(id);
    return ResponseEntity.ok(ApiResponse.success("Application withdrawn", null));
  }
}
