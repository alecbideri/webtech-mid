package com.alec.FindJobApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = { "job_id", "seeker_id" }))
public class JobApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", nullable = false)
  private Job job;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seeker_id", nullable = false)
  private User seeker;

  @Enumerated(EnumType.STRING)
  private ApplicationStatus status;

  private LocalDateTime appliedAt;

  @PrePersist
  protected void onCreate() {
    appliedAt = LocalDateTime.now();
    if (status == null) {
      status = ApplicationStatus.APPLIED;
    }
  }
}
