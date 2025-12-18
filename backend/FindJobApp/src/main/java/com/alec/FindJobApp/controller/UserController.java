package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.UserDTO;
import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user endpoints.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * Gets the current user's data.
   */
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
    return ResponseEntity.ok(ApiResponse.success(userService.toDTO(userService.getCurrentUser())));
  }

  /**
   * Gets a user by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(userService.toDTO(userService.getUserById(id))));
  }

  /**
   * Updates the current user's name.
   */
  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUser(
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName) {
    UserDTO updated = userService.updateUser(userService.getCurrentUser().getId(), firstName, lastName);
    return ResponseEntity.ok(ApiResponse.success("User updated successfully", updated));
  }
}
