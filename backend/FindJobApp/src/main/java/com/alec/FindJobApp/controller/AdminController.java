package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.ApplicationDTO;
import com.alec.FindJobApp.dto.JobDTO;
import com.alec.FindJobApp.dto.UserDTO;
import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.Application;
import com.alec.FindJobApp.repository.ApplicationRepository;
import com.alec.FindJobApp.repository.JobRepository;
import com.alec.FindJobApp.repository.UserRepository;
import com.alec.FindJobApp.service.EmailService;
import com.alec.FindJobApp.service.UserService;
import com.alec.FindJobApp.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;
  private final ApplicationRepository applicationRepository;
  private final EmailService emailService;
  private final JobService jobService;

  @GetMapping("/users")
  public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(pageable)));
  }

  @GetMapping("/users/role/{role}")
  public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsersByRole(
      @PathVariable Role role,
      @PageableDefault(size = 10) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(role, pageable)));
  }

  @GetMapping("/users/search")
  public ResponseEntity<ApiResponse<Page<UserDTO>>> searchUsers(
      @RequestParam String q,
      @RequestParam(required = false) Role role,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<UserDTO> users;
    if (role != null) {
      users = userService.searchUsersByRole(q, role, pageable);
    } else {
      users = userService.searchUsers(q, pageable);
    }
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  @GetMapping("/recruiters/pending")
  public ResponseEntity<ApiResponse<List<UserDTO>>> getPendingRecruiters() {
    List<User> pendingRecruiters = userRepository.findByRoleAndIsApprovedFalse(Role.RECRUITER);
    List<UserDTO> dtos = pendingRecruiters.stream()
        .map(user -> UserDTO.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .role(user.getRole())
            .isActive(user.getIsActive())
            .company(user.getCompany())
            .createdAt(user.getCreatedAt())
            .build())
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(dtos));
  }

  @PatchMapping("/recruiters/{id}/approve")
  public ResponseEntity<ApiResponse<Void>> approveRecruiter(@PathVariable Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole() != Role.RECRUITER) {
      throw new RuntimeException("User is not a recruiter");
    }

    user.setIsApproved(true);
    userRepository.save(user);

    emailService.sendRecruiterApprovalEmail(user.getEmail(), user.getFullName());

    return ResponseEntity.ok(ApiResponse.success("Recruiter approved successfully", null));
  }

  @PatchMapping("/recruiters/{id}/reject")
  public ResponseEntity<ApiResponse<Void>> rejectRecruiter(@PathVariable Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole() != Role.RECRUITER) {
      throw new RuntimeException("User is not a recruiter");
    }

    emailService.sendRecruiterRejectionEmail(user.getEmail(), user.getFullName());

    userRepository.delete(user);

    return ResponseEntity.ok(ApiResponse.success("Recruiter rejected and account deleted", null));
  }

  @PatchMapping("/users/{id}/activate")
  public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
    userService.activateUser(id);
    return ResponseEntity.ok(ApiResponse.success("User activated", null));
  }

  @PatchMapping("/users/{id}/deactivate")
  public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
    userService.deactivateUser(id);
    return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(ApiResponse.success("User deleted", null));
  }

  @GetMapping("/stats")
  public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboardStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("totalJobs", jobRepository.count());
    stats.put("totalApplications", applicationRepository.count());
    stats.put("totalRecruiters", userRepository.countByRole(Role.RECRUITER));
    stats.put("totalSeekers", userRepository.countByRole(Role.SEEKER));
    return ResponseEntity.ok(ApiResponse.success(stats));
  }

  @GetMapping("/recent-jobs")
  public ResponseEntity<ApiResponse<List<JobDTO>>> getRecentJobs() {
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Job> jobs = jobRepository.findAll(pageable);
    List<JobDTO> jobDTOs = jobs.getContent().stream()
        .map(jobService::toDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(jobDTOs));
  }

  @GetMapping("/recent-applications")
  public ResponseEntity<ApiResponse<List<ApplicationDTO>>> getRecentApplications() {
    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "appliedAt"));
    Page<Application> applications = applicationRepository.findAll(pageable);
    List<ApplicationDTO> applicationDTOs = applications.getContent().stream()
        .map(app -> ApplicationDTO.builder()
            .id(app.getId())
            .jobId(app.getJob().getId())
            .jobTitle(app.getJob().getTitle())
            .company(app.getJob().getCompany())
            .seekerId(app.getSeeker().getId())
            .seekerName(app.getSeeker().getFullName())
            .seekerEmail(app.getSeeker().getEmail())
            .status(app.getStatus())
            .appliedAt(app.getAppliedAt())
            .build())
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(applicationDTOs));
  }
}
