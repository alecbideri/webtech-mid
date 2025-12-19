package com.alec.FindJobApp.dto;

import com.alec.FindJobApp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning user profile data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Role role;
  private String phone;
  private String bio;
  private String company;
  private String location;
  private String linkedinUrl;
  private String profileImageUrl;
  private Boolean twoFactorEnabled;
  private LocalDateTime createdAt;
}
