package com.alec.FindJobApp.dto;

import com.alec.FindJobApp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses containing JWT token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private String token;
  private String type = "Bearer";
  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private Role role;
}
