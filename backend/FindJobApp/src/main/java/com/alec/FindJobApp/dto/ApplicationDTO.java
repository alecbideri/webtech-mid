package com.alec.FindJobApp.dto;

import com.alec.FindJobApp.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

  private Long id;
  private Long jobId;
  private String jobTitle;
  private String company;
  private Long seekerId;
  private String seekerName;
  private String seekerEmail;
  private ApplicationStatus status;
  private String coverLetter;
  private String resumeFilename;
  private LocalDateTime appliedAt;
  private LocalDateTime reviewedAt;
  private String reviewerNotes;
}
