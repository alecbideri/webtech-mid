package com.alec.FindJobApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for verifying OTP during 2FA login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {

  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "OTP code is required")
  @Size(min = 6, max = 6, message = "OTP must be 6 digits")
  private String otpCode;
}
