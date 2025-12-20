package com.alec.FindJobApp.dto;

import com.alec.FindJobApp.model.JobStatus;
import com.alec.FindJobApp.model.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

  @NotBlank(message = "Job title is required")
  @Size(min = 3, max = 100)
  private String title;

  @NotBlank(message = "Job description is required")
  @Size(min = 50, max = 5000)
  private String description;

  @NotBlank(message = "Company name is required")
  private String company;

  @NotBlank(message = "Location is required")
  private String location;

  @Positive(message = "Salary must be positive")
  private BigDecimal salary;

  @NotNull(message = "Job type is required")
  private JobType jobType;

  private JobStatus status;

  private String requirements;

  private String benefits;
}
