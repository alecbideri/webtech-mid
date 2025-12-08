package com.alec.FindJobApp.dto;

import com.alec.FindJobApp.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
  private String username;
  private String email;
  private String password;
  private String name;
  private Role role;
}
