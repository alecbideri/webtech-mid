package com.alec.FindJobApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Size(max = 500, message = "Bio must not exceed 500 characters")
  @Column(columnDefinition = "TEXT")
  private String bio;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(columnDefinition = "TEXT")
  private String skills;

  @Column(columnDefinition = "TEXT")
  private String experience;

  @Column(columnDefinition = "TEXT")
  private String education;

  @Column(name = "linkedin_url")
  private String linkedinUrl;

  @Column(name = "portfolio_url")
  private String portfolioUrl;

  @Column(name = "profile_picture_path")
  private String profilePicturePath;

  @Column(name = "company_name")
  private String companyName;

  @Column(name = "company_description", columnDefinition = "TEXT")
  private String companyDescription;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
