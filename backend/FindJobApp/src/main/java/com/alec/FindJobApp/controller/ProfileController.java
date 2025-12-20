package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.ProfileResponse;
import com.alec.FindJobApp.dto.ProfileUpdateRequest;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import com.alec.FindJobApp.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final UserRepository userRepository;
  private final OtpService otpService;

  @GetMapping
  public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    ProfileResponse profile = mapToProfileResponse(user);
    return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
  }

  @PutMapping
  public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
      @AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody ProfileUpdateRequest request) {
    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }
    if (request.getBio() != null) {
      user.setBio(request.getBio());
    }
    if (request.getCompany() != null) {
      user.setCompany(request.getCompany());
    }
    if (request.getLocation() != null) {
      user.setLocation(request.getLocation());
    }
    if (request.getLinkedinUrl() != null) {
      user.setLinkedinUrl(request.getLinkedinUrl());
    }

    user = userRepository.save(user);
    ProfileResponse profile = mapToProfileResponse(user);
    return ResponseEntity.ok(ApiResponse.success("Profile updated", profile));
  }

  @PostMapping("/2fa/enable")
  public ResponseEntity<ApiResponse<Void>> enable2FA(
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setTwoFactorEnabled(true);
    userRepository.save(user);

    return ResponseEntity.ok(ApiResponse.success("Two-factor authentication enabled", null));
  }

  @PostMapping("/2fa/disable")
  public ResponseEntity<ApiResponse<Void>> disable2FA(
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setTwoFactorEnabled(false);
    user.setOtpCode(null);
    user.setOtpExpiry(null);
    userRepository.save(user);

    return ResponseEntity.ok(ApiResponse.success("Two-factor authentication disabled", null));
  }

  private ProfileResponse mapToProfileResponse(User user) {
    return ProfileResponse.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .role(user.getRole())
        .phone(user.getPhone())
        .bio(user.getBio())
        .company(user.getCompany())
        .location(user.getLocation())
        .linkedinUrl(user.getLinkedinUrl())
        .profileImageUrl(user.getProfileImageUrl())
        .twoFactorEnabled(user.getTwoFactorEnabled())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
