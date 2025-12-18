package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.UserDTO;
import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for admin endpoints.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;

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
