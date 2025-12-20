package com.alec.FindJobApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

  private Long id;
  private Long userId;
  private String bio;
  private String phoneNumber;
  private String skills;
  private String experience;
  private String education;
  private String linkedinUrl;
  private String portfolioUrl;
  private String companyName;
  private String companyDescription;
}
