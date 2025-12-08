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
@Table(name = "jobs")
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, length = 2000)
  private String description;

  @Column(nullable = false)
  private String location;

  @Column(nullable = false)
  private String type; // e.g., Full-time, Part-time

  private LocalDateTime postedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @PrePersist
  protected void onCreate() {
    postedAt = LocalDateTime.now();
  }
}
