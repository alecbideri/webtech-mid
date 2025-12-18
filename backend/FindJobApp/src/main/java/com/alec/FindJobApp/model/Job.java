package com.alec.FindJobApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Job entity representing job postings created by recruiters.
 */
@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Job title is required")
  @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
  @Column(nullable = false)
  private String title;

  @NotBlank(message = "Job description is required")
  @Size(min = 50, max = 5000, message = "Description must be between 50 and 5000 characters")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @NotBlank(message = "Company name is required")
  @Column(nullable = false)
  private String company;

  @NotBlank(message = "Location is required")
  @Column(nullable = false)
  private String location;

  @Positive(message = "Salary must be positive")
  @Column(precision = 10, scale = 2)
  private BigDecimal salary;

  @NotNull(message = "Job type is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "job_type", nullable = false)
  private JobType jobType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private JobStatus status = JobStatus.OPEN;

  @Column(columnDefinition = "TEXT")
  private String requirements;

  @Column(columnDefinition = "TEXT")
  private String benefits;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recruiter_id", nullable = false)
  private User recruiter;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Application> applications = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /**
   * Returns the number of applications for this job.
   */
  public int getApplicationCount() {
    return applications != null ? applications.size() : 0;
  }
}
