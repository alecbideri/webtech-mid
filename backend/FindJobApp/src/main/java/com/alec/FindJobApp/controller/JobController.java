package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.JobDTO;
import com.alec.FindJobApp.dto.JobRequest;
import com.alec.FindJobApp.model.JobStatus;
import com.alec.FindJobApp.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;

  @GetMapping
  public ResponseEntity<ApiResponse<Page<JobDTO>>> getAllJobs(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(jobService.getAllJobs(pageable)));
  }

  @GetMapping("/open")
  public ResponseEntity<ApiResponse<Page<JobDTO>>> getOpenJobs(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(jobService.getOpenJobs(pageable)));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<Page<JobDTO>>> searchJobs(
      @RequestParam String keyword,
      @PageableDefault(size = 10) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(jobService.searchJobs(keyword, pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<JobDTO>> getJobById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(jobService.getJobDTOById(id)));
  }

  @PostMapping
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<ApiResponse<JobDTO>> createJob(@Valid @RequestBody JobRequest request) {
    JobDTO job = jobService.createJob(request);
    return ResponseEntity.ok(ApiResponse.success("Job created successfully", job));
  }

  @GetMapping("/my-jobs")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<ApiResponse<Page<JobDTO>>> getMyJobs(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(jobService.getMyJobs(pageable)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<ApiResponse<JobDTO>> updateJob(
      @PathVariable Long id,
      @Valid @RequestBody JobRequest request) {
    JobDTO job = jobService.updateJob(id, request);
    return ResponseEntity.ok(ApiResponse.success("Job updated successfully", job));
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<ApiResponse<JobDTO>> updateJobStatus(
      @PathVariable Long id,
      @RequestParam JobStatus status) {
    JobDTO job = jobService.updateJobStatus(id, status);
    return ResponseEntity.ok(ApiResponse.success("Job status updated", job));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
    jobService.deleteJob(id);
    return ResponseEntity.ok(ApiResponse.success("Job deleted successfully", null));
  }
}
