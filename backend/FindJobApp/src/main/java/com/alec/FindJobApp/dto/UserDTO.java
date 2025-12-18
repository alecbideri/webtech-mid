package com.alec.FindJobApp.dto;

import com.alec.FindJobApp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user data responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Role role;
  private Boolean isActive;
  private LocalDateTime createdAt;
}
