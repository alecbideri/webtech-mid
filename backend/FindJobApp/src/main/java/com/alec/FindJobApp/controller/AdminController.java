package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.UserDTO;
import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import com.alec.FindJobApp.service.EmailService;
import com.alec.FindJobApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for admin endpoints.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final EmailService emailService;

  /**
   * Gets all users with pagination.
   */
  @GetMapping("/users")
  public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(pageable)));
  }

  /**
   * Gets users by role.
   */
  @GetMapping("/users/role/{role}")
  public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsersByRole(
      @PathVariable Role role,
      @PageableDefault(size = 10) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(role, pageable)));
  }

  /**
   * Searches users by query (first name, last name, or email).
   */
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

  /**
   * Gets pending recruiters awaiting approval.
   */
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

  /**
   * Approves a recruiter.
   */
  @PatchMapping("/recruiters/{id}/approve")
  public ResponseEntity<ApiResponse<Void>> approveRecruiter(@PathVariable Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole() != Role.RECRUITER) {
      throw new RuntimeException("User is not a recruiter");
    }

    user.setIsApproved(true);
    userRepository.save(user);

    // Send approval email
    emailService.sendRecruiterApprovalEmail(user.getEmail(), user.getFullName());

    return ResponseEntity.ok(ApiResponse.success("Recruiter approved successfully", null));
  }

  /**
   * Rejects a recruiter.
   */
  @PatchMapping("/recruiters/{id}/reject")
  public ResponseEntity<ApiResponse<Void>> rejectRecruiter(@PathVariable Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole() != Role.RECRUITER) {
      throw new RuntimeException("User is not a recruiter");
    }

    // Send rejection email before deleting
    emailService.sendRecruiterRejectionEmail(user.getEmail(), user.getFullName());

    // Delete the unapproved recruiter account
    userRepository.delete(user);

    return ResponseEntity.ok(ApiResponse.success("Recruiter rejected and account deleted", null));
  }

  /**
   * Activates a user.
   */
  @PatchMapping("/users/{id}/activate")
  public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
    userService.activateUser(id);
    return ResponseEntity.ok(ApiResponse.success("User activated", null));
  }

  /**
   * Deactivates a user.
   */
  @PatchMapping("/users/{id}/deactivate")
  public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
    userService.deactivateUser(id);
    return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
  }

  /**
   * Deletes a user.
   */
  @DeleteMapping("/users/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(ApiResponse.success("User deleted", null));
  }
}
