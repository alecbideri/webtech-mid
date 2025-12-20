package com.alec.FindJobApp.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

  @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
  private String firstName;

  @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
  private String lastName;

  @Size(max = 20, message = "Phone must be at most 20 characters")
  private String phone;

  @Size(max = 1000, message = "Bio must be at most 1000 characters")
  private String bio;

  @Size(max = 255, message = "Company must be at most 255 characters")
  private String company;

  @Size(max = 255, message = "Location must be at most 255 characters")
  private String location;

  @Size(max = 500, message = "LinkedIn URL must be at most 500 characters")
  private String linkedinUrl;
}
