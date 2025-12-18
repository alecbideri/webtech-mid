package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.ProfileDTO;
import com.alec.FindJobApp.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for profile endpoints.
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  /**
   * Gets the current user's profile.
   */
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<ProfileDTO>> getMyProfile() {
    return ResponseEntity.ok(ApiResponse.success(profileService.getMyProfile()));
  }

  /**
   * Gets a user's profile by ID.
   */
  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<ProfileDTO>> getProfileByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(ApiResponse.success(profileService.getProfileByUserId(userId)));
  }

  /**
   * Updates the current user's profile.
   */
  @PutMapping("/me")
  public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(@RequestBody ProfileDTO profileDTO) {
    ProfileDTO updated = profileService.updateProfile(profileDTO);
    return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
  }
}
