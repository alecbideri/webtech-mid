package com.alec.FindJobApp.dto;

import com.alec.FindJobApp.model.JobStatus;
import com.alec.FindJobApp.model.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

  private Long id;
  private String title;
  private String description;
  private String company;
  private String location;
  private BigDecimal salary;
  private JobType jobType;
  private JobStatus status;
  private String requirements;
  private String benefits;
  private Long recruiterId;
  private String recruiterName;
  private int applicationCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
