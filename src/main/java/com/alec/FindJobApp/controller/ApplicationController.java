package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.model.ApplicationStatus;
import com.alec.FindJobApp.model.JobApplication;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

  private final ApplicationService applicationService;

  @PostMapping("/{jobId}")
  @PreAuthorize("hasRole('JOB_SEEKER')")
  public ResponseEntity<JobApplication> applyForJob(@PathVariable Long jobId,
      @AuthenticationPrincipal UserDetails userDetails) {
    User seeker = (User) userDetails;
    return ResponseEntity.ok(applicationService.applyForJob(jobId, seeker));
  }

  @GetMapping("/my-applications")
  @PreAuthorize("hasRole('JOB_SEEKER')")
  public ResponseEntity<List<JobApplication>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
    User seeker = (User) userDetails;
    return ResponseEntity.ok(applicationService.getApplicationsForSeeker(seeker));
  }

  @GetMapping("/job/{jobId}")
  @PreAuthorize("hasRole('JOB_CREATOR')")
  public ResponseEntity<List<JobApplication>> getApplicationsForJob(@PathVariable Long jobId,
      @AuthenticationPrincipal UserDetails userDetails) {
    User creator = (User) userDetails;
    return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId, creator));
  }

  @PutMapping("/{applicationId}/status")
  @PreAuthorize("hasRole('JOB_CREATOR')")
  public ResponseEntity<JobApplication> updateStatus(
      @PathVariable Long applicationId,
      @RequestBody Map<String, String> statusMap,
      @AuthenticationPrincipal UserDetails userDetails) {

    User creator = (User) userDetails;
    String statusStr = statusMap.get("status");
    ApplicationStatus status = ApplicationStatus.valueOf(statusStr.toUpperCase());

    return ResponseEntity.ok(applicationService.updateApplicationStatus(applicationId, status, creator));
  }
}
