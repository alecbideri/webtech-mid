package com.alec.FindJobApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User entity representing all users in the system.
 * Supports three roles: ADMIN, RECRUITER, and SEEKER.
 * Supports both local and OAuth (Google) authentication.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Column(nullable = false, unique = true)
  private String email;

  @Size(min = 6, message = "Password must be at least 6 characters")
  @Column(nullable = true)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;

  // Profile fields
  @Column(name = "phone")
  private String phone;

  @Column(name = "bio", columnDefinition = "TEXT")
  private String bio;

  @Column(name = "company")
  private String company;

  @Column(name = "location")
  private String location;

  @Column(name = "linkedin_url")
  private String linkedinUrl;

  // Approval system (only applies to recruiters)
  @Column(name = "is_approved")
  @Builder.Default
  private Boolean isApproved = true;

  // OAuth fields
  @Column(name = "provider")
  @Builder.Default
  private String provider = "local";

  @Column(name = "provider_id")
  private String providerId;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  // 2FA fields (email OTP)
  @Column(name = "two_factor_enabled")
  @Builder.Default
  private Boolean twoFactorEnabled = false;

  @Column(name = "otp_code")
  private String otpCode;

  @Column(name = "otp_expiry")
  private LocalDateTime otpExpiry;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /**
   * Returns the full name of the user.
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * Checks if user authenticated via OAuth.
   */
  public boolean isOAuthUser() {
    return provider != null && !provider.equals("local");
  }

  /**
   * Checks if user requires approval (only recruiters).
   */
  public boolean requiresApproval() {
    return role == Role.RECRUITER && (isApproved == null || !isApproved);
  }
}
